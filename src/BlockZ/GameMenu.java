package BlockZ;

import com.jme3.system.AppSettings;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

/**
    @author Team B - Bowes, R.J., Samonds, G., and Scuderi, M. 
 * CMSC495-6380 Professor Hung Dao
 */
public class GameMenu{
    
public enum level {

    EASY,
    MEDIUM,
    DIFFICULT
}
private GameBoard game = null;

public GameMenu()
{
    JFrame mainMenu = new JFrame();
    mainMenu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainMenu.setSize(660, 350);
    mainMenu.setLocation(300, 300);   
    mainMenu.setLayout(new BorderLayout());
    
    JButton easyButton = new JButton("Easy");
    try {
        Image img = ImageIO.read(new File("assets/Textures/easy.png"));
        easyButton.setIcon(new ImageIcon(img));
        easyButton.setText("");
    } catch (IOException ex) {
    }
    easyButton.setFont(easyButton.getFont().deriveFont(40f));
    easyButton.setPreferredSize(new Dimension(mainMenu.getSize().width/3, mainMenu.getSize().height) );
    easyButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
               loadLevel(level.EASY);
            }
        });
    mainMenu.add(easyButton, BorderLayout.LINE_START);
    
    JButton mediumButton = new JButton("Medium");
    try {
        Image img = ImageIO.read(new File("assets/Textures/medium.png"));
        mediumButton.setIcon(new ImageIcon(img));
        mediumButton.setText("");
    } catch (IOException ex) {
    }
    easyButton.setFont(easyButton.getFont().deriveFont(40f));
    mediumButton.setPreferredSize(new Dimension(mainMenu.getSize().width/3, mainMenu.getSize().height) );
    mediumButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
               loadLevel(level.MEDIUM);
            }
        });
    mainMenu.add(mediumButton, BorderLayout.CENTER);
    
    JButton difficultButton = new JButton("Difficult");
    try {
        Image img = ImageIO.read(new File("assets/Textures/difficult.png"));
        difficultButton.setIcon(new ImageIcon(img));
        difficultButton.setText("");
    } catch (IOException ex) {
    }
    easyButton.setFont(easyButton.getFont().deriveFont(40f));
    difficultButton.setPreferredSize(new Dimension(mainMenu.getSize().width/3, mainMenu.getSize().height) );
    difficultButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
               loadLevel(level.DIFFICULT);
            }
        });
    mainMenu.add(difficultButton, BorderLayout.LINE_END);
    
    mainMenu.setVisible(true);
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
    game = null;
    System.out.println("THE VOLCANO WINS AGAIN!");
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


