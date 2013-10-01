/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BlockZ;

import com.jme3.collision.CollisionResult;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author tuc
 */
public class HandEvaluator {
    private GameBoard game;
    
    public enum hands {
        BlockZ,
        FourOfAKind,
        FullHouse,
        ThreeOfAKind,
        LargeStraight,
        SmallStraight,
        Chance
    }
    
    public class HandResult {
        public int scoreChange = 0;
        public int descentChange = 0;
        public hands hand = hands.Chance;
    } 
    
    public HandEvaluator(GameBoard g)
    {
        game = g;
    }
    
    public HandResult getHand()
    {
        String debugText = new String("");
        Node lasers = (Node)game.getRootNode().getChild("LaserZ");
       
        // TODO: evaluate the hand and return a value
        
        HandResult result = new HandResult();
        return result;
    }
}
