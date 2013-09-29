package BlockZ;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.MouseInput;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector2f;
import com.jme3.math.Ray;

/**
    @author Team B - Bowes, R.J., Samonds, G., and Scuderi, M. 
 * CMSC495-6380 Professor Hung Dao
 */
public class GameBoard extends SimpleApplication {

    private ArrayList<Laser> lasers;
    
    public static void main(String[] args) {
        GameBoard app = new GameBoard();
        Logger.getLogger("").setLevel(Level.WARNING);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        Node BlockZ = new Node("BlockZ");
        rootNode.attachChild(BlockZ);
        BlockZ.attachChild(makeCube("c(0,3,0)", 0f, 3f, 0f));
        BlockZ.attachChild(makeCube("c(1,6,1)", 1f, 6f, 1f));
        BlockZ.attachChild(makeCube("c(0,9,0)", 0f, 9f, 0f));

        /*
        BlockZ.attachChild(makeCube("c(1,0,1)", 1f, 0f, 1f));
        BlockZ.attachChild(makeCube("c(2,2,2)", 2f, 2f, 2f));
        BlockZ.attachChild(makeCube("c(2,2,2)", 2f, 2f, 2f));
        BlockZ.attachChild(makeCube("c(2,2,2)", 2f, 2f, 2f));
        */
        
        lasers = new ArrayList<Laser>();
        
        
        Laser l = new Laser(this);
        lasers.add(l);
        
        initKeys();
    }

    @Override
    public void simpleUpdate(float tpf) {
        for(Laser l : lasers)
        {
            l.update(tpf);
        }
        rootNode.getChild("BlockZ").move(0f, .1f*tpf, 0f);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    /** Listener gameListener for player input **/
    private ActionListener gameListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            //System.out.println("testing output");
            if (keyPressed & (name.equals("Click"))) {
                
                // Create collisionResults array
                CollisionResults clickResults = new CollisionResults();
                
                // Convert screen click to 3d position
                Vector2f click2d = inputManager.getCursorPosition();
                Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
                Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
                // Aim the ray from the clicked spot forwards.
                Ray ray = new Ray(click3d, dir);
                // Collect intersections between ray and all nodes in results list.
                rootNode.collideWith(ray, clickResults);
                
                for (int i = 0; i < clickResults.size(); i++) {
                    // Display object name
                    String target = clickResults.getCollision(i).getGeometry().getName();
                    System.out.println("Hit #" + i + ": " + target);
                }
            }
        }
    };
    
    /** Custom Keybinding: Create left click action */
    private void initKeys() {
        
        // Disable default camera controls
        flyCam.setEnabled(false);
        
        // Enable mouse cursor in game window
        inputManager.setCursorVisible(true);
        
        // Map "Click" action to left mouse button
        inputManager.addMapping("Click", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        
        // Add the name to the action listener.
        inputManager.addListener(gameListener, "Click");
    }
    
    
    /**
     * A cube object for target practice * 
     */
    protected Geometry makeCube(String name, float x, float y, float z) {
        Box box = new Box(1, 1, 1);
        Geometry cube = new Geometry(name, box);
        cube.setLocalTranslation(x, y, z);
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.randomColor());
        cube.setMaterial(mat1);
        return cube;
    }
}
