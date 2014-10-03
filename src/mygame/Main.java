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

public class Main extends SimpleApplication {

    public static Material gold, magenta;

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
    
    public AppSettings getSettings() {
        return (settings);
    }

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

    protected static void clearJMonkey(Main m) {
        m.guiNode.detachAllChildren();
        m.rootNode.detachAllChildren();
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        // nothing
    }

    // -------------------------------------------------------------------------
    // Initialization Methods
    // -------------------------------------------------------------------------
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
    }

    private void initGui() {
        setDisplayFps(true);
        setDisplayStatView(false);
    }

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

    private void initCam() {
        flyCam.setEnabled(true);
        flyCam.setMoveSpeed(100f);
        cam.setLocation(new Vector3f(3f, 15f, 15f));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
    }
}