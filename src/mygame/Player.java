/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.scene.Node;

/**
 * Wraps up the player into a Node.
 * 
 * @author Zack Hunter
 * @version %I% %G%
 * @see Node
 * @since 2.0
 */
public class Player extends Node {
    
   /**
    * copy of current Game object
    */
    private Game game;
    
   /**
    * Constructor. Copies <code>game</code>, sets name of node, and sets 
    * starting position.
    * 
    * @param game instance of current game
    */
    public Player(Game game) {
        this.game = game;
        this.setName("Player");
        this.setLocalTranslation(0, 10, 0);
    }
}
