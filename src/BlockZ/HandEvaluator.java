package BlockZ;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Team B - Bowes, R.J., Samonds, G., and Scuderi, M. 
 * CMSC495-6380 Professor Hung Dao
 */
public class HandEvaluator {
        
    public enum hands {
        BlockZ,
        FourOfAKind,
        FullHouse,
        ThreeOfAKind,
        LargeStraight,
        SmallStraight,
        Chance,
        NotAHand
    }
    
    public class HandResult {
        public int scoreChange;
        public int descentChange;
        public int houseValue;
        public hands hand;
        public ArrayList<Block> handBlocks; 
        
        public HandResult(int s, int d, hands h)
        {
            scoreChange = s;
            descentChange = d;
            hand = h;
            houseValue = 0;
            handBlocks = blocks;
        }
        
        public void addBlock(Block b)
        {
            blocks.add(b);
        }
    }
    
    private GameBoard game; 
    private ArrayList<Block> blocks;
    
    public HandEvaluator(GameBoard g)
    {
        game = g;
    }
    
    public HandResult getHand()
    {   
        blocks = new ArrayList<Block>();
        
        HashMap<Integer, Integer> values = new HashMap<Integer, Integer>();
        
        for(Laser l:game.lasers)
        {
            int value = l.getBlockValue();

            if(value != 0)
            {
                if(values.containsKey(value)) 
                {
                    values.put(value, values.get(value)+1);
                }
                else 
                {
                    values.put(value, 1);
                }
            }
            else
            {
                return notAHand();
            }
            
            blocks.add(game.getBlock(l.getTarget()));
        }
                
        if(values.containsValue(5)) 
        {
            return blockZ();
        }
        
        if(values.containsValue(4))
        {
            return fourOfAKind();
        }
        
        if(values.containsValue(3) && values.containsValue(2))
        {
            int houseValue = 0;
            for(Map.Entry entry:values.entrySet())
            {
                houseValue += ((Integer)entry.getKey())*((Integer)entry.getValue());
            }
            return fullHouse(houseValue);
        }
        
        if(values.containsValue(3))
        {
            return threeOfAKind();
        }
        
        if (values.containsKey(3) && values.containsKey(4)) 
        {
            if (values.containsKey(1) && values.containsKey(2) && values.containsKey(5)) {
                return largeStraight();
            }

            if (values.containsKey(2) && values.containsKey(5) && values.containsKey(6)) {
                return largeStraight();
            }

            if (values.containsKey(1) && values.containsKey(2)) {
                return smallStraight();
            }

            if (values.containsKey(2) && values.containsKey(5)) {
                return smallStraight();
            }

            if (values.containsKey(5) && values.containsKey(6)) {
                return smallStraight();
            }
        }
        
        return chance();
    }
    
    private HandResult blockZ()
    {
        return new HandResult(20000, -20, hands.BlockZ);
    }
    
    private HandResult fourOfAKind()
    {
        return new HandResult(15000, -15, hands.FourOfAKind);
    }
    
    private HandResult fullHouse(int value)
    {
        HandResult hr = new HandResult(1000, -10, hands.FullHouse);
        hr.houseValue = value;
        return hr;
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
    
    private HandResult notAHand()
    {
        return new HandResult(0, 0, hands.NotAHand);
    }
}
