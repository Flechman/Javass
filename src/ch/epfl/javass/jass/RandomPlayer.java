package ch.epfl.javass.jass;

import java.util.Map;
import java.util.Random;

import ch.epfl.javass.jass.Card.Color;

public final class RandomPlayer implements Player {

    private final Random rng;
    private Player underlyingPlayer;


    public RandomPlayer(long rngSeed) {
        this.rng = new Random(rngSeed);

    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        CardSet playable = state.trick().playableCards(hand);
        return playable.get(rng.nextInt(playable.size()));
    }

    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        //underlyingPlayer.setPlayers(ownId, playerNames);
    }

    @Override
    public void updateHand(CardSet newHand) {
        
    }

    @Override
    public void setTrump(Color trump) {
        //underlyingPlayer.setTrump(trump);
    }

    @Override
    public void updateTrick(Trick newTrick) {
        //underlyingPlayer.updateTrick(newTrick);
    }

    @Override
    public void updateScore(Score score) {
        //underlyingPlayer.updateScore(score);
    }

    @Override
    public void setWinningTeam(TeamId winningTeam) {
        //underlyingPlayer.setWinningTeam(winningTeam);
    }
}