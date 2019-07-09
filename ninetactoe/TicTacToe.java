/* z3459006 COMP3411 assignment 3. See agent.java for overview */
package ninetactoe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class TicTacToe {

    public static final int EMPTY=0;
    public static final int PLAYER1=1;
    public static final int PLAYER2=2;

    private static HashMap<TicTacToe,Double> tttLookup = new HashMap<>();
    private static boolean states_generated = false;
    private static int uncached_lookups = 0;

    private int[] board;

    public TicTacToe(int[] board)  {
        this.board = board;
    }

    public TicTacToe() {
        board = new int[9];
    }

    private int isWon() {
        int[] sb = board;
        if(sb[0] != EMPTY && sb[0] == sb[1] && sb[1] == sb[2]) return sb[0];
        if(sb[3] != EMPTY && sb[3] == sb[4] && sb[4] == sb[5]) return sb[3];
        if(sb[6] != EMPTY && sb[6] == sb[7] && sb[7] == sb[8]) return sb[6];
        if(sb[0] != EMPTY && sb[0] == sb[3] && sb[3] == sb[6]) return sb[0];
        if(sb[1] != EMPTY && sb[1] == sb[4] && sb[4] == sb[7]) return sb[1];
        if(sb[2] != EMPTY && sb[2] == sb[5] && sb[5] == sb[8]) return sb[2];
        if(sb[0] != EMPTY && sb[0] == sb[4] && sb[4] == sb[8]) return sb[0];
        if(sb[2] != EMPTY && sb[2] == sb[4] && sb[4] == sb[6]) return sb[2];
        return EMPTY;
    }

    public Boolean isDraw() {
        for(int i=0;i<9;++i)
        {
            if(board[i] == EMPTY) return false;
        }
        return true;
    }

    public TicTacToe CopyAndMakeMove(int move, int player) {
        TicTacToe copied_board = new TicTacToe();
        copied_board.board = this.board.clone();
        copied_board.board[move-1] = player;
        return copied_board;
    }

    private TicTacToe Copy() {
        TicTacToe copied_board = new TicTacToe();
        copied_board.board = this.board.clone();
        return copied_board;
    }

    private boolean isValidMove(int i) {
        return board[i-1] == EMPTY;
    }

    /* Check if the heuristic is already in the cache; if it's not, compute it and return it */
    public double getHeuristic() {
        if(tttLookup.containsKey(this)) {
            return tttLookup.get(this);
        }
        double heuristic = getHeuristic_uncached();
        tttLookup.put(this.Copy(), heuristic);
        if(!states_generated) uncached_lookups++;
        return heuristic;
    }

    /* This heuristic is based on the percentage of all possible future states in favor for each player */
    /* 0 > X > 1 is in favor of Player1, -1 < X < 0 is in favour of player2 */
    private double getHeuristic_uncached() {
        if(isDraw()) {
            return 0.0;
        }
        int state = isWon();
        if(state == PLAYER1) {
            return 1.0;
        } else if(state == PLAYER2) {
            return -1.0;
        }

        double heuristic = 0.0;
        int valid_moves = 0;
        for(int i=1;i<=9;++i) {
            if(isValidMove(i)) {
                valid_moves += 2;
                TicTacToe b1 = this.CopyAndMakeMove(i, PLAYER1);
                TicTacToe b2 = this.CopyAndMakeMove(i, PLAYER2);
                heuristic += b1.getHeuristic();
                heuristic += b2.getHeuristic();
            }
        }

        return heuristic/valid_moves;
    }
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TicTacToe)) {
            return false;
        }
        return Arrays.equals(this.board,((TicTacToe)o).board);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.board);
    }

    public static void generate_states() {
        TicTacToe t = new TicTacToe();
        for(int i=1;i<=9;++i) {
            TicTacToe ti = t.CopyAndMakeMove(i,PLAYER1);
            TicTacToe tl = t.CopyAndMakeMove(i,PLAYER2);
            ti.getHeuristic();
            tl.getHeuristic();
        }
        states_generated = true;
    }

    public static int get_uncached_lookups() {
        return uncached_lookups;
    }
}
