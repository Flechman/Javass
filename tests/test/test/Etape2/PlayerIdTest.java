package test.test.Etape2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;

class PlayerIdTest {

    @Test
    void testPlayer1() {
        assertEquals(TeamId.TEAM_1,PlayerId.PLAYER_1.team());
    }
    
    @Test
    void testPlayer2() {
        assertEquals(TeamId.TEAM_2,PlayerId.PLAYER_2.team());
    }
    
    @Test
    void testPlayer3() {
        assertEquals(TeamId.TEAM_1,PlayerId.PLAYER_3.team());
    }
    
    @Test
    void testPlayer4() {
        assertEquals(TeamId.TEAM_2,PlayerId.PLAYER_4.team());
    }

}
