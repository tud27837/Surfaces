package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.system.AppSettings;
import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * Main class of the project. Initializes basic elements, creates and starts a 
 * simple app, and starts the game.
 * 
 * @author Zack Hunter
 * @version %I% %G%
 * @see SimpleApplication
 * @since 1.0
 */
public class Main extends SimpleApplication {

   /**
    * gold colored material. Can be applied to many geometries.
    */
    public static Material gold;
    /**
    * magenta colored material. Can be applied to many geometries.
    */
    public static Material magenta;
    /**
    * red colored material. Can be applied to many geometries.
    */
    public static Material red;
    /**
    * green colored material. Can be applied to many geometries.
    */
    public static Material green;
    /**
    * white colored material. Can be applied to many geometries.
    */
    public static Material white;
    /**
    * yellow colored material. Can be applied to many geometries.
    */
    public static Material yellow;

   /**
    * Main method of the project. Sets up the app.
    * 
    * @param args array of Strings containing default arguments
    */
    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        screen.width *= 0.75;
        screen.height *= 0.75;
        settings.setResolution(screen.width, screen.height);
        settings.setVSync(true);
        Main app = new Main();
        app.setShowSettings(false);
        app.setSettings(settings);
        app.start();
    }
    
   /**
    * Returns the settings object of the current app.
    * 
    * @return an AppSettings object containing the settings of the app
    */
    public AppSettings getSettings() {
        return (settings);
    }

    /**
     *
     */
    @Override
    public void simpleInitApp() {
        initMaterials();
        initLightandShadow();
        initCam();
        initGui();
        this.inputManager.clearMappings(); // clear default mappings

        // This starts the game
        StartScreen s = new StartScreen();
        stateManager.attach(s);
    }

   /**
    * Detaches all children from both the rootNode and the guiNode of the Main m 
    * argument.
    * 
    * @param m a Main class instance
    */
    protected static void clearJMonkey(Main m) {
        m.guiNode.detachAllChildren();
        m.rootNode.detachAllChildren();
    }
    
    /**
     *
     * @param tpf
     */
    @Override
    public void simpleUpdate(float tpf) {
        // nothing
    }

    // -------------------------------------------------------------------------
    // Initialization Methods
    // -------------------------------------------------------------------------
   /**
    * Initialize all materials to be used throughout the project. The individual
    * materials are stored in public global variables in this class
    * 
    * @see Material
    */
    private void initMaterials() {
        gold = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        gold.setBoolean("UseMaterialColors", true);
        gold.setColor("Ambient", ColorRGBA.Red);
        gold.setColor("Diffuse", ColorRGBA.Green);
        gold.setColor("Specular", ColorRGBA.Gray);
        gold.setFloat("Shininess", 4f); // shininess from 1-128

        magenta = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        magenta.setBoolean("UseMaterialColors", true);
        magenta.setColor("Ambient", ColorRGBA.Gray);
        magenta.setColor("Diffuse", ColorRGBA.Blue);
        magenta.setColor("Specular", ColorRGBA.Red);
        magenta.setFloat("Shininess", 2f); // shininess from 1-128
        
        red = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        red.setBoolean("UseMaterialColors", true);
        red.setColor("Ambient", ColorRGBA.Red);
        red.setColor("Diffuse", ColorRGBA.Red);
        red.setColor("Specular", ColorRGBA.Red);
        red.setFloat("Shininess", 2f);
        
        green = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        green.setBoolean("UseMaterialColors", true);
        green.setColor("Ambient", ColorRGBA.Green);
        green.setColor("Diffuse", ColorRGBA.Green);
        green.setColor("Specular", ColorRGBA.Green);
        green.setFloat("Shininess", 2f);
        
        white = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        white.setBoolean("UseMaterialColors", true);
        white.setColor("Ambient", ColorRGBA.White);
        white.setColor("Diffuse", ColorRGBA.White);
        white.setColor("Specular", ColorRGBA.Gray);
        white.setFloat("Shininess", 2f);
        
        yellow =  new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        yellow.setBoolean("UseMaterialColors", true);
        yellow.setColor("Ambient", new ColorRGBA(255,255,0,0));
        yellow.setColor("Diffuse", new ColorRGBA(255,255,0,0));
        yellow.setColor("Specular", new ColorRGBA(255,255,0,0));
        yellow.setFloat("Shininess", 2f);
    }

   /**
    * Initialize aspects of the GUI. Sets to display the frames per second and 
    * hide other stats.
    * 
    * @see SimpleApplication#setDisplayFps(boolean) 
    * @see SimpleApplication#setDisplayStatView(boolean) 
    */
    private void initGui() {
        setDisplayFps(true);
        setDisplayStatView(false);
    }

   /**
    * Initialize light elements and shadow elements. Add them to the root 
    * node and view port, respectively.
    */
    private void initLightandShadow() {
        // Light 1: white, directional
        DirectionalLight sun1 = new DirectionalLight();
        sun1.setDirection((new Vector3f(-0.7f, -1.3f, -0.9f)).normalizeLocal());
        sun1.setColor(ColorRGBA.Gray);
        rootNode.addLight(sun1);

        // Light 2: white, directional
        DirectionalLight sun2 = new DirectionalLight();
        sun2.setDirection((new Vector3f(0.7f, -1.3f, -0.9f)).normalizeLocal());
        sun2.setColor(ColorRGBA.Gray);
        rootNode.addLight(sun2);

        // Light 3: Ambient, gray
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(new ColorRGBA(0.7f, 0.7f, 0.7f, 1.0f));
        rootNode.addLight(ambient);

        // SHADOW
        // the second parameter is the resolution. Experiment with it! (Must be a power of 2)
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, 512, 1);
        dlsr.setLight(sun1);
        dlsr.setLight(sun2);
        viewPort.addProcessor(dlsr);
    }

   /**
    * Initialize elements affecting the state of the camera. Set the camera's 
    * starting position, move speed, and look direction as well as the background 
    * color of the scene.
    * 
    * @see SimpleApplication#cam
    * @see SimpleApplication#flyCam
    */
    private void initCam() {
        flyCam.setEnabled(true);
        flyCam.setMoveSpeed(100f);
        cam.setLocation(new Vector3f(3f, 15f, 15f));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
    }
}