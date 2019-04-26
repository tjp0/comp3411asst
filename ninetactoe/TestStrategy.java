package ninetactoe;

public class TestStrategy {
    public static void main(String args[]) {

        Strategy s = new AlphaBetaStrategy(5, true, 1);

        Board b = new Board();
        b.ImportFromString( " . . . | . . . | . . X\n" +
                            " . . . | . X . | . . .\n" +
                            " X . . | . X . | . . .\n" +
                            " ------+-------+------\n" +
                            " . . . | O . . | . . .\n" +
                            " . . . | . . . | . . .\n" +
                            " . . . | . . . | . . .\n" +
                            " ------+-------+------\n" +
                            " . . O | . O . | . . .\n" +
                            " . . . | . . . | . . .\n" +
                            " . . . | . . . | . . .\n");
        b.setPrevMove(3);
        b.setPlayer(Board.PLAYER2);

        System.out.print(b.toString());
        b.makeMove(s.getBestMove(b));
        System.out.print(b.toString());

    }
}
