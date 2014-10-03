/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;

/**
 *
 * @author tuc53509
 */
public class CollisionControl extends RigidBodyControl implements PhysicsCollisionListener {

    private Game game;

    public CollisionControl(Game game) {
        this.game = game;
    }

    public void collision(PhysicsCollisionEvent event) {
        if (event.getNodeA().getName().equals("Ball") || event.getNodeB().getName().equals("Ball")) {
            if (event.getNodeA().getName().equals("Hoop") || event.getNodeB().getName().equals("Hoop")) {
                game.goalReached();
            }
        }
    }
}
