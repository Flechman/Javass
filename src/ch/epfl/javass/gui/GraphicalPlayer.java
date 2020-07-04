package ch.epfl.javass.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import ch.epfl.javass.ChatClient;
import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GraphicalPlayer {

    private static final int CARD_IMAGE_WIDTH =120;
    private static final int CARD_IMAGE_HEIGHT =180;
    private static final int TRUMP_IMAGE_SIZE =101;
    private static final int HANDCARD_IMAGE_WIDTH =80;
    private static final int HANDCARD_IMAGE_HEIGHT =120;
    private static final double PLAYABLE_CARD_OPACITY = 1, UNPLAYABLE_CARD_OPACITY = 0.2;

    private static final int BLUR_EFFECT_RADIUS = 4;
    private final ObservableMap<Card, Image> imageCardsMap = createImageCardMap();
    private final ObservableMap<Color, Image> imageTrumpMap = createImageTrumpMap();
    private final ObservableMap<Card, Image> imageHandCardsMap = createImageHandCardMap();

    private StackPane mainPane;
    private VBox chat;
    private ScrollPane scrollPane;
    private ChatClient c;


    public GraphicalPlayer(PlayerId ownId, Map<PlayerId, String> names, ScoreBean score, TrickBean trick, HandBean hand, BlockingQueue<Card> queue, String ip) {
        Thread clientThread = new Thread(() -> {
            try {
                c = new ChatClient(this, ip);
            } catch (IOException e) {
                System.err.println("Connection error due to : "+ip);
                e.printStackTrace();
            }
            c.run();
        });
        clientThread.setDaemon(true);
        clientThread.start();
        GridPane trickGrid = createTrickPane(ownId, names, trick);
        GridPane scorePane = createScorePane(score, names);
        List<BorderPane> victoryPanes= createVictoryPanes(names, score);
        HBox handPane = createHandPane(hand, queue);
        GridPane chatPane = createChatPane(names, ownId);
        BorderPane InterfacePane = new BorderPane(trickGrid, scorePane, chatPane,handPane , null);
        mainPane = new StackPane(InterfacePane,victoryPanes.get(0),victoryPanes.get(1));
    }

    public Stage createStage() {

        Stage primaryStage = new Stage();
        Scene scene = new Scene(mainPane);
        primaryStage.setScene(scene);
        return primaryStage;
    }


    private HBox createHandPane(HandBean hand, BlockingQueue<Card> queue) {
        HBox handPane = new HBox();

        handPane.setStyle("-fx-background-color: lightgray; -fx-spacing: 5px; -fx-padding: 5px;");


        for (int i=0; i< hand.hand().size(); ++i) {

            ImageView cardImage = new ImageView();
            cardImage.setFitWidth(HANDCARD_IMAGE_WIDTH);
            cardImage.setFitHeight(HANDCARD_IMAGE_HEIGHT);

            ObjectProperty<Card> cardIndex = new SimpleObjectProperty<>();
            cardIndex.bind(Bindings.valueAt(hand.hand(), i));
            cardImage.imageProperty().bind(Bindings.valueAt(imageHandCardsMap, cardIndex));

            final int thisIndex = i;

            cardImage.setOnMouseClicked(e-> { 
                hand.setPlayableCards(CardSet.EMPTY);
                queue.add(hand.hand().get(thisIndex));
            });

            BooleanBinding isPlayable = Bindings.createBooleanBinding(()-> hand.playableCards().contains(hand.hand().get(thisIndex)),hand.hand(), hand.playableCards());

            cardImage.opacityProperty().bind((Bindings.when(isPlayable).then(PLAYABLE_CARD_OPACITY).otherwise(UNPLAYABLE_CARD_OPACITY)));

            cardImage.disableProperty().bind(isPlayable.not());

            handPane.getChildren().add(cardImage);
        }

        return handPane;
    }

    private List<BorderPane> createVictoryPanes(Map<PlayerId, String> names, ScoreBean score) {

        List<BorderPane> victoryPaneList = new ArrayList<>();

        for (TeamId t: TeamId.ALL) {
            Text text = new Text();
            if (t==TeamId.TEAM_1) {
                text.textProperty().bind(Bindings.format("%s et %s ont gagné avec %d points contre %d",names.get(PlayerId.PLAYER_1),names.get(PlayerId.PLAYER_3), score.totalPointsProperty(TeamId.TEAM_1),score.totalPointsProperty(TeamId.TEAM_2))); 
            }
            else {
                text.textProperty().bind(Bindings.format("%s et %s ont gagné avec %d points contre %d", names.get(PlayerId.PLAYER_2),names.get(PlayerId.PLAYER_4), score.totalPointsProperty(TeamId.TEAM_2), score.totalPointsProperty(TeamId.TEAM_1)));
            }
            BorderPane victoryPane = new BorderPane(text);
            victoryPane.setStyle("-fx-font: 16 Optima; -fx-background-color: white;");
            victoryPane.visibleProperty().bind(score.winningTeamProperty().isEqualTo(t));
            victoryPaneList.add(victoryPane);
        }
        return victoryPaneList;

    }


    private GridPane createScorePane(ScoreBean score, Map<PlayerId, String> playerNames) {

        GridPane scorePane = new GridPane();

        for(int i=0;i<TeamId.COUNT;++i) {

            //The name of the teams
            Text teamNames = new Text(playerNames.get(PlayerId.ALL.get(i))+" et "+playerNames.get(PlayerId.ALL.get(i+2))+" : ");
            scorePane.addRow(i, teamNames);
            GridPane.setHalignment(teamNames, HPos.RIGHT);

            //The points won during the turn
            Text turnPoints = new Text("0");
            IntegerProperty copyTps = new SimpleIntegerProperty();
            score.turnPointsProperty(TeamId.ALL.get(i)).addListener((o, oV, nV) -> {
                copyTps.set(nV.intValue());
                turnPoints.setText(Bindings.convert(copyTps).get());
            });
            scorePane.addRow(i, turnPoints);
            GridPane.setHalignment(turnPoints, HPos.RIGHT);

            //The points won in the last trick
            Text additionalTrickPoints = new Text();
            score.turnPointsProperty(TeamId.ALL.get(i)).addListener((o, oV, nV) -> {
                if(nV.intValue() == oV.intValue() || (nV.intValue() - oV.intValue()) < 0) { 
                    additionalTrickPoints.setText(" ");
                } else {
                    additionalTrickPoints.setText(" (+"+(nV.intValue() - oV.intValue())+") ");
                }
            });
            scorePane.addRow(i, additionalTrickPoints);

            Text total = new Text(" / Total : ");
            scorePane.addRow(i, total);

            //The game points
            Text gamePoints = new Text(Bindings.convert(score.gamePointsProperty(TeamId.ALL.get(i))).get());
            IntegerProperty gameTps = new SimpleIntegerProperty();
            score.gamePointsProperty(TeamId.ALL.get(i)).addListener((o, oV, nV) -> {
                gameTps.set(nV.intValue());
                gamePoints.setText(Bindings.convert(gameTps).get());
            });
            scorePane.addRow(i, gamePoints);
            GridPane.setHalignment(gamePoints, HPos.RIGHT);

        }

        scorePane.setStyle("-fx-font: 16 Optima; "
                + "-fx-background-color: lightgray; "
                + "-fx-padding: 5px; "
                + "-fx-alignment: center;");
        return scorePane;
    }



    private GridPane createTrickPane (PlayerId ownId, Map<PlayerId, String> names,  TrickBean trick) {
        GridPane trickPane = new GridPane();

        trickPane.setStyle("-fx-background-color: whitesmoke; -fx-padding: 5px; -fx-border-width: 3px 0px; -fx-border-style: solid; -fx-border-color: gray; -fx-alignment: center;");

        for(int i=0; i<3;++i) {
            trickPane.addRow(i);
            trickPane.addColumn(i);
        }

        List<VBox> vBoxList = new ArrayList<>();


        for (int i=0; i<PlayerId.COUNT; ++i) {

            PlayerId player =(PlayerId.ALL.get((ownId.ordinal()+i)%4));

            // Creation of the name text
            Text name = new Text (names.get(player));
            name.setStyle("-fx-font: 14 Optima;");

            // Creation of the imageView of the card    
            ImageView cardImage = new ImageView();
            cardImage.setFitHeight(CARD_IMAGE_HEIGHT);
            cardImage.setFitWidth(CARD_IMAGE_WIDTH);

            // Bindings of the imageProperty of the the imageView of the card with the corresponding card in the trick 
            SimpleObjectProperty<Card> cardIndex = new SimpleObjectProperty<>();
            cardIndex.bind(Bindings.valueAt(trick.trick(), player));
            cardImage.imageProperty().bind(Bindings.valueAt(imageCardsMap,cardIndex));


            // Creation of the red rectangle around the best card  
            Rectangle halo = new Rectangle(cardImage.getFitWidth(), cardImage.getFitHeight());    
            halo.setStyle("-fx-arc-width: 20; -fx-arc-height: 20; -fx-fill: transparent; -fx-stroke: lightpink; -fx-stroke-width: 5; -fx-opacity: 0.5;");   
            halo.setEffect(new GaussianBlur(BLUR_EFFECT_RADIUS)); 
            halo.visibleProperty().bind(trick.winningPlayerProperty().isEqualTo(player));

            // Creation of the StackPane 
            StackPane imageStack = new StackPane(cardImage,halo);

            // Creation of the VBox with the different node
            VBox b = new VBox();
            b.setStyle("-fx-padding: 5px; -fx-alignment: center;");
            if (player == ownId) {
                b.getChildren().addAll(imageStack,name);
            }
            else {
                b.getChildren().addAll(name,imageStack);
            }

            vBoxList.add(b);
        }

        ImageView trump = new ImageView(); 

        trump.imageProperty().bind(Bindings.valueAt(imageTrumpMap, trick.trumpProperty()));

        trump.setFitHeight(TRUMP_IMAGE_SIZE);
        trump.setFitWidth(TRUMP_IMAGE_SIZE);

        //Positioning of the vBox corresponding to ownId card 
        trickPane.add(vBoxList.get(0), 1, 2);

        //Positioning of the vBox corresponding to the card of the player at the left of ownId
        trickPane.add(vBoxList.get(1), 2, 0, 1, 3);

        //Positioning of the vBox corresponding to the card of the player in front of ownId
        trickPane.add(vBoxList.get(2), 1, 0);

        //Positioning of the vBox corresponding to the card of the player at the right of ownId
        trickPane.add(vBoxList.get(3), 0, 0, 1, 3);

        //Positioning of the vBox corresponding to the image of the trump in the center
        trickPane.add(trump, 1, 1);

        GridPane.setHalignment(trump, HPos.CENTER);

        return trickPane;
    }


    private static ObservableMap<Card, Image> createImageCardMap() {
        ObservableMap <Card, Image> map = FXCollections.observableHashMap();

        for (int i=0; i<CardSet.ALL_CARDS.size(); ++i) {
            Card c = CardSet.ALL_CARDS.get(i);
            map.put(c, new Image("/card_"+c.color().ordinal()+"_"+c.rank().ordinal()+"_240.png"));
        }
        return map;        
    }

    private static ObservableMap<Card, Image> createImageHandCardMap() {
        ObservableMap <Card, Image> map = FXCollections.observableHashMap();

        for (int i=0; i<CardSet.ALL_CARDS.size(); ++i) {
            Card c = CardSet.ALL_CARDS.get(i);
            map.put(c, new Image("/card_"+c.color().ordinal()+"_"+c.rank().ordinal()+"_160.png"));
        }
        return map;        
    }

    private static ObservableMap<Color, Image> createImageTrumpMap(){
        ObservableMap <Color, Image> map = FXCollections.observableHashMap();
        for (int i=0; i<Card.Color.COUNT; ++i) {
            Color c = Card.Color.ALL.get(i);
            map.put(c, new Image("/trump_"+c.ordinal()+".png"));
        }
        return map;
    }

    /**
     * Creates the pane for the chat room graphics
     * @param names (Map<PlayerId, String>): the names of the players
     * @param ownId (PlayerId): the id of the underlying player
     * @return (GridPane): the pane associated to the chat room
     */
    private GridPane createChatPane(Map<PlayerId, String> names, PlayerId ownId) {
        GridPane mainPane = new GridPane();
        chat = new VBox();
        scrollPane = new ScrollPane();
        TextField writer = new TextField();

        chat.setSpacing(3);
        writer.setOnKeyPressed(e -> {
            if(e.getCode().equals(KeyCode.ENTER)) {
                c.writeSomething(names.get(ownId)+": "+writer.getText());
                writer.clear();
            }
        });
        writer.setMaxWidth(100);
        writer.setMinWidth(100);

        Button b = new Button("Clear");
        b.setOnMouseClicked(e -> {
            chat.getChildren().clear();
        });
        GridPane writePane = new GridPane();
        writePane.add(writer, 0, 0);
        writePane.add(b, 1, 0);

        scrollPane.setMaxHeight(514);
        scrollPane.setMinHeight(514);
        scrollPane.setMaxWidth(151);
        scrollPane.setContent(chat);
        mainPane.add(writePane, 0, 1);
        mainPane.add(scrollPane, 0, 0);

        return mainPane;
    }

    /**
     * Method to write the given message to the chat room
     * @param s (String): the message to send
     */
    public void writeOnPane(String s) {
        Platform.runLater(() -> {
            chat.getChildren().add(new Text(s));
            scrollPane.setVvalue(scrollPane.hmaxProperty().get());
        });
    }
}
