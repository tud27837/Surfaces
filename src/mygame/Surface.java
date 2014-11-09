/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;

/**
 *
 * @author Jake
 */
public class Surface extends Node {

    private Geometry geomSurfUpDown;
    
    public Surface(Game game, float l, float w, float h) {
        this.setName("Surface");
        geomSurfUpDown = new Geometry("SurfUpDown", new Box(l, h, w));
        geomSurfUpDown.setMaterial(game.getMain().white);
        this.attachChild(geomSurfUpDown);
    }
    
}
