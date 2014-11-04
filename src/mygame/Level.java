/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import java.util.ArrayList;

/**
 *
 * @author Zack
 */
public class Level {
    
    private Game game;
    private Spatial model;
    private int levelNum;
    private int listIndex = 0;
    private Node nodeUp, nodeDown, nodeUpDown;
    private Vector3f playerStartPos, ballStartPos;
    private Geometry geomHoop, geomHighGravSwitch, geomLowGravSwitch, geomNormGravSwitch, geomRevGravSwitch, geomGlassBox, geomLavaBox, geomSurfUp, geomSurfDown;
    private RigidBodyControl landscape, hoopPhys, highGravSwitchPhys, lowGravSwitchPhys, normGravSwitchPhys, revGravSwitchPhys, glassPhys, lavaPhys;
    private final Cylinder hoop = new Cylinder(30, 30, 1.5f, 0.1f, true);
    private ArrayList<RigidBodyControl> physList = new ArrayList<RigidBodyControl>();
    private ArrayList<Geometry> geomList = new ArrayList<Geometry>();
    
    public Level(Game game) {
        this.game = game;
        nodeUp = new Node();
        nodeDown = new Node();
        nodeUpDown = new Node();
        levelNum = 1;
        loadLevel();
    }
    
    public void loadLevel() {
        switch (levelNum) {
            case 1:
                //Level 1
                model = game.getMain().getAssetManager().loadModel("Scenes/LevelOne.j3o");
                playerStartPos = new Vector3f(10, 5, -10);
                game.getPlayer().setStart(playerStartPos);
                ballStartPos = new Vector3f(35.0f, 15.0f, -20.0f);
                game.getBall().setPhysicsLocation(ballStartPos);
                createHoop(40.0f, 13.0f, -15.0f, 90.0f);
                break;
            default:
                //test level
                model = game.getMain().getAssetManager().loadModel("Scenes/TestScene.j3o");
                playerStartPos = new Vector3f(0, 5, 20);
                game.getPlayer().setStart(playerStartPos);
                ballStartPos = new Vector3f(0, 5, 10);
                game.getBall().setPhysicsLocation(ballStartPos);
                //createHoop(0.0f, 5.0f, 0.0f, 0.0f);
                createNormGravSwitch(15.0f, 5.0f, 0.0f, 0.0f);
                createNormGravSwitch(15.0f, 60.0f, 0.0f, 0.0f);
                createHighGravSwitch(5.0f, 5.0f, 0.0f, 0.0f);
                createLowGravSwitch(10.0f, 5.0f, 0.0f, 0.0f);
                createRevGravSwitch(20.0f, 5.0f, 0.0f, 0.0f);
                createGlassBlock(100.0f, 0.3f, 100.0f, 0.0f, 65.0f, 0.0f);
                createLavaBlock(20.0f, 10.0f, 20.0f, -80.0f, -7.0f, 75.0f);
                createUpDownBlock();
                //createUpBlock();
                //createDownBlock();
                break;
        }
        model.setName("Level");
        model.setLocalScale(5f);
        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape((Node) model);
        game.getMain().getRootNode().attachChild(model);
        landscape = new RigidBodyControl(sceneShape, 0);
        model.addControl(landscape);
        game.getBulletAppState().getPhysicsSpace().add(landscape);
    }
    
    public void nextLevel() {
        
        clearLevel();
        ++levelNum;
        loadLevel();
    }
    
    //checks each of the possible added geometry's and if they are there destroys them and the physics frame
    
    public void clearLevel(){
        if(game.getMain().getRootNode().hasChild(model)){
            game.getMain().getRootNode().detachChild(model);
            game.getBulletAppState().getPhysicsSpace().removeCollisionObject(landscape);
        }
        while(listIndex > 0){
            listIndex--;
            game.getMain().getRootNode().detachChild(geomList.get(listIndex));
            game.getBulletAppState().getPhysicsSpace().removeCollisionObject(physList.get(listIndex));   
        }
        physList.clear();
        geomList.clear();
    }
    
