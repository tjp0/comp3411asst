package ninetactoe;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Simulator {

    private Strategy p1_strategy;
    private Strategy p2_strategy;

    static AtomicInteger P1_wins = new AtomicInteger();
    static AtomicInteger P2_wins = new AtomicInteger();
    static AtomicInteger draws = new AtomicInteger();

    public Simulator(Strategy p1, Strategy p2) {
        this.p1_strategy = p1;
        this.p2_strategy = p2;
    }

    public Board simulate(Boolean debug) {
        Board b = new Board();

        Random r = new Random();
        int first_board = r.nextInt(9)+1;
        int first_move = r.nextInt(9)+1;
        b.place(first_board,first_move,Board.PLAYER1);
        b.setPlayer(Board.PLAYER2);
        boolean player = true;

        if(debug)
            System.out.println(b.toString());

        while(b.getGameState() == Board.IN_PROGRESS) {
            int next_move;
            if(!player) {
                next_move = p1_strategy.getBestMove(b);
            } else {
                next_move = p2_strategy.getBestMove(b);
            }

            assert(b.canMakeMove(next_move));
            if(debug)
                System.out.printf("%s made move: %d,%d\n",(b.getPlayer() == Board.PLAYER1 ? "X" : "O"),b.getPrevMove(),next_move);

            b.makeMove(next_move);
            if(debug)
                System.out.println(b.toString());

            player = !player;
        }
        return b;
    }


    private void mass_simulate_threaded(int rounds) {


        ExecutorService executor = Executors.newFixedThreadPool(10);
        BlockingQueue<String> blockingQueue = new LinkedBlockingDeque<>(10);

        for(int i=0;i<rounds;++i) {
            executor.submit(() -> {
                Board b = simulate(false);
                int res = b.getGameState();

                if (res == Board.PLAYER1) P1_wins.incrementAndGet();
                if (res == Board.PLAYER2) P2_wins.incrementAndGet();
                if (res == Board.DRAW) draws.incrementAndGet();
                //blockingQueue.add(b.exportGame());
                blockingQueue.add("");
            });
        }

        for(int i=0;i<rounds-1000;++i) {
                try{
                    System.out.print(blockingQueue.take());
                }
                catch (InterruptedException e) { System.err.println(e); }
                System.err.print(String.format("\rp1:%d p2:%d draw:%d",P1_wins.get(),P2_wins.get(),draws.get()));
            }

        System.out.print("\n");
    }


    private void mass_simulate(int rounds) {
        for(int i=0;i<rounds;++i) {
            Board b = simulate(false);
            int res = b.getGameState();
            if (res == Board.PLAYER1) P1_wins.incrementAndGet();
            if (res == Board.PLAYER2) P2_wins.incrementAndGet();
            if (res == Board.DRAW) draws.incrementAndGet();
            System.err.print(String.format("\rp1:%d p2:%d draw:%d",P1_wins.get(),P2_wins.get(),draws.get()));
        }

    }

    public static void main(String args[]) {
        Simulator s = new Simulator(new AlphaBetaStrategy(1000,false, 1), new AlphaBetaStrategy(1000, true, 1));


        int result = s.simulate(true).getGameState();

        if(result == Board.DRAW) {
            System.out.println("Draw");
        } else if ( result == Board.PLAYER1) { System.out.println("X Wins"); }
        else if ( result == Board.PLAYER2) { System.out.println("O Wins"); }

        s.mass_simulate(500000);
    }

}
