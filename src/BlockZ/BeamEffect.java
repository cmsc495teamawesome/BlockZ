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
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;
import com.jme3.math.FastMath;
import com.jme3.scene.Node;

/**
 * @author Team B - Bowes, R.J., Samonds, G., and Scuderi, M. 
 * CMSC495-6380 Professor Hung Dao
 * 
 * @version 0.1 
 */

public class BeamEffect{
    
    private GameBoard game;
    //private BulletAppState physicsSpace;
    private float x1, y1, x2, y2;
    private static Geometry beamFace;
    private float ttl = 1.0f;
    private float ttlCounter = 0.0f;
    private Material whiteMat;
    private String orientation;
    private String name;
    private float length;
    private int beamID;
    //private static int beamCount = 0;
    //private static Node beamNode = new Node();
    
    BeamEffect(GameBoard g, float x1In, float y1In, float x2In, float y2In, String orientIn, int beamCount)
    {
        //beamCount++;
        game = g;
        //physicsSpace = physicsIn;
        whiteMat = new Material(g.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        whiteMat.setColor("Color", ColorRGBA.White);
        x1 = x1In;
        y1 = y1In;
        x2 = x2In;
        y2 = y2In;
        beamID = beamCount;
        orientation = orientIn;
        name = "Beam" + beamCount;
        
        Vector3f point1 = new Vector3f(x1, y1, 0.0f);
        Vector3f point2 = new Vector3f(x2, y2, 0.0f);
        length = point1.distance(point2);
        /*Temporarily draw squares at beam ends. To be replaced with connecting beam
        beamFace = new Geometry("beam", new Box(Vector3f.ZERO, 0.2f, 0.2f, 0.1f));        
        beamFace.setMaterial(whiteMat);
        beamFace.move(x1, y1, 0.0f);
        game.getRootNode().attachChild(beamFace);
        
        beamFace = new Geometry("beam", new Box(Vector3f.ZERO, 0.2f, 0.2f, 0.1f));        
        beamFace.setMaterial(whiteMat);
        beamFace.move(x2, y2, 0.0f);
        game.getRootNode().attachChild(beamFace);*/
        
        
        //Draw beam across gameboard between (x1, y1) and (x2, y2)
        if (orientation.equals("horizontal")){
            beamFace = new Geometry(name, new Box(Vector3f.ZERO, length/2, 0.1f, 0.1f));
            beamFace.setMaterial(whiteMat);
            beamFace.move(game.xOffset, (y2+y1)/2, 2.0f);
            beamFace.rotate(0.0f, 0.0f, (float)FastMath.atan((y2-((y1+y2)/2))/(x2-game.xOffset)));
            //game.getRootNode().attachChild(beamFace);
        }
        
        if (orientation.equals("vertical")){
            beamFace = new Geometry(name, new Box(Vector3f.ZERO, 0.1f, length/2, 0.1f));
            beamFace.setMaterial(whiteMat);
            beamFace.move((x1+x2)/2.0f,(y1+y2)/2.0f,2.0f);
            beamFace.rotate(0.0f, 0.0f, (float)FastMath.atan(-1*(x2-((x1+x2)/2))/(y2-(y1+y2)/2)));
            //game.getRootNode().attachChild(beamFace);
        }
        
        //game.getRootNode().attachChild(beamNode);
    }
    
     // Update text fields on HUD
    public boolean isItAlive(float tpf) {
        
        System.out.println(name + " time: " + ttlCounter);
        
        // If countdown timer up, clear message
        if((ttlCounter += tpf) > ttl)
        {
            System.out.println(name + ": in detach");
            //game.getRootNode().detachChild(beamFace);
            return false;
        }
        return true;
    }
    
    public Geometry getGeom(){
        return beamFace;
    }
    
}