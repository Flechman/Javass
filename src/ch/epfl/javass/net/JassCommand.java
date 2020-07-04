package ch.epfl.javass.net;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Enum which contains the seven types of messages exchanged by the client and the server
 * @author remi
 */
public enum JassCommand {
    PLRS, /* setPlayers */
    TRMP, /* setTrump */
    HAND, /* updateHand */
    TRCK, /* updateTrick */
    CARD, /* cardToPlay */
    SCOR, /* updateScore */
    WINR; /* setWinningTeam */

    //List containing every values of the enumerated type JassCommand (in order of declaration)
    public static final List<JassCommand> ALL = Collections.unmodifiableList(Arrays.asList(values()));

    //The number of values of JassCommand
    public static final int COUNT = 7;

}
