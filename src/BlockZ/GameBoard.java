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
