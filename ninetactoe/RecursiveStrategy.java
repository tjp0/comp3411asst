package ninetactoe;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class RecursiveStrategy extends Strategy {

    private int maxDepth = 0;
    private int enableDebug = 0;

    public RecursiveStrategy(int maxDepth, int enableDebug) {
        this.maxDepth = maxDepth;
        this.enableDebug = enableDebug;

        if(enableDebug >= 1)
            test();
    }

    public int getBestMove(Board b) {
        states_evaluated = 0;
        ArrayList<Integer> bestMoves = new ArrayList<Integer>();
        Random r = new Random();
        double bestScore = -9999999.00;
        int worstMove = 0;
        double worstScore = 9999999.00;
        for (int i=1;i<=9;++i) {
            if(b.canMakeMove(i)) {
                Board bCopy = b.makeMoveCopyState(i);
                double curScore = -evaluatePosition(bCopy,0);
                if(enableDebug >= 2) {
                    System.out.println(bCopy.toString());
                    System.out.printf("Playing for %s, Move %d,%d with valuation %f\n",(b.getPlayer() == Board.PLAYER1 ? "X" : "O"),b.getPrevMove(),i,curScore);
                }
                if(curScore >= bestScore) {
                    bestMoves.add(i);
                    bestScore = curScore;
                }
                if(curScore < worstScore) {
                    worstMove = i;
                    worstScore = curScore;
                }
            }
        }
        int bestMove = bestMoves.get(r.nextInt(bestMoves.size()));
        assert(bestMove != 0);
        if(enableDebug >= 1) {
            System.out.printf("Playing for %s, Best Move %d,%d with valuation %f, worst move %d,%d with valuation %f, states evaluated %d\n", (b.getPlayer() == Board.PLAYER1 ? "X" : "O"), b.getPrevMove(), bestMove, bestScore,b.getPrevMove(),worstMove,worstScore,getStatesEvaluated());
        }
        return bestMove;
    }


    private double evaluatePosition(Board b, int currentDepth) {
        states_evaluated++;
        if(b.getGameState() == b.getPlayer()) {
            assert(false);
            return 1.0; /* You caused other player to win; shouldn't ever happen */
        }
        else if(b.getGameState() == b.otherPlayer()) {
            return -1.0; /* Game state was won for the player with the last move */
        }

        if(currentDepth == this.maxDepth) {
            return 0.0;
        }

        double maxScore = -2.0;

        for(int i=1;i<=9;++i) {
            if(b.canMakeMove(i)) {
                b.makeMove(i);
                double score = -evaluatePosition(b, currentDepth+1);
                if(score > maxScore)
                {
                    maxScore = score;
                }
                b.undoMove();
            }
        }

        return maxScore;
    }


    private void test() {
        Board b = new Board();
        b.place(1, 1, Board.PLAYER2);
        b.place(1, 2, Board.PLAYER2);
        b.place(1, 4, Board.PLAYER2);
        b.place(1, 5, Board.PLAYER2);

        b.setPlayer(Board.PLAYER1);
        System.out.println(b.toString());
        Board b2 = b.Copy();
        getBestMove(b);
        assert(b2.equals(b));
        System.out.println(b.toString());
    }
}
