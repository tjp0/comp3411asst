package ninetactoe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Agent {


    static Board board = new Board();

    static Strategy strategy = new AlphaBetaStrategy(2500,  true, 1);

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
            //TODO
        }else if(line.contains("loss")) {
            //TODO
        }else if(line.contains("end")) {
            return -1;
        }
        return 0;
    }

}
