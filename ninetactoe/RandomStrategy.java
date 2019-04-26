package ninetactoe;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

public class RandomStrategy extends Strategy {
    public int getBestMove(Board b) {

        ArrayList<Integer> al = new ArrayList<Integer>();
        for(int i=1;i<=9;++i) { al.add(i); }
        Collections.shuffle(al);

        for(int i : al) {
            if(b.canMakeMove(i))
            {
                return i;
            }
        }
        assert(false);
        return -1;
    }
}
