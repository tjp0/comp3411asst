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

    public AlphaBetaStrategy(int maxTime, boolean enableHeuristic, int enableDebug) {
        this.enableDebug = enableDebug;
        this.enableHeuristic = enableHeuristic;
        this.maxTime = maxTime;


        if(enableHeuristic)
        {
            if(enableDebug >= 1) System.out.println("Generating heuristic states");
            //TicTacToe.generate_states();
            if(enableDebug >= 1) System.out.println("Heuristic states generated");
        }

        if(enableDebug >= 2)
            test();
    }

    public int getBestMove(Board b) {
        long deadline = System.currentTimeMillis() + maxTime;
        int depth = lastdepth-1;
        int best_move = 0;

        try {
            while (true) {
                best_move = getBestMoveLimited(b, depth, deadline);
                depth++;

                if(depth > 1000) break;
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

        return best_move;
    }

    private int getBestMoveLimited(Board b, int depth, long deadline) throws TimeoutException {
        states_evaluated = 0;
        ArrayList<Integer> bestMoves = new ArrayList<Integer>();
        Random r = new Random();
        double bestScore = Double.NEGATIVE_INFINITY;
        int worstMove = 0;
        double worstScore = Double.POSITIVE_INFINITY;
        for (int i=1;i<=9;++i) {
            if(b.canMakeMove(i)) {
                Board bCopy = b.makeMoveCopyState(i);
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
        int bestMove = bestMoves.get(r.nextInt(bestMoves.size()));
        assert(bestMove != 0);
        if(enableDebug >= 1) {
            System.out.printf("Playing for %s, Best Move %d,%d with valuation %f, worst move %d,%d with valuation %f, states evaluated: %d, depth: %d\n", (b.getPlayer() == Board.PLAYER1 ? "X" : "O"), b.getPrevMove(), bestMove, bestScore,b.getPrevMove(),worstMove,worstScore,getStatesEvaluated(),depth);
        }
        return bestMove;
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

    private double alphaBeta(Board b, int currentDepth, double alpha, double beta, int maximizingPlayer, long deadline) throws TimeoutException {
        if(deadline < System.currentTimeMillis())
            throw new TimeoutException();

        states_evaluated++;
        int gameState = b.getGameState();
        if (gameState == Board.PLAYER1) {
            return 1.0;
        } else if (gameState == Board.DRAW) {
            return 0.0;
        } else if (gameState == Board.PLAYER2) {
            return -1.0;
        }

        if (currentDepth == 0) {
            return heuristic(b);
        }

        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;

        int currentPlayer = b.getPlayer();

        for (int i = 1; i <= 9; ++i) {
            if (b.canMakeMove(i)) {
                b.makeMove(i);
                double score = alphaBeta(b, currentDepth - 1, alpha, beta, maximizingPlayer,deadline);
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

    private void test() {
        Board b = new Board();
        b.ImportFromString(" O . X | . . . | . . .\n" +
                        " . . X | X . . | O . O\n" +
                        " O . . | O . . | . . .\n" +
                        " ------+-------+------\n" +
                        " X . X | . X . | O . .\n" +
                        " . . . | . . . | . O .\n" +
                        " . O O | O . X | X X .\n" +
                        " ------+-------+------\n" +
                        " . O . | . . . | X . .\n" +
                        " X . X | . X . | O . O\n" +
                        " . . . | . O X | . . .");
        b.setPlayer(Board.PLAYER1);
        b.setPrevMove(7);
        System.out.println(b.toString());
        b.makeMove(getBestMove(b));
        System.out.println(b.toString());

    }
}
