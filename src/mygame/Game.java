/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;

/**
 * Spawns the player, ball, goal hoop, switches, surfaces, and level. Contains 
 * game logic and states.
 * 
 * @author Zack Hunter
 * @author Jake DiIenno
 * @author Eric Bullock
 * @author Michael Fatal
 * @version %I% %G%
 * @see AbstractAppState
 * @since 2.0
 */
class Game extends AbstractAppState implements ActionListener {
    
   /**
    * copy of main class
    */
    private Main main;
   /**
    * copy of state manager
    */
    private AppStateManager asm;
   /**
    * the bullet physics engine
    */
    private BulletAppState bulletAppState;
   /**
    * collision control to detect physical collisions
    */
    private CollisionControl collCon;
   /**
    * Spatial for the first level
    */
    private Level level;
   /**
    * Geometries for all
    */
    private Geometry geomBall;
   /**
    * physics of the ball Geometry
    */
    private RigidBodyControl ballPhys;
   /**
    * text object for the display of text when the level is complete
    */
    private BitmapText completeText;
   /**
    * boolean for whether the goal has been reached or not
    */
    private boolean goalReached;
   /**
    * Player object containing information about the player
    */
    private Player player;
   /**
    * direction the player is walking in a Vector3f
    */
    private Vector3f walkDirection = new Vector3f();
   /**
    * booleans for the direction the play is walking
    */
    private boolean left = false, right = false, up = false, down = false, shiftUp=false, shiftDown=false;
    // states
   /**
    * the current state
    */
    private int state;
   /**
    * waiting for the game to start
    */
    private final int WAIT = 0;
   /**
    * the game is running
    */
    private final int RUNNING = 1;
   /**
    * the game is paused
    */
    private final int PAUSED = 2;
   /**
    * the game has ended
    */
    private final int END = 3;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        main = (Main) app;
        asm = stateManager;
        goalReached = false;
        
        // keys
        main.getInputManager().deleteMapping("Start");
        main.getInputManager().addMapping("Left", new KeyTrigger(KeyInput.KEY_A), new KeyTrigger(KeyInput.KEY_LEFT));
        main.getInputManager().addMapping("Right", new KeyTrigger(KeyInput.KEY_D), new KeyTrigger(KeyInput.KEY_RIGHT));
        main.getInputManager().addMapping("Up", new KeyTrigger(KeyInput.KEY_W), new KeyTrigger(KeyInput.KEY_UP));
        main.getInputManager().addMapping("Down", new KeyTrigger(KeyInput.KEY_S), new KeyTrigger(KeyInput.KEY_DOWN));
        main.getInputManager().addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        main.getInputManager().addMapping("ShiftUp", new KeyTrigger(KeyInput.KEY_Q));
        main.getInputManager().addMapping("ShiftDown", new KeyTrigger(KeyInput.KEY_E));
        main.getInputManager().addListener(this, "Left", "Right", "Up", "Down", "Jump", "ShiftUp", "ShiftDown");
        // text
        main.getGuiNode().detachAllChildren();
        BitmapFont bmf = main.getAssetManager().loadFont("Interface/Fonts/Default.fnt");
        completeText = new BitmapText(bmf);
        completeText.setSize(bmf.getCharSet().getRenderedSize() * 5);
        completeText.setColor(ColorRGBA.White);
        completeText.setText("Complete!");
        AppSettings s = main.getSettings();
        completeText.setLocalTranslation((s.getWidth() - completeText.getLineWidth()) / 2,
                (s.getHeight() + completeText.getLineHeight()) / 2, 0f);
        
        // inits
        initGeometries();
        initPhysics();
        initPlayer();
        
