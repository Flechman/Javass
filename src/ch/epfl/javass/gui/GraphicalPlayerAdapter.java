package ch.epfl.javass.gui;


import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;
import javafx.application.Platform;

public class GraphicalPlayerAdapter implements Player {

    private TrickBean trickBean;
    private HandBean handBean;
    private ScoreBean scoreBean;
    private GraphicalPlayer graphicalPlayer;
    private ArrayBlockingQueue<Card> comQueue;
    private final String ip;

    private final static int ARRAY_CAPACITY = 1;


    public GraphicalPlayerAdapter(String ip) {
        trickBean = new TrickBean();
        scoreBean = new ScoreBean();
        handBean = new HandBean();
        comQueue = new ArrayBlockingQueue<>(ARRAY_CAPACITY);
        this.ip = ip;
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        CardSet playableCards = state.trick().playableCards(hand);
        Platform.runLater(() -> { handBean.setPlayableCards(playableCards); });
        try {
            Card c = comQueue.take();
            Platform.runLater(() -> { handBean.setPlayableCards(CardSet.EMPTY); });
            return c;
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        graphicalPlayer = new GraphicalPlayer(ownId, playerNames, scoreBean, trickBean, handBean, comQueue, ip);
        Platform.runLater(() -> { graphicalPlayer.createStage().show(); });
    }

    @Override
    public void updateHand(CardSet newHand) {
        Platform.runLater(() -> { handBean.setHand(newHand); });
    }

    @Override
    public void setTrump(Color trump) {
        Platform.runLater(() -> { trickBean.setTrump(trump); });
    }

    @Override
    public void updateTrick(Trick newTrick) {
        Platform.runLater(() -> { trickBean.setTrick(newTrick); });
    }

    @Override
    public void setWinningTeam(TeamId winningTeam) {
        Platform.runLater(() -> { scoreBean.setWinningTeam(winningTeam); });
    }

    @Override
    public void updateScore(Score score) {
        Platform.runLater(() -> {  
            for(TeamId t : TeamId.ALL) {
                scoreBean.setTurnPoints(t, score.turnPoints(t));
                scoreBean.setGamePoints(t, score.gamePoints(t));
                scoreBean.setTotalPoints(t, score.totalPoints(t));
            }
        });
    }


}
