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
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.MouseInput;
import com.jme3.collision.CollisionResults;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector2f;
import com.jme3.math.Ray;
import java.util.HashMap;
import java.util.Random;



/**
    @author Team B - Bowes, R.J., Samonds, G., and Scuderi, M. 
 * CMSC495-6380 Professor Hung Dao
 */
public class GameBoard extends SimpleApplication { 
    
    ArrayList<Laser> lasers;
    
    protected float GAME_OVER_TIME_THRESHHOLD = 1.0f;
    protected float GAME_OVER_MOVE_THRESHHOLD = 0.5f;
    
    private Object playHandLock = new Object();
    private float playHandCounter = 0f;
    
    float x, y, z;                      //Board dimensions
    
    int xOffset=5;
    int dropRate=50;
    double time1=System.currentTimeMillis(); 
    
    long score = 0;
    
    private GameHUD hud;
    private GameMenu gameMenu;
    
    int blockIdent=0;
    int explosiveIdent=0;
    int detonatedIdent=0;
    
    Random rand;
    
    ArrayList<Block> blockList;
    ArrayList<Explosive> explosiveList;
    ArrayList<Explosive> detonatedList;
    ArrayList<Long> detonatedTime;    
    
    //Create GameButtons
    private GameButton playHandButton;
    
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
        floor.move(0f+xOffset, -y/2, 0f);        
        leftWall = makeFace("Left Wall", y, z);
        leftWall.move(-x+xOffset, y/2, 0).rotate(0, 0, FastMath.PI/2);
        rightWall = makeFace("Right Wall", y, z);
        rightWall.move(x+xOffset, y/2, 0).rotate(0, 0, FastMath.PI/2);    
        frontWall = makeFace("Front Wall", x, y);
        frontWall.move(0+xOffset, y/2, z).rotate(FastMath.PI/2, 0, 0);    
        backWall = makeFace("Back Wall", x, y);
        backWall.move(0+xOffset, y/2, -z).rotate(FastMath.PI/2, 0, 0);       
        
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
        
        hud = new GameHUD(this, score, 0, dropRate);
        
        playHandButton = new GameButton(this, bulletAppState, "PlayHand", 5, -11, 3, 1, true);
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
        
        /*  This is burning my eyes
        FilterPostProcessor fpp=new FilterPostProcessor(assetManager);
        BloomFilter bloom=new BloomFilter();
        fpp.addFilter(bloom);
        viewPort.addProcessor(fpp);
        */
        
