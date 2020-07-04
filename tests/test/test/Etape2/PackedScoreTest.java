package test.test.Etape2;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.SplittableRandom;

import org.junit.jupiter.api.Test;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.bits.Bits64;
import ch.epfl.javass.jass.Jass;
import ch.epfl.javass.jass.PackedScore;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import test.test.TestRandomizer;

interface TestValid {
    void test(int gamePoints1, int turnPoints1, int turnTricks1, int gamePoints2, int turnPoints2, int turnTricks2, long validCard, int trickPoints);
}

interface TestInvalid {
    void test(int gamePoints1, int turnPoints1, int turnTricks1, int gamePoints2, int turnPoints2, int turnTricks2, long rd, long invalidCard, int trickPoints);
}

class PackedScoreTest {

    void WorksForSomeValidScores(TestValid t) {
        SplittableRandom rng = TestRandomizer.newRandom();
        for (int turnTricks1 = 0; turnTricks1 <= 9; ++turnTricks1)
            for (int turnTricks2 = 0; turnTricks2 <= 9 - turnTricks1; ++turnTricks2)
                for (int turnPoints1 = turnTricks1 == 9 ? 257 : 0; turnPoints1 <= (turnTricks1 == 9 ? 257 : (turnTricks2 == 9 ? 0 : 157)); turnPoints1 += rng.nextInt(50) + 1)
                    for (int turnPoints2 = turnTricks2 == 9 ? 257 : 0; turnPoints2 <= (turnTricks2 == 9 ? 257 : (turnTricks1 == 9 ? 0 : (157 - turnPoints1))); turnPoints2 += rng.nextInt(50) + 1)
                        for (int gamePoints1 = 0; gamePoints1 <= 2000 - turnPoints1; gamePoints1 += rng.nextInt(500) + 1)
                            for (int gamePoints2 = 0; gamePoints2 <= 2000 - turnPoints2; gamePoints2 += rng.nextInt(500) + 1)
                                t.test(gamePoints1, turnPoints1, turnTricks1, gamePoints2, turnPoints2, turnTricks2,
                                        ((((long)gamePoints2 << 9 | (long)turnPoints2) << 4 | (long)turnTricks2) <<32 |
                                                (((long)gamePoints1<< 9 | (long)turnPoints1) << 4 | (long)turnTricks1)),
                                        rng.nextInt(10));
    }

