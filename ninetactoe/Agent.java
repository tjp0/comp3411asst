/* z3459006 COMP3411 assignment 3

This program attempts to play modified 9x9 tictactoe by implementing a alpha-beta pruning graph search.
The game state is kept track of in the Board class using an underlying multidimensional 9x9 array.
The standard evaluation loop of a move is
    if(canMakeMove()) { board.MakeMove(); EvaluateMove() UndoMove() }
This is designed to minimize copying and to try to optimize for performance.

Heuristics are provided in the TicTacToe class, which solves TicTacToe (as the state space of TicTacToe is quite small,
this is very fast) and provides a (hashmap) lookup table of estimated position strengths based on all possible future move permutations and evaluating which \
player has more in favor. The total score of all TicTacToe sub-boards can be added to provided a heuristic for AlphaBetaStrategy. In testing
against a number of hand written heuristics, evaluating all TicTacToe boards like this was the strongest heuristic and performed well.

AlphaBetaStrategy is where the main position evaluation code is located, As the name implies it performs AlphaBeta pruning.
Each position is evaluated for a score in the range -2.0 < x < 2.0, where a greater x is an advantage for Player 1 (X)
When abs(x) >= 1.0, this implies that this move will result in a win for the advantaged player, where abs(x) decreases as the win is further away.
This is used to prevent the algorithm from making bad moves in a situation that it believes is unwinnable
(in the event that the opponent plays nonoptimally, it may still be possible to win)

AlphaBetaStrategy performs iterative depth search with a timer. The initial depth that it computes to is based on the maximum depth computed
in its last move. In the event of not being able to complete a search within the time limit, it will perform an emergency search to depth 5.

Main data structures per class:
    AlphaBetaStrategy: State is handled via a recursive function.
    Board: The board state is kept track of in a 9x9 array of integers.
    TicTacToe (Heuristic lookup) heuristic lookups as performed via a hashmap.
 */


package ninetactoe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Agent {
    static final int TIME_PER_MOVE = 2000; /* Modify this to reduce the amount of time the agent uses per move */

    static Board board = new Board();
    static Strategy strategy = new AlphaBetaStrategy(TIME_PER_MOVE,  true, 1);
    static int myPlayer = -1;
    public static void main(String args[]) throws IOException {

        if(args.length < 2) {
            System.out.println("Usage: java Agent -p (port)");
            return;
        }

        final String host = "localhost";
        final int portNumber = Integer.parseInt(args[1]);

        Socket socket = new Socket(host, portNumber);
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        String line;

        while (true) {

            line = br.readLine();

            int move = parse(line);

            if(move == -1) {
                socket.close();
                return;
            }else if(move == 0){
                //TODO
            }else {
                board = board.makeMoveCopyState(move);
                System.out.print(board.toString());
                out.println(move);
            }

        }
    }

    public static int parse(String line) {
        System.out.println(line);
        if(line.contains("init")) {
            System.out.println(board.toString());
        }else if(line.contains("start")) {
            //TODO
        }else if(line.contains("second_move")) {

            int argsStart = line.indexOf("(");
            int argsEnd = line.indexOf(")");

            String list = line.substring(argsStart+1, argsEnd);
            String[] numbers = list.split(",");

            board.place(Integer.parseInt(numbers[0]),Integer.parseInt(numbers[1]), board.PLAYER1);
            board.setPlayer(Board.PLAYER2);

            myPlayer = Board.PLAYER2;
            System.out.println(board.toString());
            System.out.println("I am O");
            return strategy.getBestMove(board);

        }else if(line.contains("third_move")) {

            int argsStart = line.indexOf("(");
            int argsEnd = line.indexOf(")");

            String list = line.substring(argsStart+1, argsEnd);
            String[] numbers = list.split(",");

            board.place(Integer.parseInt(numbers[0]),Integer.parseInt(numbers[1]), Board.PLAYER1);
            board.place(Integer.parseInt(numbers[1]),Integer.parseInt(numbers[2]), Board.PLAYER2);
            board.setPlayer(Board.PLAYER1);

            myPlayer = Board.PLAYER1;
            System.out.println(board.toString());
            System.out.println("I am X");
            return strategy.getBestMove(board);

        }else if(line.contains("next_move")) {

            int argsStart = line.indexOf("(");
            int argsEnd = line.indexOf(")");
            String list = line.substring(argsStart+1, argsEnd);

            board = board.makeMoveCopyState(Integer.parseInt(list));
            System.out.println(board.toString());
            return strategy.getBestMove(board);


        }else if(line.contains("last_move")) {
            int argsStart = line.indexOf("(");
            int argsEnd = line.indexOf(")");
            String list = line.substring(argsStart+1, argsEnd);
            board = board.makeMoveCopyState(Integer.parseInt(list));

            System.out.print(board.toString());
            System.out.printf("I was player %s\n",(myPlayer == Board.PLAYER1 ? "X" : "O"));
        }else if(line.contains("win")) {
        }else if(line.contains("loss")) {
        }else if(line.contains("end")) {
            return -1;
        }
        return 0;
    }

}