        //Initialize physics
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState); 
        bulletAppState.getPhysicsSpace().setAccuracy(1f/90f);       //Increase physics accuracy, to be tweaked later
        
        blockList = new ArrayList();
        explosiveList = new ArrayList();
        detonatedList = new ArrayList();
        detonatedTime = new ArrayList();
        
        //Add lighting to the scene, direction to be tweaked
        DirectionalLight sun = new DirectionalLight();
        sun.setColor(ColorRGBA.White);
        sun.setDirection(new Vector3f(0.6f, -1f,-1f).normalizeLocal());
        rootNode.addLight(sun);
        
        rand = new Random(System.currentTimeMillis());            //Initialize and seed random number generator
        
        createBoard();
        
        createLasers();
        
        initKeys();
    }

    public void createLasers()
    {
        lasers = new ArrayList<Laser>();
        
        //Equally space five lasers across the bottom of the game board.
        for(int i = 0; i < 5; i++)
        {
            Laser l = new Laser(this, new Vector3f(-x + (x*0.2f) + ((float)i*x*0.4f)+xOffset, -y/2f - 2f, 0f));
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
    
    public void setGameMenu(GameMenu m)
    {
        gameMenu = m;
    }
    
    public void addBlock() {
        
        if (System.currentTimeMillis()-time1 > 1000*(50.0/dropRate))             //If enough time has elapsed for the drop rate (needs heavy tweaking)
        {

            float[] pos = {(float)rand.nextInt((int)x)-5+xOffset, y, 0};                //Randomly generate position at top of board
            
            //1 in 8 chance of making explosive, to be tweaked
            if (rand.nextInt(8)+1 == 1) {
                Explosive explosive1 = new Explosive(this, bulletAppState, 1, explosiveIdent, 1, pos, ColorRGBA.Yellow); 
                explosiveList.add(explosive1);
                explosiveIdent++;
            }
            else {                
                Block block1 = new Block(this, bulletAppState, 1, blockIdent, rand.nextInt(6)+1, 1, pos, ColorRGBA.Red);    //Call Block constructor   
                blockList.add(block1);                                                          //Add block to array list
                blockIdent++;                                                                   //Increment block identifier
            }
                                                                       //Increment block identifier

            time1=System.currentTimeMillis();                                               //Reset time counter
        }
    }
    
    //Gets Block by removing "Block " from string and using remaining number as index in ArrayList of Blocks
    public Block getBlock(String name) {
        
        int blockNum = Integer.parseInt(name.substring(6));        
        
        return blockList.get(blockNum);
    }
    
    public Explosive getExplosive(String name) {
        
        int explosiveNum = Integer.parseInt(name.substring(10));
        
        return explosiveList.get(explosiveNum);
    }
    
    public void removeProjectiles() {
        
        //Iterate through list of detonation times
        for (int i=0; i<detonatedTime.size(); i++)
            if (System.currentTimeMillis() - detonatedTime.get(i) > 2000)   //If more than 2 seconds has elapsed, remove corresponding projectiles
                detonatedList.get(i).removeProjectiles();
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        for(Laser l : lasers)
        {
            l.update(tpf);
        }
        
        for(Block b : blockList)
        {
            if (b.checkForGameOver(tpf))
            {
                HashMap<String,String> values = new HashMap<String, String>();
                values.put("Score", String.valueOf(score));
                gameMenu.handleGameOver(values);
            }
            
        }
        // Update HUD stats and display
        hud.updateScore(score);
        hud.updateRate(dropRate);
        hud.update(tpf);
        
        updatePlayHandButton();
        
        addBlock();
        removeProjectiles();
    }
    
    public void updatePlayHandButton()
    {
        HandEvaluator.HandResult hand = new HandEvaluator(this).getHand();
        if(!hand.hand.equals(HandEvaluator.hands.NotAHand))
        {
            playHandButton.enable();
        }
        else
        {
            playHandButton.disable();
        }
    }
    
    public void updateDropRate(int change)
    {
        dropRate += change;
        if(dropRate < 0) dropRate = 0;
        if(dropRate > 100) dropRate = 100;
    }
    
    private void playHand()
    {
        synchronized (playHandLock) {
            HandEvaluator.HandResult hand = new HandEvaluator(this).getHand();

            score += hand.scoreChange;
            updateDropRate(hand.descentChange);

            // TODO: Handle the various hands returned by the hand evaluator for specific behavior
            int blocksToRemove;
            switch (hand.hand) {
                // When BlockZ, remove half of the current blocks on the board
                case BlockZ:
                    removeBlocks(blockList.size()/2);
                    break;
                    
                case FourOfAKind:
                    removeBlockLine("vertical");
                    removeBlockLine("vertical");
                    break;
                    
                // When Full House, remove houseValue 
                case FullHouse:
                    removeBlocks(hand.houseValue);
                    break;
                    
                case ThreeOfAKind:
                    removeBlocks(3);
                    break;
                    
                case LargeStraight:
                    removeBlockLine("horizontal");
                    removeBlockLine("horizontal");
                    break;
                    
                case SmallStraight:
                    removeBlockLine("horizontal");
                    break;
                    
                case Chance:
                    break;
                    
                case NotAHand:
                    return;
            }

            hud.displayMessage(hand.hand.toString());

            for (Block b : hand.handBlocks) {
                b.removeBlock();
            }

        }
    }

    // Casts a straight line across the board and removes intersecting blocks
    //   parameter = "vertical" or "horizontal"
    private void removeBlockLine(String orientation){
        
        float x1 = 0;
        float x2 = 0;
        float y1 = 0;
        float y2 = 0;
        
        // Set coordinates for verical beam
        if (orientation.equals("vertical")){
            y1 = -y/2;
            y2 = 9*y/10;
            x1 = (rand.nextFloat()*2*x) - x + xOffset;
            x2 = (rand.nextFloat()*2*x) - x + xOffset;
        }
        
        // Set coordinates for horizontal beam
        if (orientation.equals("horizontal")){
            x1 = -x+xOffset;
            x2 = x+xOffset;
            y1 = (rand.nextFloat()*3*y/2) - (y/2);
            y2 = (rand.nextFloat()*3*y/2) - (y/2);
        }
        
        // Create collisionResults array
        CollisionResults hitObjects = new CollisionResults();
                
        // Create direction from point x to point y
        Vector3f dir = new Vector3f(x2-x1, y2-y1, 0);
                
        // Aim the ray from x1 to x2
        Ray ray = new Ray(new Vector3f(x1, y1, 0), dir);
        
          
        // Collect intersections between ray and all nodes in results list.
        rootNode.collideWith(ray, hitObjects);
           
        for (int j = 0; j < hitObjects.size(); j++){
          
            if (hitObjects.getCollision(j).getGeometry().getName().startsWith("Block")){
                 System.out.println("Removing block in line"); 
                 getBlock(hitObjects.getCollision(j).getGeometry().getName()).removeBlock();
             }
        }
    }
    //Removes multiple, randomly chosen blocks.
    private void removeBlocks(int blocksToRemove){
        if (blockList.size() < blocksToRemove)
            blocksToRemove = blockList.size();
        
        for (int i = 0; i < blocksToRemove; i++){
            blockList.get(rand.nextInt(blockList.size())).removeBlock();
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
                
                if (clickResults.getCollision(1).getGeometry().getName().equals("PlayHand"))
                {
                    playHand();
                    return;
                }
                
                //Test code for removing blocks on click

                if (clickResults.size() > 1)
                {
                    if (clickResults.getCollision(2).getGeometry().getName().substring(0, 5).equals("Block"))
                    {
                        getBlock(clickResults.getCollision(2).getGeometry().getName()).removeBlock();
                        hud.displayMessage("Clicky clicky.");
                        score+=10;  //Temporary score to test HUD
                    }
                    
                    //Test code for detonating explosives on click
                    if (clickResults.getCollision(2).getGeometry().getName().substring(0, 5).equals("Explo")) {
                        getExplosive(clickResults.getCollision(2).getGeometry().getName()).explode();      
                        detonatedList.add(getExplosive(clickResults.getCollision(2).getGeometry().getName()));  //Add to detonated list
                        detonatedTime.add(System.currentTimeMillis());  //Add current time to detonation time list
                        detonatedIdent++;
                    }
                }

                
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
