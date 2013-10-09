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

/**
 * @author Team B - Bowes, R.J., Samonds, G., and Scuderi, M. 
 * CMSC495-6380 Professor Hung Dao
 * 
 * @version 0.1 
 */

public class BeamEffect extends AbstractControl{
    
    private GameBoard game;
    private BulletAppState physicsSpace;
    private float x1, y1, x2, y2;
    private float beamCount;
    private static Geometry beamFace;
    private float beamTime = 1.0f;
    private Material whiteMat;
    
    
    BeamEffect(GameBoard g, BulletAppState physicsIn, float x1In, float y1In, float x2In, float y2In)
    {
        game = g;
        physicsSpace = physicsIn;
        whiteMat = new Material(g.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        whiteMat.setColor("Color", ColorRGBA.White);
        x1 = x1In;
        y1 = y1In;
        
        //Temporarily draw squares at beam ends. To be replaced with connecting beam
        beamFace = new Geometry("beam", new Box(Vector3f.ZERO, 0.2f, 0.2f, 0.1f));        
        beamFace.setMaterial(whiteMat);
        beamFace.move(x1, y1, 0.0f);
        game.getRootNode().attachChild(beamFace);
        
        beamFace = new Geometry("beam", new Box(Vector3f.ZERO, 0.2f, 0.2f, 0.1f));        
        beamFace.setMaterial(whiteMat);
        beamFace.move(x2, y2, 0.0f);
        game.getRootNode().attachChild(beamFace);
        
    }
    
     // Update text fields on HUD
    @Override
    protected void controlUpdate(float tpf) {
        
        // If countdown timer up, clear message
        if((beamCount += tpf) > beamTime)
        {
            removeBeam();
            beamCount -= beamTime;
        }
        
        
    }
    
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }
    
    public Control cloneForSpatial(Spatial spatial) {
        BeamEffect control = new BeamEffect(game, physicsSpace, x1, y1, x2, y2);
        //TODO: copy parameters to new Control
        control.setSpatial(spatial);
        return control;
    }
   
    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        //InputCapsule in = im.getCapsule(this);
        //TODO: load properties of this Control, e.g.
        //this.value = in.readFloat("name", defaultValue);
    }
    
    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        //OutputCapsule out = ex.getCapsule(this);
        //TODO: save properties of this Control, e.g.
        //out.write(this.value, "name", defaultValue);
    }
    
    void removeBeam(){
    }
}