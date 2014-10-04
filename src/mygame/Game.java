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
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;

/**
 *
 * @author Zack
 */
class Game extends AbstractAppState implements ActionListener {
    
    private Main main;
    private AppStateManager asm;
    private BulletAppState bulletAppState;
    private CollisionControl collCon;
    private Spatial levelOne;
    private Geometry geomBall, geomHoop;
    private BitmapText completeText;
    private boolean goalReached;
    private Player player;
    private BetterCharacterControl playerControl;
    private Vector3f walkDirection = new Vector3f();
    private boolean left = false, right = false, up = false, down = false;
    // states
    private int state;
    private final int WAIT = 0;
    private final int RUNNING = 1;
    private final int PAUSED = 2;
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
        // end game
        if (goalReached) {
            main.getGuiNode().attachChild(completeText);
        }
        
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
    }

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
    }

    public void initPlayer() {
        player = new Player(this);
        main.getRootNode().attachChild(player);
    }
    
    public void initPhysics() {
        bulletAppState = new BulletAppState();
        asm.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0, -20, 0));
        bulletAppState.setDebugEnabled(true);
        
        // set ball physics
        RigidBodyControl ballPhys = new RigidBodyControl(1.0f);
        geomBall.addControl(ballPhys);
        bulletAppState.getPhysicsSpace().add(ballPhys);
        
        // set hoop physics
        RigidBodyControl hoopPhys = new RigidBodyControl(0.0f);
        geomHoop.addControl(hoopPhys);
        bulletAppState.getPhysicsSpace().add(hoopPhys);

        // set collision control
        collCon = new CollisionControl(this);
        bulletAppState.getPhysicsSpace().addCollisionListener(collCon);
        
        // set player control and physics
        playerControl = new BetterCharacterControl(1.5f, 6f, 1f);
        player.addControl(playerControl);
        playerControl.setJumpForce(new Vector3f(0, 12, 0));
        playerControl.setGravity(new Vector3f(0, 100, 0));
        bulletAppState.getPhysicsSpace().add(playerControl);
    }
    
    public void loadLevel(Spatial level) {
        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape((Node) level);
        main.getRootNode().attachChild(level);
        RigidBodyControl landscape = new RigidBodyControl(sceneShape, 0);
        level.addControl(landscape);
        bulletAppState.getPhysicsSpace().add(landscape);
    }
    
    public boolean isGoalReached() {
        return goalReached;
    }
    
    public void goalReached() {
        goalReached = true;
    }
    
    public Main getMain() {
        return main;
    }

    public BulletAppState getBulletAppState() {
        return bulletAppState;
    }
    
    public Player getPlayer() {
        return player;
    }
}
