/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BlockZ;

import com.jme3.scene.Node;
import java.util.HashMap;

/**
 *
 * @author tuc
 */
public class HandEvaluator {
        
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
        public int scoreChange;
        public int descentChange;
        public hands hand;
        
        public HandResult(int s, int d, hands h)
        {
            scoreChange = s;
            descentChange = d;
            hand = h;
        }
    }
    
    private GameBoard game; 
    
    public HandEvaluator(GameBoard g)
    {
        game = g;
    }
    
    public HandResult getHand()
    {   
        HashMap<Integer, Integer> values = new HashMap<Integer, Integer>();
        
        for(Laser l:game.lasers)
        {
            int value = l.getBlock();
            if(value != 0)
            {
                if(values.containsKey(value)) values.put(value, values.get(value));
                else values.put(value, 1);
            }
            System.out.print(Integer.toString(value));
        }
        
        System.out.println();
        
        return blockZ();
    }
    
    private HandResult blockZ()
    {
        return new HandResult(20000, -20, hands.BlockZ);
    }
    
    private HandResult fourOfAKind()
    {
        return new HandResult(15000, -15, hands.FourOfAKind);
    }
    
    private HandResult fullHouse()
    {
        return new HandResult(1000, -10, hands.FullHouse);
    }
    
    private HandResult threeOfAKind()
    {
        return new HandResult(5000, -5, hands.ThreeOfAKind);
    }
    
    private HandResult largeStraight()
    {
        return new HandResult(10000, -15, hands.LargeStraight);
    }
    
    private HandResult smallStraight()
    {
        return new HandResult(0, 0, hands.SmallStraight);
    }
    
    private HandResult chance()
    {
        return new HandResult(0, 5, hands.Chance);
    }
}
