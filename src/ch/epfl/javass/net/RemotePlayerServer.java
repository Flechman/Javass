package ch.epfl.javass.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

import ch.epfl.javass.RemoteMain;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;

/**
 * Class which represents a server of a player 
 */
public final class RemotePlayerServer {

    //Port number
    protected static final int PORT = 5108;

    private final Player localPlayer;
    private RemoteMain rm;
    private BufferedReader r;
    private BufferedWriter w;
    private Socket s;

    /**
     * Constructor of RemotePlayerServer
     * @param localPlayer (Player) : the player type of the local player
     * @throws Exception 
     */
    public RemotePlayerServer(Player localPlayer, RemoteMain rm) {
        this.localPlayer = localPlayer;
        this.rm = rm;
    }

    /**
     * Method that runs the server which allows to communicate between the host and the distant player
     * Waits for the connection of the distant player, then sends information to it. Finally, if the distant player
     * sent some information, reads it and interprets it.
     */
    public void receive() {
        try {
            ServerSocket s0 = new ServerSocket(PORT);
            s = s0.accept();
            rm.closeProgram();
            r = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.US_ASCII));
            w = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.US_ASCII));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void run() {
        try {
            String string = new String();
            while((string = r.readLine()) != null) {
                String[] arrayString = StringSerializer.split(string, " ");
                String deserializedFirst = arrayString[0];

                switch(JassCommand.valueOf(deserializedFirst)) {
                case PLRS:
                    int ownIndex = StringSerializer.deserializeInt(arrayString[1]);
                    String[] nameTab = StringSerializer.split(arrayString[2], ",");
                    Map<PlayerId, String> playerNames = new TreeMap<>();
                    for (int i=0; i<PlayerId.COUNT; ++i)
                        playerNames.put(PlayerId.ALL.get(i),StringSerializer.deserializeString(nameTab[i]));

                    localPlayer.setPlayers(PlayerId.ALL.get(ownIndex), playerNames);
                    break;
                case CARD: 
                    String[] turnstate = StringSerializer.split(arrayString[1], ",");
                    long playerHand = StringSerializer.deserializeLong(arrayString[2]);
                    long currentScore = StringSerializer.deserializeLong(turnstate[0]);
                    long unplayedCards = StringSerializer.deserializeLong(turnstate[1]);
                    int currentTrick = StringSerializer.deserializeInt(turnstate[2]);
                    int cardToPlay = localPlayer.cardToPlay(TurnState.ofPackedComponents(currentScore, unplayedCards, currentTrick), CardSet.ofPacked(playerHand)).packed();
                    String card = StringSerializer.serializeInt(cardToPlay);
                    w.write(card);
                    w.write('\n');
                    w.flush();
                    break;
                case HAND:
                    long pckHand = StringSerializer.deserializeLong(arrayString[1]);
                    CardSet hand = CardSet.ofPacked(pckHand);
                    localPlayer.updateHand(hand);
                    break;
                case SCOR:
                    long score = StringSerializer.deserializeLong(arrayString[1]);
                    localPlayer.updateScore(Score.ofPacked(score));
                    break;
                case TRCK:
                    int trick = StringSerializer.deserializeInt(arrayString[1]);
                    localPlayer.updateTrick(Trick.ofPacked(trick));
                    break;
                case TRMP:
                    int trump = StringSerializer.deserializeInt(arrayString[1]);
                    localPlayer.setTrump(Color.ALL.get(trump));
                    break;
                case WINR:
                    int winner = StringSerializer.deserializeInt(arrayString[1]);
                    localPlayer.setWinningTeam(TeamId.ALL.get(winner));
                    break;
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
