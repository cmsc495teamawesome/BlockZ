
package BlockZ;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.LightControl;
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
    
    Material mat;
    PointLight blockLight;
    
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
        mat = new Material(game.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");    
        
        switch(num) {
            case 1: mat.setTexture("DiffuseMap", game.getAssetManager().loadTexture("Textures/dice_1.png"));
                    break;
            case 2: mat.setTexture("DiffuseMap", game.getAssetManager().loadTexture("Textures/dice_2.png"));
                    break;
            case 3: mat.setTexture("DiffuseMap", game.getAssetManager().loadTexture("Textures/dice_3.png"));
                    break;
            case 4: mat.setTexture("DiffuseMap", game.getAssetManager().loadTexture("Textures/dice_4.png"));
                    break;
            case 5: mat.setTexture("DiffuseMap", game.getAssetManager().loadTexture("Textures/dice_5.png"));
                    break;
            case 6: mat.setTexture("DiffuseMap", game.getAssetManager().loadTexture("Textures/dice_6.png"));
                    break;
            default: break;
        }             
        
        mat.setBoolean("UseMaterialColors",true); 
        mat.setColor("Diffuse",ColorRGBA.White);
        mat.setColor("Specular",ColorRGBA.White);        
        block.setMaterial(mat);
        
        block.setUserData("Value", num);
        blockNode.attachChild(block);
        game.getRootNode().attachChild(blockNode);
        block.setLocalTranslation(pos[0], pos[1], pos[2]);        
        block_phy = new RigidBodyControl(1f);
        block_phy.setMass(mass);
        block.addControl(block_phy);    
        bulletAppState.getPhysicsSpace().add(block_phy);        
        
        blockLight = new PointLight();        
        LightControl lightControl = new LightControl(blockLight);
        block.addControl(lightControl);
        blockLight.setColor(ColorRGBA.White);
        blockLight.setRadius(8f);
        
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
    
    public void enableLight(ColorRGBA color) {        
        mat.setColor("Diffuse", color);
        blockLight.setColor(color);
        blockNode.addLight(blockLight);
    }
    
    public void disableLight() {
        mat.setColor("Diffuse", ColorRGBA.White);
        blockNode.removeLight(blockLight);
    }
    
}
