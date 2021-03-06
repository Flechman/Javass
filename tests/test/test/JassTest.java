package test.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.epfl.javass.jass.Jass;

public final class JassTest {
    @Test
    void jassConstantsAreCorrect() throws Exception {
        assertEquals(9, Jass.HAND_SIZE);
        assertEquals(9, Jass.TRICKS_PER_TURN);
        assertEquals(1000, Jass.WINNING_POINTS);
        assertEquals(100, Jass.MATCH_ADDITIONAL_POINTS);
        assertEquals(5, Jass.LAST_TRICK_ADDITIONAL_POINTS);
    }
}
