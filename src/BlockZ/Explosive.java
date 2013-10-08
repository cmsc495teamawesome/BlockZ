
package BlockZ;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import java.util.ArrayList;

/**
    @author Team B - Bowes, R.J., Samonds, G., and Scuderi, M. 
 * CMSC495-6380 Professor Hung Dao
 */
public class Explosive {
    
    public static Node explosiveNode = new Node("BlockZ");
    private Node projectileNode = new Node();
    
    int size; 
    String name;
    float mass;
    float[] position;
    ColorRGBA color;
    Vector3f currentPosition;
    
    private Geometry explosive;    
    private GameBoard game;
    private BulletAppState bulletAppState;        
    private RigidBodyControl explosive_phy;
    
    ParticleEmitter fire;    
    
    private ArrayList<RigidBodyControl> projectilePhyList;
    
    public Explosive(GameBoard g, BulletAppState b, int s, int n, float m, float[] pos, ColorRGBA col) {
        
        size=s;
        name=Integer.toString(n);
        name = "Explosive " + name;
        mass=m;
        position=pos;
        color=col;
        game=g;
        bulletAppState=b;        
        
        projectilePhyList = new ArrayList();
        
        explosive = new Geometry(name, new Box(size, size, size));
        Material mat = new Material(game.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", col);
        explosive.setMaterial(mat);
        explosive.setUserData("Value", (n%6)+1);
        explosiveNode.attachChild(explosive);
        game.getRootNode().attachChild(explosiveNode);
        explosiveNode.attachChild(projectileNode);
        explosive.setLocalTranslation(pos[0], pos[1], pos[2]);        
        explosive_phy = new RigidBodyControl(1f);
        explosive_phy.setMass(mass);
        explosive.addControl(explosive_phy);    
        bulletAppState.getPhysicsSpace().add(explosive_phy);
        
    }  
    
    public void explode() {
        
        currentPosition = explosive.getLocalTranslation();
        
        explosiveNode.detachChild(explosive);
        bulletAppState.getPhysicsSpace().remove(explosive_phy);
        
        makeProjectile(currentPosition.add(0.2f, 0.2f, 0f), new Vector3f(20.0f, 20.0f, 0f));
        makeProjectile(currentPosition.add(0.0f, 0.2f, 0f), new Vector3f(0f, 20.0f, 0f));
        makeProjectile(currentPosition.add(-0.2f, 0.2f, 0f), new Vector3f(-20.0f, 20.0f, 0f));
        makeProjectile(currentPosition.add(0.2f, 0.0f, 0f), new Vector3f(20.0f, 0f, 0f));
        makeProjectile(currentPosition.add(-0.2f, 0.0f, 0f), new Vector3f(-20.0f, 0f, 0f));
        makeProjectile(currentPosition.add(0.2f, -0.2f, 0f), new Vector3f(20.0f, -20.0f, 0f));
        makeProjectile(currentPosition.add(0.0f, -0.2f, 0f), new Vector3f(0f, -20.0f, 0f));
        makeProjectile(currentPosition.add(-0.2f, -0.2f, 0f), new Vector3f(-20.0f, -20.0f, 0f));      
        
        emitParticles();
        
    }
    
    private void makeProjectile(Vector3f position, Vector3f velocity) {
        
        Geometry projectile = new Geometry("Projectile", new Sphere(16, 16, 0.1f));
        projectile.setLocalTranslation(position);
        Material mat = new Material(game.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Orange);
        projectile.setMaterial(mat);        
        projectileNode.attachChild(projectile);
        
        RigidBodyControl projectile_phy = new RigidBodyControl(1f);
        projectile_phy.setMass(10.0f);
        projectile.addControl(projectile_phy);
        bulletAppState.getPhysicsSpace().add(projectile_phy);
        projectilePhyList.add(projectile_phy);
        
        projectile_phy.setLinearVelocity(velocity);
        
    }
    
    public void emitParticles() {
        fire = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 1);
        Material matDebris = new Material(game.getAssetManager(), "Common/MatDefs/Misc/Particle.j3md");
        matDebris.setTexture("Texture", game.getAssetManager().loadTexture("Effects/Explosion/shockwave.png"));
        fire.setMaterial(matDebris);
        fire.setStartColor(ColorRGBA.Orange);
        fire.setLowLife(0.9f);
        fire.setHighLife(1f);
        fire.setEndSize(6f);
        fire.setParticlesPerSec(40);
        fire.getParticleInfluencer().setVelocityVariation(0f);
        explosiveNode.attachChild(fire);
        fire.setLocalTranslation(explosive.getLocalTranslation());
        
        
    }
    
    public void removeParticles() {        
        explosiveNode.detachChild(fire);        
    }
    
    public void removeProjectiles() {
        
        projectileNode.detachAllChildren();
        
        for (int i=0; i<projectilePhyList.size(); i++)
            bulletAppState.getPhysicsSpace().remove(projectilePhyList.get(i));
    }
    
}
