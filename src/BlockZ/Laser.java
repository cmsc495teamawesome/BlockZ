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
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.scene.shape.Box;
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
    private static Node LaserZ = new Node("LaserZ");
    private CollisionResult currentTarget;
    private Boolean hasTarget = false;
    
    private static int _id = 0;
    
    private Material mat;
        
    private Vector3f position;
    private Vector3f normal = Vector3f.UNIT_Y;
        
    private ColorRGBA color = ColorRGBA.Magenta;
    private Geometry beam; 
    
    private float thickness = 0.05f;
    private float boundedMaximum = 28f;
    private float boundedLength;
    
    private Ray currentRay;
    
    private float tickCount = 0;
    private float tickLimit = 0.1f;
    
    public Laser(GameBoard g, Vector3f initialPosition)
    {
        super();
        game = g;
        position = initialPosition;
        _id++;

        mat = new Material(game.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        
        beam = new Geometry("Laser " + String.valueOf(_id), new Box(thickness,  boundedMaximum/2f, thickness));

        beam.center();
        beam.move(position.add(normal.mult(boundedMaximum/2f)));
        beam.rotateUpTo(normal);
        beam.setMaterial(mat);

        currentRay = new Ray(position, normal);
        
        resizeBeam();
        
        LaserZ.attachChild(beam);
        game.getRootNode().attachChild(LaserZ);
    }
    
    public void setColor(ColorRGBA c)
    {
        color = c;
    }
    
    public String getTarget()
    {
        if(currentTarget != null)
        {
            return currentTarget.getGeometry().getName();
        }
        else 
        {
            return "";
        }
    }
    
    public int getBlockValue()
    {
        Block returnBlock;
        
        if(currentTarget == null)
        {
            return 0;
        }
        else 
        {
            return currentTarget.getGeometry().getUserData("Value");
        }
    }
    
    public Boolean hasTarget()
    {
        return hasTarget;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        if((tickCount += tpf) > tickLimit)
        {
        resizeBeam();
            tickCount -= tickLimit;
    }
    }
    
    private void resizeBeam()
    {
        CollisionResults results = new CollisionResults();
        
        Spatial BlockZ = game.getRootNode().getChild("BlockZ"); 
                
        if(BlockZ==null) return;
        
        // Find intersections between Ray and BlockZ in results list.
        BlockZ.collideWith(currentRay, results);
        
        Material currentMat = mat.clone();
        currentMat.setColor("Color", color);
        
        if (results.size() > 0) {
            CollisionResult newTarget = results.getClosestCollision();
            
            if(currentTarget!=null)
            {
                if (!newTarget.getGeometry().getName().equals(currentTarget.getGeometry().getName())) {
                    game.getBlock(currentTarget.getGeometry().getName()).disableLight();
                    game.getBlock(newTarget.getGeometry().getName()).enableLight(color);
                }
            }
            else
            {
                game.getBlock(newTarget.getGeometry().getName()).enableLight(color);
            }
            
            currentTarget = newTarget;
            
            hasTarget = true;
            boundedLength = currentTarget.getDistance();
            currentMat.setColor("GlowColor", color);
        }
        else {
            if(currentTarget != null)
            {
                game.getBlock(currentTarget.getGeometry().getName()).disableLight();
            }
            currentTarget = null;
            boundedLength = boundedMaximum;
            hasTarget = false;
            currentMat.setColor("GlowColor", ColorRGBA.BlackNoAlpha);
        }
        
        beam.setMesh(new Box(thickness, boundedLength/2f, thickness));
        beam.setMaterial(currentMat);
        
        beam.center();
        beam.move(position.add(normal.mult(boundedLength/2f)));
        beam.rotateUpTo(normal);       
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }
    
    public Control cloneForSpatial(Spatial spatial) {
        Laser control = new Laser(game, position);
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
