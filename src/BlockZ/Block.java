
package BlockZ;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.LightControl;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;

/**
 * @author Team B - Bowes, R.J., Samonds, G., and Scuderi, M. 
 * CMSC495-6380 Professor Hung Dao
 * 
 * @version 1.0
 */
public class Block  {
    
    public Node blockNode = null;
    private Node projectileNode = new Node();
    private Node particleNode = new Node();
    
    int size, num; 
    String name;
    float mass;
    float[] position;
    float gameOverTimer;
    ColorRGBA color;
    
    private Geometry block;    
    private Geometry projectile;
    private GameBoard game;
    private BulletAppState bulletAppState;        
    private RigidBodyControl block_phy;
    
    Vector3f currentPosition;
    
    Material mat;
    PointLight blockLight;
    
    ParticleEmitter debris;
    ParticleEmitter debris1;
    
    private ArrayList<RigidBodyControl> projectilePhyList;    
    
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
        
        projectilePhyList = new ArrayList();
        
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
        
        blockNode = (Node) game.getRootNode().getChild("BlockZ");
        
        if(blockNode == null)
        {
            blockNode = new Node("BlockZ");
            game.getRootNode().attachChild(blockNode);
        }
        
        blockNode.attachChild(block);
        blockNode.attachChild(projectileNode);
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
        game.blockParticleList.add(this);
        game.blockParticleTime.add(System.currentTimeMillis());
        
        currentPosition = block.getLocalTranslation();
        
        makeProjectile(currentPosition.add(0.3f, 0.3f, 0f), new Vector3f(block_phy.getLinearVelocity()));
        makeProjectile(currentPosition.add(0.0f, 0.3f, 0f), new Vector3f(block_phy.getLinearVelocity()));
        makeProjectile(currentPosition.add(-0.3f, 0.3f, 0f), new Vector3f(block_phy.getLinearVelocity()));
        makeProjectile(currentPosition.add(0.3f, 0.0f, 0f), new Vector3f(block_phy.getLinearVelocity()));
        makeProjectile(currentPosition.add(-0.3f, 0.0f, 0f), new Vector3f(block_phy.getLinearVelocity()));
        makeProjectile(currentPosition.add(0.3f, -0.3f, 0f), new Vector3f(block_phy.getLinearVelocity()));
        makeProjectile(currentPosition.add(0.0f, -0.3f, 0f), new Vector3f(block_phy.getLinearVelocity()));
        makeProjectile(currentPosition.add(-0.3f, -0.3f, 0f), new Vector3f(block_phy.getLinearVelocity())); 
        
        emitParticles();
        blockNode.detachChild(block);
        bulletAppState.getPhysicsSpace().remove(block_phy);
    }
    
    private void makeProjectile(Vector3f position, Vector3f velocity) {
        
        projectile = new Geometry("Projectile", new Box(0.2f, 0.2f, 0.2f));
        projectile.setLocalTranslation(position);
        Material mat = new Material(game.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        mat.setTexture("DiffuseMap", game.getAssetManager().loadTexture("Textures/dice_blank.png"));
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Diffuse", color);
        projectile.setMaterial(mat);        
        projectileNode.attachChild(projectile);
        
        RigidBodyControl projectile_phy = new RigidBodyControl(1f);
        projectile_phy.setMass(0.2f);
        projectile.addControl(projectile_phy);
        bulletAppState.getPhysicsSpace().add(projectile_phy);
        projectilePhyList.add(projectile_phy);
        
        projectile_phy.setLinearVelocity(velocity);
        
        
    }
    
    public void emitParticles() {
        debris = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 5);
        Material matDebris = new Material(game.getAssetManager(), "Common/MatDefs/Misc/Particle.j3md");
        matDebris.setTexture("Texture", game.getAssetManager().loadTexture("Effects/Explosion/Debris.png"));        
        debris.setMaterial(matDebris);
        debris.setStartColor(color);
        debris.getParticleInfluencer().setInitialVelocity(block_phy.getLinearVelocity());
        debris.setLowLife(1.4f);
        debris.setHighLife(1.5f);
        debris.setParticlesPerSec(40);
        debris.getParticleInfluencer().setVelocityVariation(0.15f);
        particleNode.attachChild(debris);        
        debris.setLocalTranslation(block.getLocalTranslation());
        
        
        //Position of second particle emitter as a function of block's velocity
        debris1 = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 5);        
        debris1.setMaterial(matDebris);
        debris1.setStartColor(color);
        debris1.getParticleInfluencer().setInitialVelocity(block_phy.getLinearVelocity());
        debris1.setLowLife(0.99f);
        debris1.setHighLife(1f);
        debris1.setParticlesPerSec(40);
        debris1.getParticleInfluencer().setVelocityVariation(0.1f);
        particleNode.attachChild(debris1);
        debris1.setLocalTranslation(block.getLocalTranslation().add(block_phy.getLinearVelocity().mult(0.1f)));
        
        blockNode.attachChild(particleNode);
        
        
    }
    
    public void removeEffects() {        
        
         particleNode.detachAllChildren();
         blockNode.detachChild(particleNode);
         
         projectileNode.detachAllChildren();
         blockNode.detachChild(projectileNode);
         
        for (int i=0; i<projectilePhyList.size(); i++)
            bulletAppState.getPhysicsSpace().remove(projectilePhyList.get(i));
    }
    
    public boolean checkForGameOver(float tpf){
        if (    (block_phy.getPhysicsLocation().y >= game.y*8.5/10) & 
                ((block_phy.getLinearVelocity().y <= game.GAME_OVER_MOVE_THRESHHOLD) &
                 (block_phy.getLinearVelocity().y >= -game.GAME_OVER_MOVE_THRESHHOLD))){
            gameOverTimer += tpf;
            // Commented out troubleshooting line to see game over trigger
            //System.out.println("linear velocity " + this.name + " :" +block_phy.getLinearVelocity().y);
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
    
    public void enableLight(ColorRGBA col) {        
        color = col;
        mat.setColor("Diffuse", color);
        blockLight.setColor(color);
        blockNode.addLight(blockLight);
    }
    
    public void disableLight() {
        color = ColorRGBA.Gray;
        mat.setColor("Diffuse", ColorRGBA.White);
        blockNode.removeLight(blockLight);
    }
    
}
