package ch.epfl.javass.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;

/**
 * Class which represents the client of a player
 * @author remi
 */
public final class RemotePlayerClient implements Player, AutoCloseable  {

    private Socket s;
    private BufferedReader r;
    private BufferedWriter w;

    /**
     * Constructor of RemotePlayerClient
     * @param hostName (String): the name of the host on which the server of the distant player executes itself
     * @throws IOException if I/O operations are interrupted
     */
    public RemotePlayerClient(String hostName) throws IOException {
        s = new Socket(hostName, RemotePlayerServer.PORT);
        r = new BufferedReader(new InputStreamReader(s.getInputStream(),StandardCharsets.US_ASCII));
        w = new BufferedWriter( new OutputStreamWriter(s.getOutputStream(),StandardCharsets.US_ASCII));
    }

    /**
     * Override of the method close of AutoCloseable
     */
    @Override
    public void close() throws Exception {
        s.close();
        r.close();
        w.close();
    }

    /**
     * Override of the method cardToPlay of Player
     */
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        String keyWord = JassCommand.CARD.name();
        String score = StringSerializer.serializeLong(state.packedScore());
        String unplayedCard = StringSerializer.serializeLong(state.packedUnplayedCards());
        String trick = StringSerializer.serializeInt(state.packedTrick());

        String turnState = StringSerializer.combine(",", score, unplayedCard, trick);
        String Hand = StringSerializer.serializeLong(hand.packed());

        String message = StringSerializer.combine(" ", keyWord, turnState, Hand);
        try {
            w.write(message);
            w.write('\n');
            w.flush();
            String card = r.readLine();
            return Card.ofPacked(StringSerializer.deserializeInt(card));
        }
        catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Override of the method setPlayers of Player
     */
    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {

        String keyWord = JassCommand.PLRS.name();
        String ownIndex = StringSerializer.serializeInt(ownId.ordinal());

        String player1 = StringSerializer.serializeString(playerNames.get(PlayerId.PLAYER_1));
        String player2 = StringSerializer.serializeString(playerNames.get(PlayerId.PLAYER_2));
        String player3 = StringSerializer.serializeString(playerNames.get(PlayerId.PLAYER_3));
        String player4 = StringSerializer.serializeString(playerNames.get(PlayerId.PLAYER_4));

        String names = StringSerializer.combine(",", player1, player2, player3, player4);

        String message = StringSerializer.combine(" ", keyWord, ownIndex, names);

        try {
            w.write(message);
            w.write('\n');
            w.flush();
        }
        catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Override of the method updateHand of Player
     */
    @Override
    public void updateHand(CardSet newHand) {
        String hand = JassCommand.HAND.name();
        String pkHand = StringSerializer.serializeLong(newHand.packed());
        try {
            w.write(StringSerializer.combine(" ", hand, pkHand));
            w.write('\n');
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Override of the method setTrump of Player
     */
    @Override
    public void setTrump(Color trump)  {
        String trmp = JassCommand.TRMP.name();
        String pkTrmp = StringSerializer.serializeInt(trump.ordinal());
        try {
            w.write(StringSerializer.combine(" ", trmp, pkTrmp));
            w.write('\n');
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Override of the method updateTrick of Player
     */
    @Override
    public void updateTrick(Trick newTrick) {
        String trck = JassCommand.TRCK.name();
        String pkTrck = StringSerializer.serializeInt(newTrick.packed());
        try {
            w.write(StringSerializer.combine(" ", trck, pkTrck));
            w.write('\n');
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Override of the method updateScore of Player
     */
    @Override
    public void updateScore(Score score) {
        String scor = JassCommand.SCOR.name();
        String pkScor = StringSerializer.serializeLong(score.packed());
        try {
            w.write(StringSerializer.combine(" ", scor, pkScor));
            w.write('\n');
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Override of the method setWinningTeam of Player
     */
    @Override
    public void setWinningTeam(TeamId winningTeam) {
        String winr = JassCommand.WINR.name();
        String team = StringSerializer.serializeInt(winningTeam.ordinal());
        try {
            w.write(StringSerializer.combine(" ", winr, team));
            w.write('\n');
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
