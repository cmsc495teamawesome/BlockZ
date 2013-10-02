package BlockZ;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.MouseInput;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector2f;
import com.jme3.math.Ray;
import java.util.Random;

/**
    @author Team B - Bowes, R.J., Samonds, G., and Scuderi, M. 
 * CMSC495-6380 Professor Hung Dao
 */
public class GameBoard extends SimpleApplication { 
    
    private BitmapText hudText;
    
    ArrayList<Laser> lasers;
    
    private Object playHandLock = new Object();
    private float playHandCounter = 0f;
    
    float x, y, z;                      //Board dimensions
    
    int dropRate=50;
    double time1=System.currentTimeMillis(); 
    
    long score = 0;
    
    int blockIdent=0;
    
    Random rand;
    
    ArrayList<Block> blockList;
    
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
        
        blockList = new ArrayList();
        rand = new Random();            //Initialize random number generator
        
        createBoard();
        
        createLasers();
        
        setupDebugText();
        
        initKeys();
    }

    public void createLasers()
    {
        lasers = new ArrayList<Laser>();
        
        //Equally space five lasers across the bottom of the game board.
        for(int i = 0; i < 5; i++)
        {
            Laser l = new Laser(this, new Vector3f(-x + (x*0.2f) + ((float)i*x*0.4f), -y/2f - 2f, 0f));
            switch(i)
            {
                case 0:
                    l.setColor(ColorRGBA.Magenta);
                    break;
                case 1:
                    l.setColor(ColorRGBA.Red);
                    break;
                case 2:
                    l.setColor(ColorRGBA.Yellow);
                    break;
                case 3:
                    l.setColor(ColorRGBA.Green);
                    break;
                case 4:
                    l.setColor(ColorRGBA.Blue);
                    break;
            }
            lasers.add(l);
        }
    }
    
    public void addBlock() {
        
        if (System.currentTimeMillis()-time1 > 1000*(50/dropRate))       //If enough time has elapsed for the drop rate (needs heavy tweaking)
        {
            float[] pos = {(float)rand.nextInt((int)x)-5, y, 0};                            //Randomly generate position at top of board
            Block block1 = new Block(this, bulletAppState, 1, blockIdent, 1, pos, ColorRGBA.Red);    //Call Block constructor
            blockList.add(block1);                                                          //Add block to array list
            blockIdent++;                                                                   //Increment block identifier
            time1=System.currentTimeMillis();                                               //Reset time counter
        }
    }
    
    //Gets Block by removing "Block " from string and using remaining number as index in ArrayList of Blocks
    public Block getBlock(String name) {
        
        int blockNum = Integer.parseInt(name.substring(6));        
        //System.out.println(blockNum);
        
        return blockList.get(blockNum);
    }
    
    private void setupDebugText()
    {
        hudText = new BitmapText(guiFont, false);
        hudText.setSize(guiFont.getCharSet().getRenderedSize());      
        hudText.setColor(ColorRGBA.Blue);                             
        String debugText = new String("");
        
        /*
        for(Laser l:lasers)
        {
            String currentTarget = l.getTarget();
            debugText = debugText.concat((!currentTarget.equals(""))?currentTarget + "\t\t":"\t\t");
        }
        */
        
        hudText.setText(debugText);             // the text
        hudText.setLocalTranslation(275, hudText.getLineHeight(), 0); 
        guiNode.attachChild(hudText);
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        for(Laser l : lasers)
        {
            l.update(tpf);
        }
        
        playHandCounter += tpf;
        if (playHandCounter > .5f) {
            playHand();
            playHandCounter -= .5f;
        }
        
        addBlock();
    }
    
    public void updateDropRate(int change)
    {
        // TODO: Implement when drop rate is tweaked - stubbed out for now.
        /* 
        dropRate += change;
        if(dropRate < 0) dropRate = 0;
        if(dropRate > 100) dropRate = 100;
        */ 
    }
    
    private void playHand()
    {
        synchronized (playHandLock) {
            HandEvaluator.HandResult hand = new HandEvaluator(this).getHand();

            score += hand.scoreChange;
            updateDropRate(hand.descentChange);

            // TODO: Update HUD

            // TODO: Handle the various hands returned by the hand evaluator for specific behavior
            switch (hand.hand) {
                case BlockZ:
                    break;
                case FourOfAKind:
                    break;
                case FullHouse:
                    break;
                case ThreeOfAKind:
                    break;
                case LargeStraight:
                    break;
                case SmallStraight:
                    break;
                case Chance:
                    break;
                case NotAHand:
                    return;
            }

            
            System.out.println("Score = " + String.valueOf(score));
            System.out.println("Rate Of Descent = " + String.valueOf(dropRate));
            System.out.println(hand.hand.toString());

            for (Block b : hand.handBlocks) {
                b.removeBlock();
            }

        }
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
            
                if(clickResults.size()==0) return;
                
                //Test code for removing blocks on click
                if (clickResults.getCollision(2).getGeometry().getName().substring(0, 5).equals("Block"))
                    getBlock(clickResults.getCollision(2).getGeometry().getName()).removeBlock();                
                
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
}
