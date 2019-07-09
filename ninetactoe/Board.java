/* z3459006 COMP3411 assignment 3. See agent.java for overview */
/* Note that internally, all moves are shifted down. A game move of 1 on board 1 refers to the index position 0,0 */
/* All internal datastructures use 0 indexed positions, all external functions, 1 indexed */
package ninetactoe;

import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class Board {

    public static final int EMPTY=0;
    public static final int PLAYER1=1;
    public static final int PLAYER2=2;
    public static final int DRAW=0;
    public static final int IN_PROGRESS=-1;

    public int[][] boards = new int[9][9];
    private int prevMove = 0;
    public int currentPlayer = 0;

    private ArrayList<Integer> moveHistory = new ArrayList<Integer>();

    public void place(int board, int num, int player) {

        prevMove = num-1;
        boards[board-1][num-1] = player;
    }

    public boolean canMakeMove(int number) {
        assert(number >= 1 && number <= 9);

        number = number - 1;
        if (boards[prevMove][number] != EMPTY)
            return false;
        return true;
    }

    private void switchPlayer() {
        if(currentPlayer == PLAYER1) {
            currentPlayer = PLAYER2;
            return;
        }
        currentPlayer = PLAYER1;
    }

    public Board makeMoveCopyState(int number) {
        Board b = (Board) this.Copy();
        b.makeMove(number);
        return b;
    }

    public void makeMove(int number) {
        number = number - 1;
        moveHistory.add(number);
        boards[prevMove][number] = currentPlayer;
        prevMove = number;
        switchPlayer();
    }

    public void undoMove() {
        int thisMove = moveHistory.remove(moveHistory.size()-1);
        prevMove = moveHistory.get(moveHistory.size()-1);
        boards[prevMove][thisMove] = Board.EMPTY;
        switchPlayer();
    }
    public void setPrevMove(int move) {
        this.prevMove = move-1;
    }

    public void setPlayer(int player) {
        currentPlayer = player;
    }
    public int getPlayer() {
        return currentPlayer;
    }
    public int otherPlayer() {
        if(currentPlayer == Board.PLAYER1)
            return Board.PLAYER2;
        return Board.PLAYER1;
    }

    public int getPrevMove() {
        return prevMove+1;
    }

    public Board Copy() {
        Board copied_board = new Board();
        copied_board.boards = this.boards.clone();
        for(int i=0;i<9;++i) {
            copied_board.boards[i] = this.boards[i].clone();
        }

        copied_board.moveHistory = (ArrayList<Integer>) this.moveHistory.clone();

        copied_board.currentPlayer = this.currentPlayer;
        copied_board.prevMove = this.prevMove;
        return copied_board;
    }

    @Override /* Returns a nicely formatted board to look at :) */
    public String toString() {
        StringBuilder s = new StringBuilder();

        for (int y = 0; y < 9; ++y) {
            s.append(" ");
            if (y == 3 || y == 6) {
                s.append("------+-------+------\n ");
            }

            for (int x = 0; x < 9; ++x) {
                if (x == 3 || x == 6) {
                    s.append("| ");
                }

                int board_idx = y - y % 3 + x / 3;
                int pos_idx = (y % 3) * 3 + x % 3;
                int val = boards[board_idx][pos_idx];

                if (val == Board.PLAYER1) s.append("X");
                if (val == Board.PLAYER2) s.append("O");
                if (val == Board.EMPTY) s.append(".");

                if (x != 8) {
                    s.append(" ");
                }
            }
            s.append("\n");
        }
        return s.toString();
    }

    /* Returns if a given TicTacToe board has been won */
    public int isSubBoardWon(int[] sb) {
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

    public int[] getSubBoard(int subboard) {
        return boards[subboard-1];
    }

    private Boolean isDrawSubboard(int sb[]) {
        for(int i=0;i<9;++i) {
            if(sb[i] == EMPTY) return false;
        }
        return true;
    }

    public Boolean isDraw() {
        if (moveHistory.size() >= 2)
        {
            int last_board = moveHistory.get(moveHistory.size() - 2);
            return isDrawSubboard(boards[last_board]);
        }
        for(int i=0;i<9;++i)
        {
            if(isDrawSubboard(boards[i])) return true;
        }
        return false;
    }

    public int getGameState() {
        /* We only need to check if the state has changed due to the last move */

        if(moveHistory.size() >= 2) {
            int last_board = moveHistory.get(moveHistory.size()-2);
            int res = isSubBoardWon(boards[last_board]);
            if(res != EMPTY) return res;
            if(isDrawSubboard(boards[last_board])) return DRAW;
            return IN_PROGRESS;
        }

        for(int b = 0;b<9;b++) {
            int res = isSubBoardWon(boards[b]);
            if(res != EMPTY) return res;
            if(isDraw()) return DRAW;
        }
        return IN_PROGRESS;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!Board.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final Board other = (Board) obj;

        if(!Arrays.deepEquals(boards,other.boards)){
            return false;
        }
        return moveHistory.equals(other.moveHistory) && prevMove == other.prevMove && currentPlayer == other.currentPlayer;
    }


    /* These are for debugging/dumping output for statistical analysis.
    public ArrayList<Double> stateToArray() {
        ArrayList<Double> al = new ArrayList<Double>();
        if(currentPlayer == PLAYER1)
            al.add(-1.0);
        else
            al.add(1.0);

        al.add(prevMove / 9.0);

        for(int x=0;x<9;x++) {
            for (int y = 0; y < 9; y++) {
                int c = boards[x][y];
                if(c == EMPTY)
                    al.add(0.0);
                if(c == PLAYER1)
                    al.add(-1.0);
                if(c == PLAYER2)
                    al.add(1.0);
            }
        }
        return al;
    }

    public String exportGame() {
        int gameState = this.getGameState();
        StringBuilder s = new StringBuilder();
        while(moveHistory.size()>2) {
            undoMove();
            ArrayList<Double> al = stateToArray();
            for(int i=0;i<al.size();++i) {
                s.append(al.get(i));
                s.append(",");
            }
            if(gameState == PLAYER1)
            {
                s.append(-1.0);
            } else {
                s.append(1.0);
            }
            s.append("\n");
        }
        return s.toString();
    }


    public void ImportFromString(String str) {
        int stringIdx = 0;

        for(int y=0;y<9;++y)
        {
            for(int x=0;x<9;++x)
            {
                int board_idx = y - y % 3 + x/3;
                int pos_idx = (y%3)*3 + x%3;

                while(true) {
                    char character = str.charAt(0);
                    str = str.substring(1);
                    int token = -1;

                    if(character == 'X') { token = Board.PLAYER1; }
                    if(character == 'O') { token = Board.PLAYER2; }
                    if(character == '.') { token = Board.EMPTY; }

                    if(token != -1) {
                        boards[board_idx][pos_idx] = token;
                        break;
                    }
                }


            }
        }
        assert(1==1);
        return;
    } */
}
