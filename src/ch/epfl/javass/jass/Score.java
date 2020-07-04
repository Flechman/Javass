package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;

/**
 * Class which represents the scores of a game
 */
public final class Score {

    //Initial score when the game starts
    public static final Score INITIAL =new Score(PackedScore.INITIAL);

    //The packed scores of the game
    private final long score;

    /**
     * Constructor of Score
     * @param score (long): the packed version of the score
     */
    private Score(long score) {
        this.score=score;
    }



    /**
     * Returns the score of the game
     * @param packed (long): packed version of the score
     * @return (Score): the score of the game
     */
    public static Score ofPacked(long packed) {

        Preconditions.checkArgument(PackedScore.isValid(packed));
        Score score= new Score(packed);
        return score;
    }


    /**
     * Gives the packed representation of the score
     * @return (long): the packed version of the scores
     */
    public long packed() {
        return score;
    }

    /**
     * Gives the number of tricks won by a given team
     * @param t (TeamId): the specified team
     * @return (int): The number of tricks obtained by the specified team
     */
    public int turnTricks(TeamId t) {
        return PackedScore.turnTricks(score, t);
    }

    /**
     * Gives the number of points scored by the given team during the current turn
     * @param t (TeamId): the specified team
     * @return (int): The number of points scored by the specified team 
     */
    public int turnPoints(TeamId t) {
        return PackedScore.turnPoints(score, t);
    }

    /** Gives the number of points scored by a given team during the full game
     * @param t (TeamId): the specified team
     * @return (int): The number of points scored by the specified team during the game
     */
    public int gamePoints(TeamId t) {
        return PackedScore.gamePoints(score, t);
    }

    /**
     * Sums up the points scored by the given team during the game with the points scored by this team during the current 
     * turn, to get a total of the points scored by this team
     * @param t (TeamId): the  specified team
     * @return (int): The total points scored by the team
     */
    public int totalPoints(TeamId t) {
        return PackedScore.totalPoints(score, t);
    }

    /**
     * Adds up the points scored by the given team during the current round to the total points won 
     * by the team during the full game, also adds 1 to the trick counter
     * @param winningTeam (TeamId): the specified team on which we apply the method to
     * @param trickPoints (int): the points won by the specified team during the round
     * @return (Score): the updated representation of all the scores during the game
     */
    public Score withAdditionalTrick(TeamId winningTeam, int trickPoints) {
        Preconditions.checkArgument(trickPoints>=0);
        return new Score(PackedScore.withAdditionalTrick(score, winningTeam, trickPoints));
    }

    /**
     * Adds the turn points to the game points of each team and resets their turn points and turn tricks
     * @return (Score): an updated representation of all the scores during the game
     */
    public Score nextTurn() {
        return new Score(PackedScore.nextTurn(score));
    }

    /**
     * Says if two score are equals
     * Override of the method equals of the class Object
     */
    @Override
    public boolean equals(Object thatO) {
        if (thatO==null) {
            return false;
        }
        else {
            if (thatO instanceof Score) {
                return (this.score== ((Score)thatO).score);
            }
            else {return false;}
        }  
    }

    /**
     * Override of the method hashCode of the class Object
     */
    @Override
    public int hashCode() {
        return Long.hashCode(score);
    }

    /**
     * Gives a representation of the score
     * Override of the method toString of the class Object
     */
    @Override
    public String toString() {
        return PackedScore.toString(score);
    }
}
