package test.test.Etape2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.epfl.javass.jass.TeamId;

class TeamIdTest {

    @Test
    void testTeam1() {
        assertEquals(TeamId.TEAM_2, (TeamId.TEAM_1).other());
    }
    
    @Test
    void testTeam2() {
        assertEquals(TeamId.TEAM_1, (TeamId.TEAM_2).other());
    }

}
