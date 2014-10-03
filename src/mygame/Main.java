/*
 * Copyright (c) 2009-2010 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
 
package mygame;
 
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import java.awt.Dimension;
import java.awt.Toolkit;
 
 
public class Main extends SimpleApplication
        implements ActionListener{
 
  private Spatial levelOne;
  private BulletAppState bulletAppState;
  private RigidBodyControl landscape;
  private BetterCharacterControl player;
  private Sphere ball;
  private Vector3f walkDirection = new Vector3f();
  private boolean left = false, right = false, up = false, down = false;
  private Node playerNode;
 
 
  public static void main(String[] args) {
    Main app = new Main();
     //some presets
        initAppScreen(app);
        app.start();
  }
 
  private static void initAppScreen(SimpleApplication app) {
        AppSettings aps = new AppSettings(true);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        screen.width *= 0.75;
        screen.height *= 0.75;
        aps.setResolution(screen.width, screen.height);
        app.setSettings(aps);
        app.setShowSettings(false);
    }
  
  public void simpleInitApp() {  
    //don't show stats
    setDisplayFps(false);
    setDisplayStatView(false);
    /** Set up Physics */
    bulletAppState = new BulletAppState();
    bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);//for bullets
    stateManager.attach(bulletAppState);
    //bulletAppState.setDebugEnabled(true);
    bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0,-20,0));
    //bulletAppState.getPhysicsSpace().enableDebug(assetManager);
    // We re-use the flyby camera for rotation, while positioning is handled by physics
    viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
    flyCam.setMoveSpeed(100);
    setUpKeys();
    setUpLight();
    //setUpSky();
    //Preload Levels
    levelOne = assetManager.loadModel("Scenes/TestScene.j3o");
    levelOne.setLocalScale(5f);
    //Set up physics and add level/player to scenegraph
    setLevel(levelOne);
  }
 
  private void setLevel(Spatial level) {
      // We set up collision detection for the scene by creating a
    // compound collision shape and a static RigidBodyControl with mass zero.
    CollisionShape sceneShape =
            CollisionShapeFactory.createMeshShape((Node) level);
    landscape = new RigidBodyControl(sceneShape, 0);
    level.addControl(landscape);
 
    // We set up collision detection for the player by creating
    // a capsule collision shape and a CharacterControl.
    // The CharacterControl offers extra settings for
    // size, stepheight, jumping, falling, and gravity.
    // We also put the player in its starting position.
    //CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 6f, 1);
    player = new BetterCharacterControl(1.5f, 6f, 1);
    playerNode = new Node();
    playerNode.setLocalTranslation(0, 10, 0);
    playerNode.addControl(player);
    player.setJumpForce(new Vector3f(0,12,0));
    //player.setFallSpeed(30);
    player.setGravity(new Vector3f(0,100,0));
    
    //player.warp(new Vector3f(0, 10, 0));
    // We attach the scene and the player to the rootnode and the physics space,
    // to make them appear in the game world.
    rootNode.attachChild(level);
    rootNode.attachChild(playerNode);
    bulletAppState.getPhysicsSpace().add(landscape);
    bulletAppState.getPhysicsSpace().add(player);/** Illuminated bumpy rock with shiny effect. 
 *  Uses Texture from jme3-test-data library! Needs light source! */
  }

  private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
  }
 
  private void setUpLight() {
    // We add light so we see the scene
    AmbientLight al = new AmbientLight();
    al.setColor(ColorRGBA.White.mult(3.3f));
    rootNode.addLight(al);
 
    DirectionalLight dl = new DirectionalLight();
    dl.setColor(ColorRGBA.White);
    dl.setDirection(new Vector3f(1.5f, -2, 2).normalizeLocal());
    rootNode.addLight(dl);
  }
 
  /** We over-write some navigational key mappings here, so we can
   * add physics-controlled walking and jumping: */
  private void setUpKeys() {
    inputManager.deleteMapping(INPUT_MAPPING_EXIT);
    inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
    inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
    inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
    inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
    inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
    inputManager.addMapping("Quit", new KeyTrigger(KeyInput.KEY_ESCAPE));
    inputManager.addListener(this, "Left");
    inputManager.addListener(this, "Right");
    inputManager.addListener(this, "Up");
    inputManager.addListener(this, "Down");
    inputManager.addListener(this, "Jump");
    inputManager.addListener(this, "Quit"); // listen for ESC key to close game
  }
 
  /*private void setUpSky() {
 
        Texture west = assetManager.loadTexture("Textures/Sky/Lagoon/west.jpg");
        Texture east = assetManager.loadTexture("Textures/Sky/Lagoon/east.jpg");
        Texture north = assetManager.loadTexture("Textures/Sky/Lagoon/north.jpg");
        Texture south = assetManager.loadTexture("Textures/Sky/Lagoon/south.jpg");
        Texture up = assetManager.loadTexture("Textures/Sky/Lagoon/up.jpg");
        Texture down = assetManager.loadTexture("Textures/Sky/Lagoon/down.jpg");
 
        Spatial sky = SkyFactory.createSky(assetManager, west, east, north, south, up, down);
        rootNode.attachChild(sky);
 
  }*/
 
  /** These are our custom actions triggered by key presses.
   * We do not walk yet, we just keep track of the direction the user pressed. */
  public void onAction(String binding, boolean value, float tpf) {
    if (binding.equals("Left")) {
      if (value) { left = true; } else { left = false; }
    } else if (binding.equals("Right")) {
      if (value) { right = true; } else { right = false; }
    } else if (binding.equals("Up")) {
      if (value) { up = true; } else { up = false; }
    } else if (binding.equals("Down")) {
      if (value) { down = true; } else { down = false; }
    } else if (binding.equals("Jump")) {
      player.jump();
    } else if (binding.equals("Quit")) {
      stop(); // simple way to close game
    }
  }
 
 
  /**
   * This is the main event loop--walking happens here.
   * We check in which direction the player is walking by interpreting
   * the camera direction forward (camDir) and to the side (camLeft).
   * The setWalkDirection() command is what lets a physics-controlled player walk.
   * We also make sure here that the camera moves with player.
   */
  @Override
  public void simpleUpdate(float tpf) {
   
        Vector3f camDir = cam.getDirection().clone().multLocal(10f);
        camDir.setY(0);
        Vector3f camLeft = cam.getLeft().clone().multLocal(10f);
        walkDirection.set(0, 0, 0);
        if (left)  { walkDirection.addLocal(camLeft); }
        if (right) { walkDirection.addLocal(camLeft.negate()); }
        if (up)    { walkDirection.addLocal(camDir); }
        if (down)  { walkDirection.addLocal(camDir.negate()); }
        player.setWalkDirection(walkDirection);
        cam.setLocation(playerNode.getWorldTranslation().add(0, 3, 0));
        Vector3f camPoint = cam.getDirection();
        System.out.println(camPoint.toString());
   
  }
}