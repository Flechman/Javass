package ch.epfl.javass.jass;

import java.util.Map;

import ch.epfl.javass.jass.Card.Color;

/**
 * Class which represents a player that takes at least a certain time before playing
 */
public final class PacedPlayer implements Player {

    private Player underlyingPlayer;
    private double minTime;


    /**
     * Constructor of PacedPlayer
     * @param underlyingPlayer (Player): the player that play
     * @param minTime (double): the minimum time that the player have to take to play
     */
    public PacedPlayer(Player underlyingPlayer, double minTime){
        this.underlyingPlayer=underlyingPlayer;
        this.minTime=minTime;
    }


    /**
     * Calls the method cardToPlay of the underlying player but return the card 
     *  only after minTime time
     *  Note that here minTime is in seconds
     */
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        long time = System.currentTimeMillis();
        Card cardToPlay= underlyingPlayer.cardToPlay(state, hand);

        //Makes sure that the method takes at least minTime seconds before returning the card
        try {
            long nowTime = System.currentTimeMillis();
            if(nowTime-time<minTime*1000) {
                Thread.sleep((long) (minTime*1000-(nowTime-time)));
            }
        }
        catch (InterruptedException e) { /* ignore */ }
        return cardToPlay;
    }

    /**
     * Calls the method setPlayers of the underlying player 
     */
    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        underlyingPlayer.setPlayers(ownId, playerNames);
    }

    /**
     * Override of the method updateHand of the underlying player
     */
    @Override
    public void updateHand(CardSet newHand) {
        underlyingPlayer.updateHand(newHand);
    }

    /**
     * Override of the method setTrump of the underlying player
     */
    @Override
    public void setTrump(Color trump) {
        underlyingPlayer.setTrump(trump);
    }

    /**
     * Override of the method updateTrick of the underlying player
     */
    @Override
    public void updateTrick(Trick newTrick) {
        underlyingPlayer.updateTrick(newTrick);
    }

    /**
     * Override of the method setWinningTeam of the underlying player
     */
    @Override
    public void setWinningTeam(TeamId winningTeam) {
        underlyingPlayer.setWinningTeam(winningTeam);
    }

    /**
     * Override of the method updateScore of the underlying player
     */
    @Override
    public void updateScore(Score score) {
        underlyingPlayer.updateScore(score);

    }

}
