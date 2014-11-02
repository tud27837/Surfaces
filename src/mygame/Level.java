/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author Zack
 */
public class Level {
    
    private Game game;
    private Spatial model;
    private int levelNum;
    private Vector3f playerStartPos;
    private Vector3f ballStartPos;
    
    public Level(Game game) {
        this.game = game;
        levelNum = 1;
        loadLevel();
    }
    
    public void loadLevel() {
        switch (levelNum) {
            case 1:
                model = game.getMain().getAssetManager().loadModel("Scenes/TestScene.j3o");
                playerStartPos = new Vector3f(0, 10, 0);
                game.getPlayer().setStart(playerStartPos);
                ballStartPos = new Vector3f(0, 5, 10);
                game.getBall().setPhysicsLocation(ballStartPos);
                break;
            case 2:
                break;
        }
        model.setName("Level");
        model.setLocalScale(5f);
        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape((Node) model);
        game.getMain().getRootNode().attachChild(model);
        RigidBodyControl landscape = new RigidBodyControl(sceneShape, 0);
        model.addControl(landscape);
        game.getBulletAppState().getPhysicsSpace().add(landscape);
    }
    
    public void nextLevel() {
        ++levelNum;
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
