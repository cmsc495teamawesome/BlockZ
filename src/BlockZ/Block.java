
package BlockZ;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Box;

/**
 *
 * @author Greg
 */
public class Block  {
    
    int size; 
    String name;
    float mass;
    float[] position;
    ColorRGBA color;
    
    private Geometry block;    
    private GameBoard game;
    private BulletAppState bulletAppState;        
    private RigidBodyControl block_phy;
    
    public Block(GameBoard g, BulletAppState b, int s, int n, float m, float[] pos, ColorRGBA col) {
        
        size=s;
        name=Integer.toString(n);
        name = "Block " + name;
        mass=m;
        position=pos;
        color=col;
        game=g;
        bulletAppState=b;        
        
        block = new Geometry(name, new Box(size, size, size));
        Material mat = new Material(game.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", col);
        block.setMaterial(mat);
        game.getRootNode().attachChild(block);
        block.setLocalTranslation(pos[0], pos[1], pos[2]);        
        block_phy = new RigidBodyControl(1f);
        block_phy.setMass(mass);
        block.addControl(block_phy);    
        bulletAppState.getPhysicsSpace().add(block_phy);
        
    }  
    
    public void removeBlock() {
        game.getRootNode().detachChild(block);
        bulletAppState.getPhysicsSpace().remove(block_phy);
    }
    
}
