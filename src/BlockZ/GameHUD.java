/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BlockZ;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;
import com.jme3.font.BitmapText;
import com.jme3.font.BitmapFont;

/**
 * @author Team B - Bowes, R.J., Samonds, G., and Scuderi, M. 
 * CMSC495-6380 Professor Hung Dao
 * 
 * @version 0.1 
 */
public class GameHUD extends AbstractControl {
    //Any local variables should be encapsulated by getters/setters so they
    //appear in the SDK properties window and can be edited.
    //Right-click a local variable to encapsulate it with getters and setters.
    private GameBoard game;
    private static Node GameHUD = new Node("GameHUD");
   
    private Vector3f position;
    
    private static int _id = 0;
    
    private ColorRGBA color = ColorRGBA.White;
       
    //Tick timers for temporary HUD messages
    private float messageCount = 0;
    private float messageTime = 2.0f;
    private float timeTicker = 0;
    
    
    private long score = 0;
    private int time = 0;
    private int rate;
    //private StringBuilder message = new StringBuilder(64);
    
    private BitmapFont hudFont; 
    BitmapText timeDisplay;  
    BitmapText scoreDisplay;
    BitmapText rateDisplay; 
    BitmapText messageDisplay;
    
    public GameHUD(GameBoard g, long startScore, int startTime, int startRate)
    {
        super();
        game = g;
        _id++;
        score = startScore;
        time = startTime;
        rate = startRate;
        
        hudFont = game.getAssetManager().loadFont("Interface/Fonts/Console.fnt");
        
        timeDisplay = new BitmapText(hudFont, false);  
        scoreDisplay = new BitmapText(hudFont, false);
        rateDisplay = new BitmapText(hudFont, false); 
        messageDisplay = new BitmapText(hudFont, false);
    
        game.getGuiNode().detachAllChildren();
        game.setDisplayFps(false);
        game.setDisplayStatView(false);
        
        
                  
        scoreDisplay.setSize(hudFont.getCharSet().getRenderedSize());      // font size
        scoreDisplay.setColor(color);                             // font color
        scoreDisplay.setText("Score: " + score); 
        scoreDisplay.setLocalTranslation(50, 300, 0); // position
        game.getGuiNode().attachChild(scoreDisplay);
        
                 
        rateDisplay.setSize(hudFont.getCharSet().getRenderedSize());      // font size
        rateDisplay.setColor(color);                             // font color
        rateDisplay.setText("Rate: " + rate); 
        rateDisplay.setLocalTranslation(50, 250, 0); // position
        game.getGuiNode().attachChild(rateDisplay);
        
                
        timeDisplay.setSize(hudFont.getCharSet().getRenderedSize());      // font size
        timeDisplay.setColor(color);                             // font color
        timeDisplay.setText("Time: " + time); 
        timeDisplay.setLocalTranslation(50, 200, 0); // position
        game.getGuiNode().attachChild(timeDisplay);
        
        messageDisplay.setSize(hudFont.getCharSet().getRenderedSize());      // font size
        messageDisplay.setColor(color);                             // font color
        messageDisplay.setText(""); 
        messageDisplay.setLocalTranslation(50, 150, 0); // position
        game.getGuiNode().attachChild(messageDisplay);
        
    }
    
    // Set temporary status message
    public void displayMessage(String message)
    {
        // Set text for displayMessage
        messageDisplay.setText(message);
        
        // Reset counters for message timer
        messageCount = 0;
    }
    
    public void updateScore(long newScore)
    {
        score = newScore;
    }
    
    public void updateRate(int newRate)
    {
        rate = newRate;
    }
    
    public void updateTime(int timeChange)
    {
        time+= timeChange;
    }
    
    public void setColor(ColorRGBA c)
    {
        color = c;
    }
    
    
    // Update text fields on HUD
    @Override
    protected void controlUpdate(float tpf) {
        
        // Keep score, rate, and time current
            scoreDisplay.setText("Score: " + score);
            rateDisplay.setText("Rate: " + rate);
            timeDisplay.setText("Time: " + time);
         
        // If countdown timer up, clear message
        if((messageCount += tpf) > messageTime)
        {
            messageDisplay.setText("");
            
            messageCount -= messageTime;
        }
        
        if((timeTicker += tpf) > 1.0f)
        {
            updateTime(1);
            timeTicker -= 1.0f;
        }
    }
    
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }
    
    public Control cloneForSpatial(Spatial spatial) {
        GameHUD control = new GameHUD(game, score, time, rate);
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
}
