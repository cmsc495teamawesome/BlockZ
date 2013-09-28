/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BlockZ;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.scene.debug.WireBox;
import java.io.IOException;

/**
 * @author Team B - Bowes, R.J., Samonds, G., and Scuderi, M. 
 * CMSC495-6380 Professor Hung Dao
 * 
 * @version 0.1 
 */
public class Laser extends AbstractControl {
    //Any local variables should be encapsulated by getters/setters so they
    //appear in the SDK properties window and can be edited.
    //Right-click a local variable to encapsulate it with getters and setters.
    private GameBoard game;
    private static int _id = 1;
    private ColorRGBA color = ColorRGBA.White;
    
    private Vector3f position = Vector3f.ZERO;
    private Vector3f normal = Vector3f.UNIT_Y;
    
    private Geometry beam; 
    
    private float boundedMaximum = 10f;
    private float boundedLength;
    
    private Ray currentRay = new Ray(Vector3f.ZERO, Vector3f.UNIT_Y);
    
    private float tickCount = 0;
    private int tickLimit = 5;
    
    public Laser(GameBoard g)
    {
        super();
        
        game = g;

        beam = new Geometry("Laser " + String.valueOf(_id), new WireBox(.1f, boundedMaximum, .1f));

//        beam = new Geometry("Laser " + String.valueOf(_id), new Cylinder(4, 8, 1f, boundedMaximum));
        
        Material mat = new Material(game.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        
  //      beam.
        
        beam.setMaterial(mat);
        beam.center();
        beam.move(position);
        beam.rotateUpTo(normal);
        
        game.getRootNode().attachChild(beam);
        
        resizeBeam();
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        if((tickCount += tpf) > tickLimit)
        {
            //TODO: add code that controls Spatial,
            //e.g. spatial.rotate(tpf,tpf,tpf);
            resizeBeam();
            System.out.println(String.valueOf(boundedLength));
            tickCount -= tickLimit;
        }
    }
    
    public void resizeBeam()
    {
        // 1. Reset results list.
        CollisionResults results = new CollisionResults();
        
        Spatial BlockZ = game.getRootNode().getChild("BlockZ"); 
                
        // 3. Collect intersections between Ray and Shootables in results list.
        BlockZ.collideWith(currentRay, results);
        
        // 4. Print the results
        System.out.println("----- Collisions? " + results.size() + "-----");
        for (int i = 0; i < results.size(); i++) {
            // For each hit, we know distance, impact point, name of geometry.
            float dist = results.getCollision(i).getDistance();
            Vector3f pt = results.getCollision(i).getContactPoint();
            String hit = results.getCollision(i).getGeometry().getName();
            System.out.println("* Collision #" + i);
            System.out.println("  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
        }
        
        if (results.size() > 0) {
            // The closest collision point is what was truly hit:
            CollisionResult closest = results.getClosestCollision();
            boundedLength = closest.getDistance();
            
            beam.setMesh(new WireBox(.1f, boundedLength, .1f));
            
            /*
            Material laserMat = mat.clone();
            laserMat.setColor("Color", laserColor);
            laserMat.setColor("GlowColor", laserColor);
            this.setMaterial(laserMat);
            */
        }
        else {
            boundedLength = boundedMaximum;
            //no glow
        }
        
        
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }
    
    public Control cloneForSpatial(Spatial spatial) {
        Laser control = new Laser(game);
        //TODO: copy parameters to new Control
        control.setSpatial(spatial);
        return control;
    }
    
    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule in = im.getCapsule(this);
        //TODO: load properties of this Control, e.g.
        //this.value = in.readFloat("name", defaultValue);
    }
    
    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule out = ex.getCapsule(this);
        //TODO: save properties of this Control, e.g.
        //out.write(this.value, "name", defaultValue);
    }
}
