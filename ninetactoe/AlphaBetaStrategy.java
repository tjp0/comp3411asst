/* z3459006 COMP3411 assignment 3. See agent.java for overview */
package ninetactoe;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class AlphaBetaStrategy extends Strategy {

    private int maxDepth = 0;
    private int enableDebug = 0;
    private boolean enableHeuristic = true;
    private long maxTime = 3000;

    private int lastdepth = 5;

    private class movescore {
        public int move;
        public double score;
        public movescore(int move, double score) { this.move = move; this.score = score; }
    }

    public AlphaBetaStrategy(int maxTime, boolean enableHeuristic, int enableDebug) {
        this.enableDebug = enableDebug;
        this.enableHeuristic = enableHeuristic;
        this.maxTime = maxTime;

        if(enableHeuristic)
        {
            if(enableDebug >= 1) System.out.println("Generating heuristic states");
            TicTacToe.generate_states();
            if(enableDebug >= 1) System.out.println("Heuristic states generated");
        }
    }

    /* Returns the best move by doing an iterative depth search. Keeps track of the time
    and will abort the search when it runs out, returning the best move from the last completed search
     */
    public int getBestMove(Board b) {
        long deadline = System.currentTimeMillis() + maxTime;
        int depth = lastdepth-1;
        int best_move = -1;

        try {
            while (true) {
                movescore s = getBestMoveLimited(b, depth, deadline);
                if(s.move != -1) best_move = s.move;
                depth++;
                if(s.score >= 1.0 || s.score <= -1.0) break;
            }
        } catch (TimeoutException e) {
            if(enableDebug >= 1)
            {
                lastdepth = depth-1;
                System.out.println("Time Expired!");
            }

        }

        if(enableDebug >= 1 && enableHeuristic)
            System.out.printf("%d uncached heuristic lookups\n",TicTacToe.get_uncached_lookups());


        if(best_move == -1) {
            try {
                return (getBestMoveLimited(b, 5, Long.MAX_VALUE)).move; /* In case time expired and we don't have a valid move
                 * get one quickly */
            } catch (TimeoutException t) {
                return -1; /* This shouldn't ever happen. */
            }
        }
        return best_move;
    }
    /* Gets the best move and a score for a specific depth. */
    private movescore getBestMoveLimited(Board b, int depth, long deadline) throws TimeoutException {
        states_evaluated = 0;
        ArrayList<Integer> bestMoves = new ArrayList<Integer>();
        Random r = new Random();
        double bestScore = Double.NEGATIVE_INFINITY;
        int worstMove = 0;
        double worstScore = Double.POSITIVE_INFINITY;
        for (int i=1;i<=9;++i) {
            if(b.canMakeMove(i)) {
                Board bCopy = b.makeMoveCopyState(i); /* Simulate the move on a copy of the game board */
                double curScore = alphaBeta(bCopy,depth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Board.PLAYER1, deadline);

                if(b.getPlayer() == Board.PLAYER2) curScore=-curScore;
                if(enableDebug >= 2) {
                    System.out.println(bCopy.toString());
                    System.out.printf("Playing for %s, Move %d,%d with valuation %f\n",(b.getPlayer() == Board.PLAYER1 ? "X" : "O"),b.getPrevMove(),i,curScore);
                }

                if(curScore >= bestScore) {
                    if(curScore > bestScore)
                        bestMoves.clear();
                    bestMoves.add(i);
                    bestScore = curScore;
                }
                if(curScore <= worstScore) {
                    worstMove = i;
                    worstScore = curScore;
                }
            }
        }
        int bestMove = -1;
        if(bestMoves.size() > 0)
            bestMove = bestMoves.get(r.nextInt(bestMoves.size()));
        if(enableDebug >= 1) {
            System.out.printf("Playing for %s, Best Move %d,%d with valuation %f, worst move %d,%d with valuation %f, states evaluated: %d, depth: %d\n", (b.getPlayer() == Board.PLAYER1 ? "X" : "O"), b.getPrevMove(), bestMove, bestScore,b.getPrevMove(),worstMove,worstScore,getStatesEvaluated(),depth);
        }
        return new movescore(bestMove,bestScore);
    }



    private double heuristic(Board b) {
        if(!enableHeuristic) return 0.0;
        double heuristic = 0.0;
        for(int i=1;i<=9;++i) {
            TicTacToe t = new TicTacToe(b.getSubBoard(i));
            heuristic += t.getHeuristic();
        }
        return heuristic / 9.0;
    }
    /* Evaluate the score of a given board state. maximizingPlayer is used to keep track of the player trying to maximize */
    private double alphaBeta(Board b, int currentDepth, double alpha, double beta, int maximizingPlayer, long deadline) throws TimeoutException {
        if(deadline < System.currentTimeMillis())
            throw new TimeoutException();

        states_evaluated++;
        int gameState = b.getGameState();
        /* Check if the game is over. 2.0/-2.0 is the evaluated score for an immediate win */
        if(gameState != Board.IN_PROGRESS) {
            switch (gameState) {
                case Board.PLAYER1:
                    return 2.0;
                case Board.PLAYER2:
                    return -2.0;
                case Board.DRAW:
                    return 0.0;
            }
        }
        /* We've reached the maximum depth we want to evaluate, so just return a simple heuristic */
        if (currentDepth == 0) {
            return heuristic(b);
        }

        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;

        int currentPlayer = b.getPlayer();

        /* We check if a move is possible; if it is we update the board state and then recursively call and undo it once we're done
        evaluating. Using the same board for all evaluations and minimizing copies helps performance significantly.
         */
        for (int i = 1; i <= 9; ++i) {
            if (b.canMakeMove(i)) {
                b.makeMove(i);
                double score = alphaBeta(b, currentDepth - 1, alpha, beta, maximizingPlayer,deadline);

                if(score > 1.0) { /* Anything > 1 / < -1 is already a win, but we want to prioritize quicker wins and slower losses */
                    score = Math.pow(score, 0.95);
                }
                else if(score < -1.0)
                {
                    score = -Math.pow(-score, 0.95);
                }

                minimum = Math.min(minimum, score);
                maximum = Math.max(maximum, score);

                if (currentPlayer == maximizingPlayer)
                    alpha = Math.max(alpha, maximum);
                else
                    beta = Math.min(beta, minimum);

                if(enableDebug >= 3) {
                    System.out.println(b.toString());
                    System.out.printf("Playing for %s, Move %d,%d with valuation %f\n",(maximizingPlayer == Board.PLAYER1 ? "X" : "O"),b.getPrevMove(),i,score);
                }
                b.undoMove();
                if (alpha >= beta)
                    break;
            }
        }

        if(currentPlayer == maximizingPlayer)
            return maximum;
        else
            return minimum;
    }
}