    /*creates a completion hoop at specified coordinates
     For the rotation field just fill in 90.0f or 0.0.f, one makes the hoop face north/south the other east/west*/
    public void createHoop(float x, float y, float z, float rotation){
        listIndex++;
        geomList.add(geomHoop = new Geometry("Hoop", hoop));
        geomHoop.setMaterial(game.getMain().magenta);
        geomHoop.setLocalTranslation(x, y, z);
        geomHoop.rotate(0.0f, rotation * FastMath.DEG_TO_RAD, 0.0f);
        game.getMain().getRootNode().attachChild(geomHoop);
        physList.add(hoopPhys = new RigidBodyControl(0.0f));
        geomHoop.addControl(hoopPhys);
        game.getBulletAppState().getPhysicsSpace().add(hoopPhys);
    }
    
    //creates a high gravity switch at specified coordinates
    public void createHighGravSwitch(float x, float y, float z, float rotation){
        listIndex++;
        geomList.add(geomHighGravSwitch = new Geometry("HighGravSwitch", hoop));
        geomHighGravSwitch.setMaterial(game.getMain().yellow);
        geomHighGravSwitch.setLocalTranslation(x, y, z);
        geomHighGravSwitch.rotate(0.0f, rotation * FastMath.DEG_TO_RAD, 0.0f);
        game.getMain().getRootNode().attachChild(geomHighGravSwitch);
        physList.add(highGravSwitchPhys = new RigidBodyControl(0.0f));
        geomHighGravSwitch.addControl(highGravSwitchPhys);
        game.getBulletAppState().getPhysicsSpace().add(highGravSwitchPhys);
    }
    
    //creates a low gravity switch at specified coordinates
    public void createLowGravSwitch(float x, float y, float z, float rotation){
        listIndex++;
        geomList.add(geomLowGravSwitch = new Geometry("LowGravSwitch", hoop));
        geomLowGravSwitch.setMaterial(game.getMain().red);
        geomLowGravSwitch.setLocalTranslation(x, y, z);
        geomLowGravSwitch.rotate(0.0f, rotation * FastMath.DEG_TO_RAD, 0.0f);
        game.getMain().getRootNode().attachChild(geomLowGravSwitch);
        physList.add(lowGravSwitchPhys = new RigidBodyControl(0.0f));
        geomLowGravSwitch.addControl(lowGravSwitchPhys);
        game.getBulletAppState().getPhysicsSpace().add(lowGravSwitchPhys);
    }
    
    //creates a normal gravity switch at specified coordinates
    public void createNormGravSwitch(float x, float y, float z, float rotation){
        listIndex++;
        geomList.add(geomNormGravSwitch = new Geometry("NormGravSwitch", hoop));
        geomNormGravSwitch.setMaterial(game.getMain().green);
        geomNormGravSwitch.setLocalTranslation(x, y, z);
        geomNormGravSwitch.rotate(0.0f, rotation * FastMath.DEG_TO_RAD, 0.0f);
        game.getMain().getRootNode().attachChild(geomNormGravSwitch);
        physList.add(normGravSwitchPhys = new RigidBodyControl(0.0f));
        geomNormGravSwitch.addControl(normGravSwitchPhys);
        game.getBulletAppState().getPhysicsSpace().add(normGravSwitchPhys);
    }
    
    //creates a reverse gravity switch at specified coordinates
    public void createRevGravSwitch(float x, float y, float z, float rotation){
        listIndex++;
        geomList.add(geomRevGravSwitch = new Geometry("RevGravSwitch", hoop));
        geomRevGravSwitch.setMaterial(game.getMain().white);
        geomRevGravSwitch.setLocalTranslation(x, y, z);
        geomRevGravSwitch.rotate(0.0f, rotation * FastMath.DEG_TO_RAD, 0.0f);
        game.getMain().getRootNode().attachChild(geomRevGravSwitch);
        physList.add(revGravSwitchPhys = new RigidBodyControl(0.0f));
        geomRevGravSwitch.addControl(revGravSwitchPhys);
        game.getBulletAppState().getPhysicsSpace().add(revGravSwitchPhys);
    }
    
