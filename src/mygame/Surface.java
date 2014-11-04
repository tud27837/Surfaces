/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

/**
 *
 * @author Jake
 */
public class Surface extends Node {

    public Surface(Game game) {
        this.setName("Surface");
        Geometry geomSurfUp = new Geometry("SurfUp", new Box(5f, .5f, 2f));
        geomSurfUp.setMaterial(game.getMain().white);
        this.attachChild(geomSurfUp);
    }
    
    
}
