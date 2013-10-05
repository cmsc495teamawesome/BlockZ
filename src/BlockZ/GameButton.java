/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BlockZ;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Vector3f;

/**
 * @author Team B - Bowes, R.J., Samonds, G., and Scuderi, M. 
 * CMSC495-6380 Professor Hung Dao
 * 
 * @version 0.1 
 */

public class GameButton {
    
    private GameBoard game;
    private BulletAppState physicsSpace;
    private String buttonName;
    private int xPos, yPos, length, height;
    
    private static Geometry buttonFace;
    private RigidBodyControl face_phy;
    
    private Material greenMat;
    
    
    GameButton(GameBoard g, BulletAppState physicsIn, String name, int xIn, int yIn, int lengthIn, int heightIn, boolean active)
    {
        game = g;
        physicsSpace = physicsIn;
        greenMat = new Material(g.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        greenMat.setColor("Color", ColorRGBA.Green);
        length = lengthIn;
        height = heightIn;
        xPos = xIn;
        yPos = yIn;
        buttonName = name;
        
        buttonFace = new Geometry(buttonName, new Box(Vector3f.ZERO, length, height, 0.1f));        
        buttonFace.setMaterial(greenMat);
        buttonFace.move(xPos, yPos, 0.0f);
        
        face_phy = new RigidBodyControl(0.0f);        
        buttonFace.addControl(face_phy);
        
        
        
        if (active)
        {
            physicsSpace.getPhysicsSpace().add(face_phy);
            game.getRootNode().attachChild(buttonFace);
            
        }
    }
    
    public void enable()
    {
        game.getRootNode().detachChild(buttonFace);
        physicsSpace.getPhysicsSpace().remove(face_phy);
    }
    
    public void disable()
    {
        game.getRootNode().attachChild(buttonFace);
        physicsSpace.getPhysicsSpace().add(face_phy);
    }
    
}