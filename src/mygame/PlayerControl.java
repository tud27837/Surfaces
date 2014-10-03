/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;

/**
 *
 * @author tuc53509
 */
public class PlayerControl extends BetterCharacterControl {
    
    private Game game;
    
    public PlayerControl(Game game) {
        this.game = game;
        BetterCharacterControl bcc = new BetterCharacterControl(1.5f, 6f, 1f);
        bcc.setJumpForce(new Vector3f(0, 12, 0));
        bcc.setGravity(new Vector3f(0, 100, 0));
        game.getPlayer().addControl(bcc);
    }
    
}
