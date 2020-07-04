package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Trick;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public final class TrickBean {

    private ObjectProperty<Color> trump;
    private ObservableMap<PlayerId, Card> trick;
    private ObjectProperty<PlayerId> winningPlayer;

    public TrickBean() {
        trump = new SimpleObjectProperty<>();
        trick = FXCollections.observableHashMap();
        winningPlayer = new SimpleObjectProperty<>();
    }

    public ObservableMap<PlayerId, Card> trick() {
        return trick;
    }

    public void setTrick(Trick newTrick) {
        trick.clear();
        for (int i=0; i<newTrick.size(); ++i) {
            trick.put(newTrick.player(i), newTrick.card(i));

        }
        if (newTrick.isEmpty()) { winningPlayer.set(null); }
        else {
            winningPlayer.set(newTrick.winningPlayer());
        }
    }

    public void setTrump(Color newTrump) {
        trump.set(newTrump);
    }

    public ReadOnlyObjectProperty<Color> trumpProperty() {
        return trump;
    }

    public ReadOnlyObjectProperty<PlayerId> winningPlayerProperty() {
        return winningPlayer;
    }
}
