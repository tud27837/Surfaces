/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;

/**
 * Detects collisions between physical objects.
 * 
 * @author Zack Hunter
 * @author Eric Bullock
 * @version %I% %G%
 * @see PhysicsCollisionListener
 */
public class CollisionControl extends RigidBodyControl implements PhysicsCollisionListener {

   /**
    * copy of current Game object
    */
    private Game game;
    
   /**
    * Constructor. Copies <code>game</code> object.
    * @param game instance of the current game
    */
    public CollisionControl(Game game) {
        this.game = game;
    }

   /**
    * Detects collision between two physical objects.
    * 
    * @param event PhysicsCollisionEvent containing properties about the collision 
    *              including names, speed, etc.
    */
    public void collision(PhysicsCollisionEvent event) {
        if (event.getNodeA().getName().equals("Lava") || event.getNodeB().getName().equals("Lava")) {
            if (event.getNodeA().getName().equals("Player") || event.getNodeB().getName().equals("Player")) {
                game.resetLevel();
            }
            if (event.getNodeA().getName().equals("Ball") || event.getNodeB().getName().equals("Ball")) {
                game.resetLevel();
            }
        }
        
        if (event.getNodeA().getName().equals("Ball") || event.getNodeB().getName().equals("Ball")) {
            // finish hoop
            if (event.getNodeA().getName().equals("Hoop") || event.getNodeB().getName().equals("Hoop")) {
                    game.goalReached();   
            }
            
            // high gravity switch
            if (event.getNodeA().getName().equals("HighGravSwitch") || event.getNodeB().getName().equals("HighGravSwitch")) {
                game.getBulletAppState().getPhysicsSpace().setGravity(new Vector3f(0.0f, -100.0f, 0.0f));
            }
            // low gravity switch
            if (event.getNodeA().getName().equals("LowGravSwitch") || event.getNodeB().getName().equals("LowGravSwitch")) {
                game.getBulletAppState().getPhysicsSpace().setGravity(new Vector3f(0.0f, -5.0f, 0.0f));
            }
            // normal gravity switch
            if (event.getNodeA().getName().equals("NormGravSwitch") || event.getNodeB().getName().equals("NormGravSwitch")) {
                game.getBulletAppState().getPhysicsSpace().setGravity(new Vector3f(0.0f, -9.81f, 0.0f));
            }
            // reverse gravity switch
            if (event.getNodeA().getName().equals("RevGravSwitch") || event.getNodeB().getName().equals("RevGravSwitch")) {
                game.getBulletAppState().getPhysicsSpace().setGravity(new Vector3f(0.0f, 9.81f, 0.0f));
            }
        }
    }
}
