
package BlockZ;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

/**
 * @author Team B - Bowes, R.J., Samonds, G., and Scuderi, M. 
 * CMSC495-6380 Professor Hung Dao
 */
public class Block  {
    
    public static Node blockNode = new Node("BlockZ");
    
    int size, num; 
    String name;
    float mass;
    float[] position;
    float gameOverTimer;
    ColorRGBA color;
    
    private Geometry block;    
    private GameBoard game;
    private BulletAppState bulletAppState;        
    private RigidBodyControl block_phy;
    
    public Block(GameBoard g, BulletAppState b, int s, int n, int nu, float m, float[] pos, ColorRGBA col) {
        
        size=s;
        num=nu;
        name=Integer.toString(n);
        name = "Block " + name;
        mass=m;
        position=pos;
        color=col;
        game=g;
        bulletAppState=b;        
        
        block = new Geometry(name, new Box(size, size, size));
        Material mat = new Material(game.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");        
        
        switch(num) {
            case 1: mat.setTexture("ColorMap", game.getAssetManager().loadTexture("Textures/dice_1.png"));
                    break;
            case 2: mat.setTexture("ColorMap", game.getAssetManager().loadTexture("Textures/dice_2.png"));
                    break;
            case 3: mat.setTexture("ColorMap", game.getAssetManager().loadTexture("Textures/dice_3.png"));
                    break;
            case 4: mat.setTexture("ColorMap", game.getAssetManager().loadTexture("Textures/dice_4.png"));
                    break;
            case 5: mat.setTexture("ColorMap", game.getAssetManager().loadTexture("Textures/dice_5.png"));
                    break;
            case 6: mat.setTexture("ColorMap", game.getAssetManager().loadTexture("Textures/dice_6.png"));
                    break;
            default: break;
        }        
        
        block.setMaterial(mat);
        block.setUserData("Value", num);
        blockNode.attachChild(block);
        game.getRootNode().attachChild(blockNode);
        block.setLocalTranslation(pos[0], pos[1], pos[2]);        
        block_phy = new RigidBodyControl(1f);
        block_phy.setMass(mass);
        block.addControl(block_phy);    
        bulletAppState.getPhysicsSpace().add(block_phy);
        
    }  
    
    public void removeBlock() {
        blockNode.detachChild(block);
        bulletAppState.getPhysicsSpace().remove(block_phy);
    }
    
    public boolean checkForGameOver(float tpf){
        if (    (block_phy.getPhysicsLocation().y >= game.y) & 
                ((block_phy.getLinearVelocity().y <= game.GAME_OVER_MOVE_THRESHHOLD) ||
                 (block_phy.getLinearVelocity().y >= -game.GAME_OVER_MOVE_THRESHHOLD))){
            gameOverTimer += tpf;
        }
        else {
            gameOverTimer = 0;
        }
        
        if (gameOverTimer >= game.GAME_OVER_TIME_THRESHHOLD){
            return true;
        }
        else{
            return false;
        }
    }
    
}