    void WorksForSomeInvalidScores(TestInvalid t) {
        SplittableRandom rng = TestRandomizer.newRandom();
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; ++i) {
            int gamePoints1 = rng.nextInt(1<<11);
            int gamePoints2 = rng.nextInt(1<<11);
            int turnPoints1 = rng.nextInt(1<<9);
            int turnPoints2 = rng.nextInt(1<<9);
            int turnTricks1 = rng.nextInt(1<<4);
            int turnTricks2 = rng.nextInt(1<<4);
            int trickPoints = rng.nextInt(10);
            long rd = 0;
            do rd = (rng.nextLong() & ~(Bits64.mask(0, 24)) << 32 | Bits64.mask(0, 24));
            while (rd == 0);
            long invalidCard = Bits64.pack(
                    (long) Bits32.pack(turnTricks1, 4, turnPoints1, 9 , gamePoints1, 11), 32,
                    (long) Bits32.pack(turnTricks2, 4, turnPoints2, 9 , gamePoints2, 11), 32);
            invalidCard |= (gamePoints1 <= 2000 && gamePoints2 <= 2000 && turnPoints1 <= 257 && turnPoints2 <= 257 && turnTricks1 <= 9 && turnTricks2 <= 9) ?  rd : 0L;
            t.test(gamePoints1, turnPoints1, turnTricks1, gamePoints2, turnPoints2, turnTricks2, rd, invalidCard, trickPoints);
        }
    }

    @Test
    void packedScoreScoreConstantsAreCorrect() throws Exception {
        assertEquals(0L, PackedScore.INITIAL);
    }

    @Test
    void isValidWorksForSomeValidScores() {
        WorksForSomeValidScores(
                (int gamePoints1, int turnPoints1, int turnTricks1, int gamePoints2, int turnPoints2, int turnTricks2, long validCard, int trickPoints) -> {
                    assertTrue(PackedScore.isValid(Bits64.pack(
                            (long) Bits32.pack(turnTricks1, 4, turnPoints1, 9 , gamePoints1, 11), 32,
                            (long) Bits32.pack(turnTricks2, 4, turnPoints2, 9 , gamePoints2, 11), 32)));
                }
                );
    }

    @Test
    void isValidWorksForSomeInvalidScores() {
        WorksForSomeInvalidScores(
                (int gamePoints1, int turnPoints1, int turnTricks1, int gamePoints2, int turnPoints2, int turnTricks2, long rd, long invalidCard, int trickPoints) -> {
                    assertFalse(PackedScore.isValid(invalidCard));
                });
    }

    @Test
    void packWorksForSomeValidScores() {
        WorksForSomeValidScores(
                (int gamePoints1, int turnPoints1, int turnTricks1, int gamePoints2, int turnPoints2, int turnTricks2, long validCard, int trickPoints) -> {
                    assertEquals(validCard, PackedScore.pack(turnTricks1, turnPoints1, gamePoints1, turnTricks2, turnPoints2, gamePoints2));
                });
    }

    @Test
    void turnTricksWorksForSomeValidScores() {
        WorksForSomeValidScores(
                (int gamePoints1, int turnPoints1, int turnTricks1, int gamePoints2, int turnPoints2, int turnTricks2, long validCard, int trickPoints) -> {
                    assertEquals(turnTricks1,
                            PackedScore.turnTricks(validCard, TeamId.TEAM_1));
                    assertEquals(turnTricks2,
                            PackedScore.turnTricks(validCard, TeamId.TEAM_2));
                });
    }

    @Test
    void turnTricksWorksForSomeInvalidScores() {
        WorksForSomeInvalidScores(
                (int gamePoints1, int turnPoints1, int turnTricks1, int gamePoints2, int turnPoints2, int turnTricks2, long rd, long invalidCard, int trickPoints) -> {
                    assertThrows(AssertionError.class, () -> {
                        PackedScore.turnTricks(invalidCard, TeamId.TEAM_1);
                    });
                    assertThrows(AssertionError.class, () -> {
                        PackedScore.turnTricks(invalidCard, TeamId.TEAM_2);
                    });
                });
    }

    @Test
    void turnPointsWorksForSomeValidScores() {
        WorksForSomeValidScores(
                (int gamePoints1, int turnPoints1, int turnTricks1, int gamePoints2, int turnPoints2, int turnTricks2, long validCard, int trickPoints) -> {
                    assertEquals(turnPoints1,
                            PackedScore.turnPoints(validCard, TeamId.TEAM_1));
                    assertEquals(turnPoints2,
                            PackedScore.turnPoints(validCard, TeamId.TEAM_2));
                });
    }

    @Test
    void turnPointsWorksForSomeInvalidScores() {
        WorksForSomeInvalidScores(
                (int gamePoints1, int turnPoints1, int turnTricks1, int gamePoints2, int turnPoints2, int turnTricks2, long rd, long invalidCard, int trickPoints) -> {
                    assertThrows(AssertionError.class, () -> {
                        PackedScore.turnPoints(invalidCard, TeamId.TEAM_1);
                    });
                    assertThrows(AssertionError.class, () -> {
                        PackedScore.turnPoints(invalidCard, TeamId.TEAM_2);
                    });
                });
    }

    @Test
    void gamePointsWorksForSomeValidScores() {
        WorksForSomeValidScores(
                (int gamePoints1, int turnPoints1, int turnTricks1, int gamePoints2, int turnPoints2, int turnTricks2, long validCard, int trickPoints) -> {
                    assertEquals(gamePoints1,
                            PackedScore.gamePoints(validCard, TeamId.TEAM_1));
                    assertEquals(gamePoints2,
                            PackedScore.gamePoints(validCard, TeamId.TEAM_2));
                });
    }

    @Test
    void gamePointsWorksForSomeInvalidScores() {
        WorksForSomeInvalidScores(
                (int gamePoints1, int turnPoints1, int turnTricks1, int gamePoints2, int turnPoints2, int turnTricks2, long rd, long invalidCard, int trickPoints) -> {
                    assertThrows(AssertionError.class, () -> {
                        PackedScore.gamePoints(invalidCard, TeamId.TEAM_1);
                    });
                    assertThrows(AssertionError.class, () -> {
                        PackedScore.gamePoints(invalidCard, TeamId.TEAM_2);
                    });
                });
    }

    @Test
    void totalPointsWorksForSomeValidScores() {
        WorksForSomeValidScores(
                (int gamePoints1, int turnPoints1, int turnTricks1, int gamePoints2, int turnPoints2, int turnTricks2, long validCard, int trickPoints) -> {
                    assertEquals(gamePoints1 + turnPoints1,
                            PackedScore.totalPoints(validCard, TeamId.TEAM_1));
                    assertEquals(gamePoints2 + turnPoints2,
                            PackedScore.totalPoints(validCard, TeamId.TEAM_2));
                });
    }

    @Test
    void totalPointsWorksForSomeInvalidScores() {
        WorksForSomeInvalidScores(
                (int gamePoints1, int turnPoints1, int turnTricks1, int gamePoints2, int turnPoints2, int turnTricks2, long rd, long invalidCard, int trickPoints) -> {
                    assertThrows(AssertionError.class, () -> {
                        PackedScore.totalPoints(invalidCard, TeamId.TEAM_1);
                    });
                    assertThrows(AssertionError.class, () -> {
                        PackedScore.totalPoints(invalidCard, TeamId.TEAM_2);
                    });
                });
    }

    @Test
    void withAdditionalTrickWorksForSomeValidScores() {
        WorksForSomeValidScores(
                (int gamePoints1, int turnPoints1, int turnTricks1, int gamePoints2, int turnPoints2, int turnTricks2, long validCard, int trickPoints) -> {
                    if(turnTricks1 != 9 && turnPoints1 + trickPoints + (turnTricks1 == 8 ? 100 : 0) <= 257)
                        assertEquals(PackedScore.pack(turnTricks1 + 1, turnPoints1 + trickPoints + (turnTricks1 == 8 ? 100 : 0), gamePoints1, turnTricks2, turnPoints2, gamePoints2),
                                PackedScore.withAdditionalTrick(validCard, TeamId.TEAM_1, trickPoints));
                    if(turnTricks2 != 9 && turnPoints2 + trickPoints + (turnTricks2 == 8 ? 100 : 0) <= 257)
                        assertEquals(PackedScore.pack(turnTricks1, turnPoints1, gamePoints1, turnTricks2 + 1, turnPoints2 + trickPoints + (turnTricks2 == 8 ? 100 : 0), gamePoints2),
                                PackedScore.withAdditionalTrick(validCard, TeamId.TEAM_2, trickPoints));
                });
    }

    @Test
    void withAdditionalTrickWorksForSomeInvalidScores() {
        WorksForSomeInvalidScores(
                (int gamePoints1, int turnPoints1, int turnTricks1, int gamePoints2, int turnPoints2, int turnTricks2, long rd, long invalidCard, int trickPoints) -> {
                    assertThrows(AssertionError.class, () -> {
                        PackedScore.withAdditionalTrick(invalidCard, TeamId.TEAM_1, trickPoints);
                    });
                    assertThrows(AssertionError.class, () -> {
                        PackedScore.withAdditionalTrick(invalidCard, TeamId.TEAM_2, trickPoints);
                    });
                });
    }

    @Test
    void nextTurnWorksForSomeValidScores() {
        WorksForSomeValidScores(
                (int gamePoints1, int turnPoints1, int turnTricks1, int gamePoints2, int turnPoints2, int turnTricks2, long validCard, int trickPoints) -> {
                    if(gamePoints1 + turnPoints1 <= 2000 && gamePoints2 + turnPoints2 <= 2000)
                    {
                        long l = PackedScore.pack(0, 0, gamePoints1 + turnPoints1, 0, 0, gamePoints2 + turnPoints2);
                        long l2= PackedScore.nextTurn(validCard);
                        assertEquals(l,l2
                                );
                    }
                });
    }

    @Test
    void nextTurnWorksForSomeInvalidScores() {
        WorksForSomeInvalidScores(
                (int gamePoints1, int turnPoints1, int turnTricks1, int gamePoints2, int turnPoints2, int turnTricks2, long rd, long invalidCard, int trickPoints) -> {
                    assertThrows(AssertionError.class, () -> {
                        PackedScore.nextTurn(invalidCard);
                    });
                });
    }
    /**
     * ofPacked
     */
    @Test
    void ofPackedWorks() {
        assertEquals(Score.INITIAL, Score.ofPacked(0L));
        for (int r = 0; r <= 9; ++r) {
            for (int c = 0; c <= 257; ++c) {
                for(int j = 2001; j<=2050; ++j) {
                    final int kj = j;
                    final int kc = c;
                    final int kr = r;
                    assertThrows(IllegalArgumentException.class, () -> {
                        Score.ofPacked((long)kj << 45 | (long)kc << 36 | (long)kr <<32 | (long)kj<< 13 | (long)kc << 4 | (long)kr);
                    });
                }
            }
        }
    }
    /*
     * packed
     */
    @Test
    void packedWorks() {
        for (int r = 0; r <=9; ++r) {
            for (int c = 0; c <= 257; ++c) {
                for(int j = 0; j<=2000; ++j) {
                    assertEquals((long)j << 45 | (long)c << 36 | (long)r <<32 | (long)j<< 13 | (long)c << 4 | (long)r, 
                            Score.ofPacked(PackedScore.pack(r, c, j, r, c, j)).packed());
                }
            }
        }
    }
    /*
     * turnTrick
     */
    @Test
    void turnTrickWorks() {
        for (int r = 0; r <=9; ++r) {
            for (int c = 0; c <= 257; ++c) {
                for(int j = 0; j<=2000; ++j) {
                    assertEquals(r, 
                            Score.ofPacked(PackedScore.pack(r, c, j, r, c, j)).turnTricks(TeamId.TEAM_1));
                    assertEquals(r,
                            Score.ofPacked(PackedScore.pack(r, c, j, r, c, j)).turnTricks(TeamId.TEAM_2));
                }
            }
        }
    }
    /*
     * turnPoints
     */
    @Test 
    void turnPointsWorks(){
        for (int r = 0; r <=9; ++r) {
            for (int c = 0; c <= 257; ++c) {
                for(int j = 0; j<=2000; ++j) {
                    assertEquals(c, 
                            Score.ofPacked(PackedScore.pack(r, c, j, r, c, j)).turnPoints(TeamId.TEAM_1));
                    assertEquals(c,
                            Score.ofPacked(PackedScore.pack(r, c, j, r, c, j)).turnPoints(TeamId.TEAM_2));
                }
            }
        }
    }
    
    /*
     * gamePoints
     */
    @Test
    void gamePoints() {
        for (int r = 0; r <=9; ++r) {
            for (int c = 0; c <= 257; ++c) {
                for(int j = 0; j<=2000; ++j) {
                    assertEquals(j, 
                            Score.ofPacked(PackedScore.pack(r, c, j, r, c, j)).gamePoints(TeamId.TEAM_1));
                    assertEquals(j,
                            Score.ofPacked(PackedScore.pack(r, c, j, r, c, j)).gamePoints(TeamId.TEAM_2));
                }
            }
        }
    }
    /*
     * totalPoints
     */
    @Test
    void totalPoints() {
        for (int r = 0; r <=9; ++r) {
            for (int c = 0; c <= 257; ++c) {
                for(int j = 0; j<=2000; ++j) {
                    assertEquals(j+c, 
                            Score.ofPacked(PackedScore.pack(r, c, j, r, c, j)).totalPoints(TeamId.TEAM_1));
                    assertEquals(j+c,
                            Score.ofPacked(PackedScore.pack(r, c, j, r, c, j)).totalPoints(TeamId.TEAM_2));
                }
            }
        }
    }
    /*
     * ScoreWorks
     */
    @Test 
    void ScoreWorks() {
        Score s = Score.INITIAL;
        String score = s.toString();
        for (int i = 0; i < Jass.TRICKS_PER_TURN; ++i) {
          int p = (i == 0 ? 13 : 18);
          TeamId w = (i % 2 == 0 ? TeamId.TEAM_1 : TeamId.TEAM_2);
          s = s.withAdditionalTrick(w, p);
          score += s.toString();
        }
        s = s.nextTurn();
        score += s.toString();
        assertEquals("(0,0,0)/(0,0,0)(1,13,0)"
                + "/(0,0,0)(1,13,0)/(1,18,0)(2,31,0)"
                + "/(1,18,0)(2,31,0)/(2,36,0)(3,49,0)"
                + "/(2,36,0)(3,49,0)/(3,54,0)(4,67,0)"
                + "/(3,54,0)(4,67,0)/(4,72,0)(5,85,0)"
                + "/(4,72,0)(0,0,85)/(0,0,72)",
                score);
    }
    
    /*
     * equals works
     */
    @Test
    void equalsWorks() {
        for (int r = 1; r <=9; ++r) {
            for (int c = 1; c <= 257; ++c) {
                for(int j = 1; j<=2000; ++j) {
                    assertEquals(true, 
                            Score.ofPacked(PackedScore.pack(r, c, j, r, c, j)).equals(Score.ofPacked(PackedScore.pack(r, c, j, r, c, j))));
                    assertEquals(false, Score.ofPacked(PackedScore.pack(r, c, j, r, c, j)).equals(null));
                    assertEquals(false, Score.ofPacked(PackedScore.pack(r, c, j, r, c, j)).equals(Score.ofPacked(PackedScore.pack(r-1, c-1, j-1, r, c, j))));
                }
            }
        }
    }
}




