/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
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
    private Spatial levelOne;
   /**
    * Geometries for all
    */
    private Geometry geomBall, geomHoop, geomHighGravSwitch, geomLowGravSwitch, geomNormGravSwitch, geomRevGravSwitch, geomGlassBox, geomNormCeilingGravSwitch, geomLavaBox;
   /**
    * GhostControl to detect collisions with the lava block
    */
    private GhostControl lavaGhost;
   /**
    * physics of the ball Geometry
    */
    private RigidBodyControl globalBallControl;
   /**
    * Node for the lava block
    */
    private Node lavaNode;
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
    * BetterCharacterControl object to control the player
    */
    private BetterCharacterControl playerControl;
   /**
    * direction the player is walking in a Vector3f
    */
    private Vector3f walkDirection = new Vector3f();
   /**
    * booleans for the direction the play is walking
    */
    private boolean left = false, right = false, up = false, down = false;
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
        main.getInputManager().addListener(this, "Left", "Right", "Up", "Down", "Jump");
        
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
        initPlayer();
        initPhysics();
        
        // load level
        levelOne = main.getAssetManager().loadModel("Scenes/TestScene.j3o");
        levelOne.setName("Level");
        levelOne.setLocalScale(5f);
        loadLevel(levelOne);
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
            playerControl.jump();
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
        playerControl.setWalkDirection(walkDirection);
        main.getCamera().setLocation(player.getWorldTranslation().add(0, 3, 0));
        // end game
        if (goalReached) {
            main.getGuiNode().attachChild(completeText);
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
        geomBall.setLocalTranslation(0.0f, 5.0f, 10.0f);
        main.getRootNode().attachChild(geomBall);
        
        // create hoop
        Cylinder hoop = new Cylinder(30, 30, 1.5f, 0.1f, true);
        geomHoop = new Geometry("Hoop", hoop);
        geomHoop.setMaterial(main.magenta);
        geomHoop.setLocalTranslation(-11.0f, 5.0f, 0.0f);
        geomHoop.rotate(0.0f, 90.0f * FastMath.DEG_TO_RAD, 0.0f);
        main.getRootNode().attachChild(geomHoop);
        
        // create high gravity switch
        geomHighGravSwitch = new Geometry("HighGravSwitch", hoop);
        geomHighGravSwitch.setMaterial(main.gold);
        geomHighGravSwitch.setLocalTranslation(0.0f, 5.0f, -11.0f);
        main.getRootNode().attachChild(geomHighGravSwitch);
        
        // create low gravity switch
        geomLowGravSwitch = new Geometry("LowGravSwitch", hoop);
        geomLowGravSwitch.setMaterial(main.red);
        geomLowGravSwitch.setLocalTranslation(5.0f, 5.0f, -11.0f);
        main.getRootNode().attachChild(geomLowGravSwitch);
        
        // create normal gravity switch
        geomNormGravSwitch = new Geometry("NormGravSwitch", hoop);
        geomNormGravSwitch.setMaterial(main.green);
        geomNormGravSwitch.setLocalTranslation(10.0f, 5.0f, -11.0f);
        main.getRootNode().attachChild(geomNormGravSwitch);
        
        // create reverse gravity switch
        geomRevGravSwitch = new Geometry("RevGravSwitch", hoop);
        geomRevGravSwitch.setMaterial(main.white);
        geomRevGravSwitch.setLocalTranslation(15.0f, 5.0f, -11.0f);
        main.getRootNode().attachChild(geomRevGravSwitch);
        
        // create glass box that acts as a ceiling
        geomGlassBox = new Geometry("GlassCeiling", new Box(90.0f,.2f,85.0f));
        Material glassMat = new Material(main.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        glassMat.setTexture("ColorMap", main.getAssetManager().loadTexture("Materials/glass.png"));
        glassMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        geomGlassBox.setQueueBucket(RenderQueue.Bucket.Transparent);
        geomGlassBox.setLocalTranslation(new Vector3f(-10.0f,70.0f,10.0f));
        geomGlassBox.setMaterial(glassMat);
        main.getRootNode().attachChild(geomGlassBox);
        
        // create a block of lava over under the hole in the floor
        lavaNode = new Node("Lava Node");
        geomLavaBox = new Geometry("Lava", new Box(14,25,14));
        Material lavaMat = new Material(main.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        lavaMat.setTexture("ColorMap", main.getAssetManager().loadTexture("Materials/lava.jpg"));
        geomLavaBox.setMaterial(lavaMat);
        lavaNode.setLocalTranslation(new Vector3f(-88.0f, -25.0f,77.0f));
        lavaNode.attachChild(geomLavaBox);
        main.getRootNode().attachChild(lavaNode);
        
        // create normal gravity switch on ceiling in order to drop back down
        geomNormCeilingGravSwitch = new Geometry("NormGravSwitch", hoop);
        geomNormCeilingGravSwitch.setMaterial(main.green);
        geomNormCeilingGravSwitch.setLocalTranslation(10.0f, 67.0f, -11.0f);
        main.getRootNode().attachChild(geomNormCeilingGravSwitch);
        
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
        bulletAppState.setDebugEnabled(true);
        
        // set ball physics
        RigidBodyControl ballPhys = new RigidBodyControl(1.0f);
        globalBallControl = ballPhys;
        geomBall.addControl(ballPhys);
        bulletAppState.getPhysicsSpace().add(ballPhys);
        
        // set hoop physics
        RigidBodyControl hoopPhys = new RigidBodyControl(0.0f);
        geomHoop.addControl(hoopPhys);
        bulletAppState.getPhysicsSpace().add(hoopPhys);
        
        // set high gravity switch physics
        RigidBodyControl highGravSwitchPhys = new RigidBodyControl(0.0f);
        geomHighGravSwitch.addControl(highGravSwitchPhys);
        bulletAppState.getPhysicsSpace().add(highGravSwitchPhys);
        
        // set low gravity switch physics
        RigidBodyControl lowGravSwitchPhys = new RigidBodyControl(0.0f);
        geomLowGravSwitch.addControl(lowGravSwitchPhys);
        bulletAppState.getPhysicsSpace().add(lowGravSwitchPhys);
        
        // set normal gravity switch physics
        RigidBodyControl normGravSwitchPhys = new RigidBodyControl(0.0f);
        geomNormGravSwitch.addControl(normGravSwitchPhys);
        bulletAppState.getPhysicsSpace().add(normGravSwitchPhys);
        
        // set reverse gravity switch physics
        RigidBodyControl revGravSwitchPhys = new RigidBodyControl(0.0f);
        geomRevGravSwitch.addControl(revGravSwitchPhys);
        bulletAppState.getPhysicsSpace().add(revGravSwitchPhys);
        
        // make glass solid
        RigidBodyControl glassCeiling = new RigidBodyControl(0.0f);
        geomGlassBox.addControl(glassCeiling);
        bulletAppState.getPhysicsSpace().add(glassCeiling);
        
        // set normal gravity switch physics on the ceiling
        RigidBodyControl normCeilingGravSwitchPhys = new RigidBodyControl(0.0f);
        geomNormCeilingGravSwitch.addControl(normCeilingGravSwitchPhys);
        bulletAppState.getPhysicsSpace().add(normCeilingGravSwitchPhys);
        
        lavaGhost = new GhostControl(new BoxCollisionShape(new Vector3f(14,25,14)));  // a box-shaped ghost
        lavaNode.addControl(lavaGhost); 
        main.getRootNode().attachChild(lavaNode);
        bulletAppState.getPhysicsSpace().add(lavaGhost);
        
        // set collision control
        collCon = new CollisionControl(this);
        bulletAppState.getPhysicsSpace().addCollisionListener(collCon);
        
        // set player control and physics
        playerControl = new BetterCharacterControl(1.5f, 6f, 1f);
        player.addControl(playerControl);
        playerControl.setJumpForce(new Vector3f(0, 10, 0));
        playerControl.setGravity(new Vector3f(0, 100, 0));
        bulletAppState.getPhysicsSpace().add(playerControl);
    }
    
   /**
    * Returns the GhostControl object for the lava block.
    * 
    * @return the GhostControl object for the lava block
    */
    public GhostControl getLavaGhost(){
        return lavaGhost;
    }
    
   /**
    * Creates physics for a level and attaches the level to the root node.
    * 
    * @param level the Spacial for the level to be loaded
    */
    public void loadLevel(Spatial level) {
        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape((Node) level);
        main.getRootNode().attachChild(level);
        RigidBodyControl landscape = new RigidBodyControl(sceneShape, 0);
        level.addControl(landscape);
        bulletAppState.getPhysicsSpace().add(landscape);
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
    
   /**
    * Returns the playerControl object
    * @return the BetterCharacterControl object for the player
    */
    public BetterCharacterControl getPlayerControl(){
        return playerControl;
    }
    
   /**
    * Returns the physics of the ball.
    * @return the RigidBodyControl of the ball Geometry
    */
    public RigidBodyControl getBallControl(){
        return globalBallControl;
    }
}
