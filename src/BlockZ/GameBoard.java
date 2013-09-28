package BlockZ;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
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
    
    float x, y, z;                      //Board dimensions
    
    //Prepare materials
    private Material blueTrans;
    
    //Prepare geometries
    private static Geometry floor;
    private static Geometry leftWall;
    private static Geometry rightWall;
    private static Geometry frontWall;
    private static Geometry backWall;    
    
    //Prepare physics application state
    private BulletAppState bulletAppState;    
    
    //Prepare physical nodes
    private RigidBodyControl    floor_phy;    
    private RigidBodyControl    leftWall_phy;
    private RigidBodyControl    rightWall_phy;
    private RigidBodyControl    frontWall_phy;
    private RigidBodyControl    backWall_phy; 
    
    public static void main(String[] args) {
        GameBoard app = new GameBoard(10, 14, (float)1.2);
        Logger.getLogger("").setLevel(Level.WARNING);
        app.start();
    }
    
    public GameBoard(float width, float height, float depth) {
        
        x = width;
        y = height;
        z = depth;       
        
    }
    
    public void createBoard() {
        
        //Create transparent material
        blueTrans = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        blueTrans.setColor("Color", new ColorRGBA(0, 0, 1, 0.1f));
        blueTrans.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        
        //Create geometries and move/rotate to form game board        
        floor = makeFace("Floor", x, z);        
        floor.move(0f, -y/2, 0f);        
        leftWall = makeFace("Left Wall", y, z);
        leftWall.move(-x, y/2, 0).rotate(0, 0, FastMath.PI/2);
        rightWall = makeFace("Right Wall", y, z);
        rightWall.move(x, y/2, 0).rotate(0, 0, FastMath.PI/2);    
        frontWall = makeFace("Front Wall", x, y);
        frontWall.move(0, y/2, z).rotate(FastMath.PI/2, 0, 0);    
        backWall = makeFace("Back Wall", x, y);
        backWall.move(0, y/2, -z).rotate(FastMath.PI/2, 0, 0);       
        
        //Attach geometries to scene graph
        rootNode.attachChild(floor);
        rootNode.attachChild(leftWall);
        rootNode.attachChild(rightWall);
        rootNode.attachChild(frontWall);
        rootNode.attachChild(backWall);        
        
        //Make board physical
        floor_phy = new RigidBodyControl(0.0f);
        frontWall_phy = new RigidBodyControl(0.0f);
        backWall_phy = new RigidBodyControl(0.0f);
        leftWall_phy = new RigidBodyControl(0.0f);
        rightWall_phy = new RigidBodyControl(0.0f);        
        floor.addControl(floor_phy);
        frontWall.addControl(frontWall_phy);
        backWall.addControl(backWall_phy);
        leftWall.addControl(leftWall_phy);
        rightWall.addControl(rightWall_phy);        
        bulletAppState.getPhysicsSpace().add(floor_phy);
        bulletAppState.getPhysicsSpace().add(frontWall_phy);
        bulletAppState.getPhysicsSpace().add(backWall_phy);
        bulletAppState.getPhysicsSpace().add(leftWall_phy);
        bulletAppState.getPhysicsSpace().add(rightWall_phy);
        
    }
    
    public Geometry makeFace(String name, float x, float y) {
        
        Geometry face = new Geometry(name, new Box(Vector3f.ZERO, x, 0.1f, y));
        face.setMaterial(blueTrans);
        face.setQueueBucket(RenderQueue.Bucket.Transparent);
        
        return face;
    }

    @Override
    public void simpleInitApp() {
        
        //Set camera starting location, increase movement speed
        cam.setLocation(new Vector3f(0f, 0f, 30f));        
        flyCam.setMoveSpeed(30);        
        
        //Initialize physics
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState); 
        
        Node BlockZ = new Node("BlockZ");
        rootNode.attachChild(BlockZ);
        BlockZ.attachChild(makeCube("c(0,3,0)", 0f, 3f, 0f));
        BlockZ.attachChild(makeCube("c(1,6,1)", 1f, 6f, 1f));
        BlockZ.attachChild(makeCube("c(0,9,0)", 0f, 9f, 0f));
        
        createBoard();
        
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
