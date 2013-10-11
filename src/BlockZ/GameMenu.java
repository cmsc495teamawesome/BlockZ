package BlockZ;

import com.jme3.system.AppSettings;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
    @author Team B - Bowes, R.J., Samonds, G., and Scuderi, M. 
 * CMSC495-6380 Professor Hung Dao
 */
public class GameMenu {
    
private GameBoard game = null;
    
public enum level{
    EASY,
    MEDIUM,
    DIFFICULT
}

    
public GameMenu()
{
    // TODO: Implement a visual control to present the choice between a few difficulty levels
    
    loadLevel(level.DIFFICULT);
}
    
private void loadLevel(level l)
{
    AppSettings settings = new AppSettings(true);
    settings.setTitle("BlockZ");
    settings.setEmulateMouse(true);
    
    switch (l) {
        case EASY:
            settings.setResolution(640, 480);
            game = new GameBoard(10, 14, (float) 1.2);
            break;
        case MEDIUM:
            settings.setResolution(640, 480);
            game = new GameBoard(7, 11, (float) 1.2);
            break;
        case DIFFICULT:
            settings.setResolution(960,720);
            game = new GameBoard(15, 25, (float) 1.2);
            break;

    }

    if(game!=null)
    {
        Logger.getLogger("").setLevel(Level.WARNING);
        game.setSettings(settings);
        game.setGameMenu(this);
        game.setPauseOnLostFocus(false);
        game.setShowSettings(false);
        game.start();
    }
}

public void handleGameOver(HashMap<String,String> results)
{
    game.stop();
    System.out.println("GAME OVER MOTHA FUCKA");
    for(Entry e:results.entrySet())
    {
        System.out.println(e.getKey().toString() + " = " + e.getValue().toString());
    }
    
    // TODO: Display the information in a pretty thing on the screen.
    
    // TODO: After "Game Over" screen, link them back to the main menu.
}

public static void main(String[] args) {
        GameMenu gm = new GameMenu();
    }
}


