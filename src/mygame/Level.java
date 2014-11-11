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
    private int upDownIndex = 0;
    private int upIndex = 0;
    private int downIndex = 0;
    private Node nodeUp, nodeDown, nodeUpDown;
    private Vector3f playerStartPos, ballStartPos;
    private Geometry geom;
    private RigidBodyControl landscape, phys, upDownPhys, upPhys, downPhys;
    private final Cylinder hoop = new Cylinder(30, 30, 1.5f, 0.1f, true);
    private ArrayList<Vector3f> upDownPositionList = new ArrayList<Vector3f>();
    private ArrayList<Vector3f> upPositionList = new ArrayList<Vector3f>();
    private ArrayList<Vector3f> downPositionList = new ArrayList<Vector3f>();
    private ArrayList<RigidBodyControl> physList = new ArrayList<RigidBodyControl>();
    private ArrayList<RigidBodyControl> upDownPhysList = new ArrayList<RigidBodyControl>();
    private ArrayList<RigidBodyControl> upPhysList = new ArrayList<RigidBodyControl>();
    private ArrayList<RigidBodyControl> downPhysList = new ArrayList<RigidBodyControl>();
    private ArrayList<Geometry> geomList = new ArrayList<Geometry>();
    
    public Level(Game game) {
        this.game = game;
        nodeUp = new Node();
        nodeDown = new Node();
        nodeUpDown = new Node();
        game.getMain().getRootNode().attachChild(nodeUp);
        game.getMain().getRootNode().attachChild(nodeDown);
        game.getMain().getRootNode().attachChild(nodeUpDown);
        levelNum = 1;
        loadLevel();
    }
    
    public void loadLevel() {
        switch (levelNum) {
            case 1:
                //Level 1
                model = game.getMain().getAssetManager().loadModel("Scenes/LevelOne.j3o");
                playerStartPos = new Vector3f(20, 3, -35);
                game.getPlayer().setStart(playerStartPos);
                ballStartPos = new Vector3f(50.0f, 13.0f, -15.0f);
                game.getBall().setPhysicsLocation(ballStartPos);
                createHoop(89.0f, 13.0f, -65.0f, 0.0f);
                createDownBlock(15.0f, 16.0f, 15.0f, 17.0f, -5.0f, -10.0f);                 
            case 2:
                //Level 2
                model = game.getMain().getAssetManager().loadModel("Scenes/Level2.mesh.xml");
                playerStartPos = new Vector3f(0.0f, 10.0f, 40.0f);
                game.getPlayer().setStart(playerStartPos);
                ballStartPos = new Vector3f(0.0f, 5.0f, 30.0f);
                game.getBall().setPhysicsLocation(ballStartPos);
                createHoop(0.0f, 2.0f, -50.0f, 0.0f);
                createGlassBlock(1.0f, 0.2f, 16.2f, 0.0f, 0.5f, 0.0f);
                createLavaBlock(7.5f, 1.0f, 16.2f, 0.0f, -1.0f, 0.0f);
                break;
            case 3:
                //Level 3
                model = game.getMain().getAssetManager().loadModel("Scenes/Level3.mesh.xml");
                playerStartPos = new Vector3f(0, 0, 0);
                game.getPlayer().setStart(playerStartPos);
                ballStartPos = new Vector3f(0f, 10f, -115f);
                game.getBall().setPhysicsLocation(ballStartPos);
                createRevGravSwitch(0f, 5f, -126f, 0f);
                createNormGravSwitch(0f, 110f, -238f, 0f);
                createHoop(0f, -4f, -248f, 0f);
                break;
            default:
                //test level
                model = game.getMain().getAssetManager().loadModel("Scenes/TestScene.j3o");
                playerStartPos = new Vector3f(0, 5, 20);
                game.getPlayer().setStart(playerStartPos);
                ballStartPos = new Vector3f(0, 5, 10);
                game.getBall().setPhysicsLocation(ballStartPos);
                createHoop(0.0f, 5.0f, 0.0f, 0.0f);
                createNormGravSwitch(15.0f, 5.0f, 0.0f, 0.0f);
                createNormGravSwitch(15.0f, 60.0f, 0.0f, 0.0f);
                createHighGravSwitch(5.0f, 5.0f, 0.0f, 0.0f);
                createLowGravSwitch(10.0f, 5.0f, 0.0f, 0.0f);
                createRevGravSwitch(20.0f, 5.0f, 0.0f, 0.0f);
                createGlassBlock(100.0f, 0.3f, 100.0f, 0.0f, 65.0f, 0.0f);
                createLavaBlock(25.0f, 10.0f, 25.0f, -80.0f, -7.0f, 75.0f);
                createDownBlock(5f, .5f, 10f, -20.0f, 20.0f, -20.0f);
                createDownBlock(5f, .5f, 10f, -5.0f, 20.0f, -20.0f);
                createUpDownBlock(5f, .5f, 10f, 10.0f, 5.0f, -20.0f);
                createUpDownBlock(5f, .5f, 10f, 25.0f, 5.0f, -20.0f);
                createUpBlock(5f, .5f, 10f, 40.0f, 5.0f, -20.0f);
                createUpBlock(5f, .5f, 10f, 55.0f, 5.0f, -20.0f);
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
    
    public void resetLevel(){
        clearLevel();
        loadLevel();
    }
    
    //checks each of the possible added geometry's and if they are there destroys them and the physics frame
    
    public void clearLevel(){
        if(game.getMain().getRootNode().hasChild(model)){
            game.getMain().getRootNode().detachChild(model);
            game.getBulletAppState().getPhysicsSpace().removeCollisionObject(landscape);
        }
        clearMovableSurfaces();
        clearLists();
    }
    
    /*creates a completion hoop at specified coordinates
     For the rotation field just fill in 90.0f or 0.0.f, one makes the hoop face north/south the other east/west*/
    public void createHoop(float x, float y, float z, float rotation){
        listIndex++;
        geomList.add(geom = new Geometry("Hoop", hoop));
        geom.setMaterial(game.getMain().magenta);
        geom.setLocalTranslation(x, y, z);
        geom.rotate(0.0f, rotation * FastMath.DEG_TO_RAD, 0.0f);
        game.getMain().getRootNode().attachChild(geom);
        physList.add(phys = new RigidBodyControl(0.0f));
        geom.addControl(phys);
        game.getBulletAppState().getPhysicsSpace().add(phys);
    }
    
    //creates a high gravity switch at specified coordinates
    public void createHighGravSwitch(float x, float y, float z, float rotation){
        listIndex++;
        geomList.add(geom = new Geometry("HighGravSwitch", hoop));
        geom.setMaterial(game.getMain().yellow);
        geom.setLocalTranslation(x, y, z);
        geom.rotate(0.0f, rotation * FastMath.DEG_TO_RAD, 0.0f);
        game.getMain().getRootNode().attachChild(geom);
        physList.add(phys = new RigidBodyControl(0.0f));
        geom.addControl(phys);
        game.getBulletAppState().getPhysicsSpace().add(phys);
    }
    
    //creates a low gravity switch at specified coordinates
    public void createLowGravSwitch(float x, float y, float z, float rotation){
        listIndex++;
        geomList.add(geom = new Geometry("LowGravSwitch", hoop));
        geom.setMaterial(game.getMain().red);
        geom.setLocalTranslation(x, y, z);
        geom.rotate(0.0f, rotation * FastMath.DEG_TO_RAD, 0.0f);
        game.getMain().getRootNode().attachChild(geom);
        physList.add(phys = new RigidBodyControl(0.0f));
        geom.addControl(phys);
        game.getBulletAppState().getPhysicsSpace().add(phys);
    }
    
    //creates a normal gravity switch at specified coordinates
    public void createNormGravSwitch(float x, float y, float z, float rotation){
        listIndex++;
        geomList.add(geom = new Geometry("NormGravSwitch", hoop));
        geom.setMaterial(game.getMain().green);
        geom.setLocalTranslation(x, y, z);
        geom.rotate(0.0f, rotation * FastMath.DEG_TO_RAD, 0.0f);
        game.getMain().getRootNode().attachChild(geom);
        physList.add(phys = new RigidBodyControl(0.0f));
        geom.addControl(phys);
        game.getBulletAppState().getPhysicsSpace().add(phys);
    }
    
    //creates a reverse gravity switch at specified coordinates
    public void createRevGravSwitch(float x, float y, float z, float rotation){
        listIndex++;
        geomList.add(geom = new Geometry("RevGravSwitch", hoop));
        geom.setMaterial(game.getMain().white);
        geom.setLocalTranslation(x, y, z);
        geom.rotate(0.0f, rotation * FastMath.DEG_TO_RAD, 0.0f);
        game.getMain().getRootNode().attachChild(geom);
        physList.add(phys = new RigidBodyControl(0.0f));
        geom.addControl(phys);
        game.getBulletAppState().getPhysicsSpace().add(phys);
    }
    
    //creates a glass block at specified coordinates
    public void createGlassBlock(float l, float h, float w, float x, float y, float z){
        listIndex++;
        geomList.add(geom = new Geometry("GlassCeiling", new Box(l, h, w)));
        Material glassMat = new Material(game.getMain().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        glassMat.setTexture("ColorMap", game.getMain().getAssetManager().loadTexture("Materials/glass.png"));
        glassMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        geom.setQueueBucket(RenderQueue.Bucket.Transparent);
        geom.setLocalTranslation(new Vector3f(x, y, z));
        geom.setMaterial(glassMat);
        game.getMain().getRootNode().attachChild(geom);
        physList.add(phys = new RigidBodyControl(0.0f));
        geom.addControl(phys);
        game.getBulletAppState().getPhysicsSpace().add(phys);
    }
    
    //creates a lava block at specified coordinates
    public void createLavaBlock(float l, float h, float w, float x, float y, float z){
        listIndex++;
        geomList.add(geom = new Geometry("Lava", new Box(l, h, w)));
        Material lavaMat = new Material(game.getMain().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        lavaMat.setTexture("ColorMap", game.getMain().getAssetManager().loadTexture("Materials/lava.jpg"));
        geom.setMaterial(lavaMat);
        geom.setLocalTranslation(new Vector3f(x, y, z));
        game.getMain().getRootNode().attachChild(geom);
        physList.add(phys = new RigidBodyControl(0.0f));
        geom.addControl(phys);
        game.getBulletAppState().getPhysicsSpace().add(phys);
    }
    
    public void createUpDownBlock(float l, float h, float w, float x, float y, float z){
        Vector3f currentUpDownPosition;
        upDownIndex++;
        geom = new Geometry("SurfUpDown", new Box(l, h, w));
        geom.setMaterial(game.getMain().white);
        upDownPositionList.add(currentUpDownPosition = new Vector3f(x, y, z));
        geom.setLocalTranslation(currentUpDownPosition);
        nodeUpDown.attachChild(geom);
        upDownPhysList.add(upDownPhys = new RigidBodyControl(0.0f));
        geom.addControl(upDownPhys);
        game.getBulletAppState().getPhysicsSpace().add(upDownPhys);
    }
    
    public void createUpBlock(float l, float h, float w, float x, float y, float z){
        Vector3f currentUpPosition;
        upIndex++;
        geom = new Geometry("SurfUp", new Box(l, h, w));
        geom.setMaterial(game.getMain().red);
        upPositionList.add(currentUpPosition = new Vector3f(x, y, z));
        geom.setLocalTranslation(currentUpPosition);
        nodeUp.attachChild(geom);
        upPhysList.add(upPhys = new RigidBodyControl(0.0f));
        geom.addControl(upPhys);
        game.getBulletAppState().getPhysicsSpace().add(upPhys);
    }
    
    public void createDownBlock(float l, float h, float w, float x, float y, float z){
        Vector3f currentDownPosition;
        downIndex++;
        geom = new Geometry("SurfDown",new Box(l, h, w));
        geom.setMaterial(game.getMain().yellow);
        downPositionList.add(currentDownPosition = new Vector3f(x, y, z));
        geom.setLocalTranslation(currentDownPosition);
        nodeDown.attachChild(geom);
        downPhysList.add(downPhys = new RigidBodyControl(0.0f));
        geom.addControl(downPhys);
        game.getBulletAppState().getPhysicsSpace().add(downPhys);
    }
    
    public void moveSurfaceUp(){
        if(!upDownPositionList.isEmpty()){
            nodeUpDown.move(0f, .5f, 0f);
            for(int i = 0; i < upDownIndex; i++){
                upDownPositionList.get(i).setY(upDownPositionList.get(i).getY() + 0.5f);
                upDownPhysList.get(i).setPhysicsLocation(upDownPositionList.get(i));
            }
        }
        if(!upPositionList.isEmpty()){
            nodeUp.move(0f, .5f, 0f);
            for(int i = 0; i < upIndex; i++){
                upPositionList.get(i).setY(upPositionList.get(i).getY() + 0.5f);
                upPhysList.get(i).setPhysicsLocation(upPositionList.get(i));
            }
        }
    }
    
    public void moveSurfaceDown(){
        if(!upDownPositionList.isEmpty()){
            nodeUpDown.move(0f, -.5f, 0f);
            for(int i = 0; i < upDownIndex; i++){
                upDownPositionList.get(i).setY((upDownPositionList.get(i).getY() - 0.5f));
                upDownPhysList.get(i).setPhysicsLocation(upDownPositionList.get(i));
            }
        }
        if(!downPositionList.isEmpty()){
            nodeDown.move(0f, -.5f, 0f);
            for(int i = 0; i < downIndex; i++){
                downPositionList.get(i).setY((downPositionList.get(i).getY() - 0.5f));
                downPhysList.get(i).setPhysicsLocation(downPositionList.get(i));
            }
        }
    }
    
    public void clearMovableSurfaces(){
        nodeUpDown.detachAllChildren();
        nodeUp.detachAllChildren();
        nodeDown.detachAllChildren();
        nodeUpDown.setLocalTranslation(0f, 0f, 0f);
        nodeUp.setLocalTranslation(0f, 0f, 0f);
        nodeDown.setLocalTranslation(0f, 0f, 0f);
        while(upDownIndex > 0){
            upDownIndex--;
            game.getBulletAppState().getPhysicsSpace().removeCollisionObject(upDownPhysList.get(upDownIndex));
        }
        while(upIndex > 0){
            upIndex--;
            game.getBulletAppState().getPhysicsSpace().removeCollisionObject(upPhysList.get(upIndex));
        }
        while(downIndex > 0){
            downIndex--;
            game.getBulletAppState().getPhysicsSpace().removeCollisionObject(downPhysList.get(downIndex));
        }
        while(listIndex > 0){
            listIndex--;
            game.getMain().getRootNode().detachChild(geomList.get(listIndex));
            game.getBulletAppState().getPhysicsSpace().removeCollisionObject(physList.get(listIndex));   
        }
    }
    
    public void clearLists(){
        upDownPositionList.clear();
        upPositionList.clear();
        downPositionList.clear();
        physList.clear();
        upDownPhysList.clear();
        upPhysList.clear();
        downPhysList.clear();
        geomList.clear();
    }

    public Spatial getModel() {
        return model;
    }
    
    public Vector3f getPlayerStart() {
        return playerStartPos;
    }
    
    public Vector3f getBallStart() {
        return ballStartPos;
    }
}
