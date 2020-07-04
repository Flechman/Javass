package ch.epfl.javass.gui;

import ch.epfl.javass.jass.TeamId;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Observable way to represent the scores of a game
 * @author remi
 */
public final class ScoreBean {

    private IntegerProperty turnPoints1;
    private IntegerProperty turnPoints2;

    private IntegerProperty gamePoints1;
    private IntegerProperty gamePoints2;

    private IntegerProperty totalPoints1;
    private IntegerProperty totalPoints2;

    private ObjectProperty<TeamId> winningTeam;

    /**
     * Constructor of ScoreBean
     */
    public ScoreBean() {
        turnPoints1 = new SimpleIntegerProperty();
        turnPoints2 = new SimpleIntegerProperty();

        gamePoints1 = new SimpleIntegerProperty();
        gamePoints2 = new SimpleIntegerProperty();

        totalPoints1 = new SimpleIntegerProperty();
        totalPoints2 = new SimpleIntegerProperty();

        winningTeam = new SimpleObjectProperty<>();
    }

    /**
     * Sets the turn points of the given team
     * @param team (TeamId): the team to which we want to update the turn points
     * @param newTurnPoints
     */
    public void setTurnPoints(TeamId team, int newTurnPoints) {
        if(team == TeamId.TEAM_1) { turnPoints1.set(newTurnPoints); }
        else { turnPoints2.set(newTurnPoints); }
    }

    /**
     * Sets the game points of the given team
     * @param team (TeamId): the team to which we want to update the game points
     * @param newGamePoints
     */
    public void setGamePoints(TeamId team, int newGamePoints) {
        if(team == TeamId.TEAM_1) { gamePoints1.set(newGamePoints); }
        else { gamePoints2.set(newGamePoints); }
    }

    /**
     * Sets the total points of the given team
     * @param team (TeamId): the team to which we want to update the total points
     * @param newTotalPoints (int): the new total points of the team
     */
    public void setTotalPoints(TeamId team, int newTotalPoints) {
        if(team == TeamId.TEAM_1) { totalPoints1.set(newTotalPoints); }
        else { totalPoints2.set(newTotalPoints); }
    }

    /**
     * Sets the winning team of the game
     * @param winningTeam (TeamId): The winning team
     */
    public void setWinningTeam(TeamId winningTeam) {
        this.winningTeam.set(winningTeam);
    }

    /**
     * Getter for the points of the turn of a given team
     * @param team (TeamId): the given team
     * @return (ReadOnlyIntegerProperty): the integer property which represents the turn points of the team
     */
    public ReadOnlyIntegerProperty turnPointsProperty(TeamId team) {
        if(team == TeamId.TEAM_1) { return turnPoints1; }
        else { return turnPoints2; }
    }

    /**
     * Getter for the game points of a team
     * @param team (TeamId): the team
     * @return (ReadOnlyIntegerProperty): the integer property associated to the game points of the team 
     */
    public ReadOnlyIntegerProperty gamePointsProperty(TeamId team) {
        if(team == TeamId.TEAM_1) { return gamePoints1; }
        else { return gamePoints2; }
    }

    /**
     * Getter for the total points of a team
     * @param team (TeamId): the team
     * @return (ReadOnlyIntegerProperty): the integer property associated to the total points of the team
     */
    public ReadOnlyIntegerProperty totalPointsProperty(TeamId team) {
        if(team == TeamId.TEAM_1) { return totalPoints1; }
        else { return totalPoints2; }
    }

    /**
     * Method to get the winning team of the game
     * @return (ReadOnlyObjectProperty<TeamId>): the property associated to the team that won
     */
    public ReadOnlyObjectProperty<TeamId> winningTeamProperty() {
        return winningTeam;
    }

}