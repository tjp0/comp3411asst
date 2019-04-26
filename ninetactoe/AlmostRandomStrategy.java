package ninetactoe;

import java.util.ArrayList;
import java.util.Collections;
/* Picks the winning move if there is one; otherwise random */

public class AlmostRandomStrategy extends Strategy {
    public int getBestMove(Board b) {

        ArrayList<Integer> al = new ArrayList<Integer>();
        for(int i=1;i<=9;++i) { al.add(i); }
        Collections.shuffle(al);

        int to_win = b.getPlayer();

        ArrayList<Integer> validMoves;
        int valid_move = -1;

        for(int i : al) {
            if(b.canMakeMove(i))
            {
                int gameState = b.makeMoveCopyState(i).getGameState();
                if(gameState == to_win) {
                    return i;
                }
                valid_move = i;
            }
        }
        assert(valid_move != -1);
        return valid_move;
    }
}