    //creates a glass block at specified coordinates
    public void createGlassBlock(float l, float h, float w, float x, float y, float z){
        listIndex++;
        geomList.add(geomGlassBox = new Geometry("GlassCeiling", new Box(l, h, w)));
        Material glassMat = new Material(game.getMain().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        glassMat.setTexture("ColorMap", game.getMain().getAssetManager().loadTexture("Materials/glass.png"));
        glassMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        geomGlassBox.setQueueBucket(RenderQueue.Bucket.Transparent);
        geomGlassBox.setLocalTranslation(new Vector3f(x, y, z));
        geomGlassBox.setMaterial(glassMat);
        game.getMain().getRootNode().attachChild(geomGlassBox);
        physList.add(glassPhys = new RigidBodyControl(0.0f));
        geomGlassBox.addControl(glassPhys);
        game.getBulletAppState().getPhysicsSpace().add(glassPhys);
    }
    
    //creates a lava block at specified coordinates
    public void createLavaBlock(float l, float h, float w, float x, float y, float z){
        listIndex++;
        geomList.add(geomLavaBox = new Geometry("Lava", new Box(l, h, w)));
        Material lavaMat = new Material(game.getMain().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        lavaMat.setTexture("ColorMap", game.getMain().getAssetManager().loadTexture("Materials/lava.jpg"));
        geomLavaBox.setMaterial(lavaMat);
        geomLavaBox.setLocalTranslation(new Vector3f(x, y, z));
        game.getMain().getRootNode().attachChild(geomLavaBox);
        physList.add(lavaPhys = new RigidBodyControl(0.0f));
        geomLavaBox.addControl(lavaPhys);
        game.getBulletAppState().getPhysicsSpace().add(geomLavaBox);
    }
    
    public void createUpDownBlock(/*float l, float h, float w, float x, float y, float z*/){
        geomSurfUp = new Geometry("SurfUp", new Box(5f, .5f, 2f));
        geomSurfUp.setMaterial(game.getMain().white);
        geomSurfUp.setLocalTranslation(new Vector3f(-15f,12f,69f));
        nodeUpDown.attachChild(geomSurfUp);
        RigidBodyControl surfUpPhysics = new RigidBodyControl(0.0f);
        geomSurfUp.addControl(surfUpPhysics);
        game.getBulletAppState().getPhysicsSpace().add(surfUpPhysics);
    }
    
    //still in development don't use
    /*public void createUpBlock(float l, float h, float w, float x, float y, float z){
        geomSurfUp = new Geometry("SurfUp", new Box(5f, .1f, 2f));
        geomSurfUp.setMaterial(game.getMain().white);
        geomSurfUp.setLocalTranslation(new Vector3f(-15f,12f,69f));
        surfUp.attachChild(geomSurfUp);
        RigidBodyControl surfUpPhysics = new RigidBodyControl(0.0f);
        geomSurfUp.addControl(surfUpPhysics);
        game.getBulletAppState().getPhysicsSpace().add(surfUpPhysics);
    }*/
    
    //still in development don't use
    /*public void createDownBlock(float l, float h, float w, float x, float y, float z){
        geomSurfDown = new Geometry("SurfDown",new Box(5f, .1f, 2f));
        geomSurfDown.setMaterial(game.getMain().white);
        geomSurfDown.setLocalTranslation(new Vector3f(-15f,12f,69f));
        surfDown.attachChild(geomSurfDown);
        RigidBodyControl surfDownPhysics = new RigidBodyControl(0f);
        geomSurfDown.addControl(surfDownPhysics);
        game.getBulletAppState().getPhysicsSpace().add(surfDownPhysics);
    }*/
    
    public Spatial getModel() {
        return model;
    }
    
    public Vector3f getPlayerStart() {
        return playerStartPos;
    }
    
    public Vector3f getBallStart() {
        return ballStartPos;
    }
    
    public Node getUpDownNode(){
        return nodeUpDown;
    }
    
    public Geometry getUpGeom(){
        return geomSurfUp;
    }
}
