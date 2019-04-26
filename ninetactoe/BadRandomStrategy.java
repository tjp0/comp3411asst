package ninetactoe;

import java.util.ArrayList;
import java.util.Collections;
/* Avoids picking a winning move */

public class BadRandomStrategy extends Strategy {
    public int getBestMove(Board b) {

        ArrayList<Integer> al = new ArrayList<Integer>();
        for(int i=1;i<=9;++i) { al.add(i); }
        Collections.shuffle(al);

        int to_win = b.getPlayer();

        ArrayList<Integer> validMoves;
        int bad_move = 0;
        int valid_move = 0;

        for(int i : al) {
            if(b.canMakeMove(i))
            {
                valid_move = i;
                int gameState = b.makeMoveCopyState(i).getGameState();
                if(gameState != to_win) {
                    bad_move = i;
                }
            }
        }

        if(bad_move != 0)
            return bad_move;
        assert(valid_move != 0);
        return valid_move;
    }
}