        // load first level
        level = new Level(this);
    }

   /**
    * Determine what happens when a registered key is pressed. Controls player
    * movement.
    * 
    * @param name a String for the name given to the key pressed
    * @param isPressed a boolean to determine if a key is pressed
    * @param tpf a float containing the time per frame 
    */
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("Left")) {
            if (isPressed) {
                left = true;
            } else {
                left = false;
            }
        } else if (name.equals("Right")) {
            if (isPressed) {
                right = true;
            } else {
                right = false;
            }
        } else if (name.equals("Up")) {
            if (isPressed) {
                up = true;
            } else {
                up = false;
            }
        } else if (name.equals("Down")) {
            if (isPressed) {
                down = true;
            } else {
                down = false;
            }
        } else if (name.equals("Jump")) {
            player.getControl().jump();
        }
        else if (name.equals("ShiftUp")) {
            if (isPressed) {
                shiftUp = true;
            }
        } else if (name.equals("ShiftDown")) {
            if (isPressed) {
                shiftDown = true;
            }
        }
   }
    
    @Override
    public void update(float tpf) {
        // walking
        Vector3f camDir = main.getCamera().getDirection().clone().multLocal(10f);
        camDir.setY(0);
        Vector3f camLeft = main.getCamera().getLeft().clone().multLocal(10f);
        walkDirection.set(0, 0, 0);
        if (left) {
            walkDirection.addLocal(camLeft);
        }
        if (right) {
            walkDirection.addLocal(camLeft.negate());
        }
        if (up) {
            walkDirection.addLocal(camDir);
        }
        if (down) {
            walkDirection.addLocal(camDir.negate());
        }
        player.getControl().setWalkDirection(walkDirection);
        main.getCamera().setLocation(player.getWorldTranslation().add(0, 3, 0));
        if(shiftUp)
        {   
            shiftUp = false;
            level.moveSurfaceUp();
            
        }
        
        //shift down
        if(shiftDown)
        {
            shiftDown = false;
            level.moveSurfaceDown();
            
        }
        // end game
        if (goalReached) {
            goalReached = false;
            level.nextLevel();
        }
        
    }

   /**
    * Initialize general geometries for game elements. Creates ball, hoop, 
    * gravity switches, lava block, and glass ceiling.
    */
    public void initGeometries() {
        // create ball
        Sphere ball = new Sphere(20, 20, 1.0f);
        geomBall = new Geometry("Ball", ball);
        geomBall.setMaterial(main.gold);
        main.getRootNode().attachChild(geomBall);
    }

   /**
    * Initialize the player using the Player class and add it to the root node.
    * 
    * @see Player
    */
    public void initPlayer() {
        player = new Player(this);
        main.getRootNode().attachChild(player);
    }
    
   /**
    * Initialize the bullet physics engine and all physical elements. Starts 
    * the bullet engine and adds appropriate physics to each geometry.
    * 
    * @see com.jme3.bullet.control.RigidBodyControl
    * @see com.jme3.bullet.BulletAppState
    */
    public void initPhysics() {
        bulletAppState = new BulletAppState();
        asm.attach(bulletAppState);
        //bulletAppState.setDebugEnabled(true);
        
        // set ball physics
        ballPhys = new RigidBodyControl(1.0f);
        geomBall.addControl(ballPhys);
        bulletAppState.getPhysicsSpace().add(ballPhys);
        
        // set collision control
        collCon = new CollisionControl(this);
        bulletAppState.getPhysicsSpace().addCollisionListener(collCon);
    }
    
   /**
    * Returns true if the goal is reached, false if it is not.
    * 
    * @return boolean for whether or not the goal has been reached
    */
    public boolean isGoalReached() {
        return goalReached;
    }
    
   /**
    * Sets the goalReached boolean to true.
    */
    public void goalReached() {
        goalReached = true;
        ballPhys.setPhysicsRotation(Quaternion.ZERO);
        ballPhys.setPhysicsLocation(new Vector3f(1000.0f, 1000.0f, 1000.0f));
    }
    
   /**
    * Returns the current instance of Main.
    * 
    * @return the Main currently used
    */
    public Main getMain() {
        return main;
    }

   /**
    * Returns the bulletAppState
    * 
    * @return the instance of the bullet engine in use
    */
    public BulletAppState getBulletAppState() {
        return bulletAppState;
    }
    
   /**
    * Returns the Player class object.
    * 
    * @return the Player class object
    */
    public Player getPlayer() {
        return player;
    }
    
    public RigidBodyControl getBall() {
        return ballPhys;
    }
    
   /**
    * Resets all elements of the level to starting positions
    */
    public void resetLevel() {
        // reset player
        player.reset();
        // reset ball
        ballPhys.setPhysicsLocation(new Vector3f(level.getBallStart()));
        ballPhys.setLinearVelocity(Vector3f.ZERO);
        ballPhys.setAngularVelocity(Vector3f.ZERO);
        // reset normal gravity
        bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0, -9.81f, 0));
        level.resetLevel();
    }
}
