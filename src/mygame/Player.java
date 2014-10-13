/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author tuc53509
 */
public class Player extends Node {
    
    private Game game;
    
    public Player(Game game) {
        this.game = game;
        this.setName("Player");
        this.setLocalTranslation(0, 10, 0);
    }
}
