package ch.epfl.javass.jass;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public final class RandomJassGame {

    public static void main(String[] args) {
        
        Map<PlayerId, Player> players = new HashMap<>();
        Map<PlayerId, String> playerNames = new HashMap<>();

        /**
         * Random game with two MctsPlayers facing two RandomPlayers
         */
        for(PlayerId pId: PlayerId.ALL) {
            if(pId.ordinal()%2==0) {
                Random rng = new Random(1);
                Player mctsPlayer = new MctsPlayer(pId, 1000+rng.nextInt(5000), 10);
                if(pId.ordinal()==0) {
                    mctsPlayer = new PrintingPlayer(mctsPlayer);
                }
                players.put(pId, mctsPlayer);
                playerNames.put(pId, pId.name());
            }
            if(pId.ordinal()==1 /* PLAYER_2 */) {
                Player randomPlayer = new RandomPlayer(76);
                players.put(pId, randomPlayer);
                playerNames.put(pId, pId.name());
            }
            else {
                Player randomPlayer = new RandomPlayer(2019);
                players.put(pId, randomPlayer);
                playerNames.put(pId, pId.name());
            }
        }

        /**
         * Random game with a random player
         */
        //        for (PlayerId pId: PlayerId.ALL) {
        //            Player player = new RandomPlayer(2019);
        //            if (pId == PlayerId.PLAYER_1)
        //                player = new PrintingPlayer(player);
        //            players.put(pId, player);
        //            playerNames.put(pId, pId.name());
        //        }
        double startingTime = System.currentTimeMillis();

        int simulationNumber = 1;
        for(int i=0;i<simulationNumber;++i) {
            Random rng = new Random(94L);
            JassGame g = new JassGame(1000+rng.nextInt(2000), players, playerNames);
            while (!g.isGameOver()) {
                g.advanceToEndOfNextTrick();
                //System.out.println("----");
            }
        }

        double deltaTime = (System.currentTimeMillis()-startingTime)/1000;

        System.out.println("Runtime: "+deltaTime+" seconds");
        
    }
}
