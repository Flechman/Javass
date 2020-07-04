package ch.epfl.javass;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.net.RemotePlayerServer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Main class to play a distant game
 */
public class RemoteMain extends Application {

    private String ip;
    private Stage s;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Creates a thread
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Platform.setImplicitExit(false);
        s = primaryStage;
        ip = new String("");
        BorderPane pane = new BorderPane();
        Text theGame = new Text("The game will start at the connection of the client...");
        pane.setBottom(theGame);
        theGame.visibleProperty().set(false);

        GridPane gridPane = new GridPane();
        TextField txtField = new TextField();
        txtField.maxWidthProperty().set(110);
        txtField.minWidthProperty().set(110);

        gridPane.add(txtField, 1, 0);
        gridPane.add(new Text("          Connection  ip :          "), 0, 0);
        gridPane.add(new Text("   "), 1, 1);
        gridPane.add(new Text("   "), 1, 2);
        BorderPane buttonPane = new BorderPane();
        buttonPane.visibleProperty().set(false);
        Button ipButton = new Button("Change ip");
        Button goButton = new Button("GO");
        buttonPane.setCenter(goButton);
        buttonPane.setTop(ipButton);
        buttonPane.setBottom(new Text("    "));
        BorderPane.setAlignment(ipButton, Pos.CENTER);
        BorderPane.setAlignment(goButton, Pos.CENTER);
        txtField.setOnKeyPressed(e -> {
            if(e.getCode().equals(KeyCode.ENTER)) {
                if(txtField.getText().isEmpty()) {
                    ip = "localhost";
                } else {
                    ip = txtField.getText();
                }
                gridPane.visibleProperty().set(false);
                buttonPane.visibleProperty().set(true);
            }
        });
        ipButton.setOnMouseClicked(e -> {
            gridPane.visibleProperty().set(true);
            txtField.clear();
            buttonPane.visibleProperty().set(false);
        });
        goButton.setOnMouseClicked(e -> {
            goButton.visibleProperty().set(false);
            ipButton.visibleProperty().set(false);
            theGame.visibleProperty().set(true);
            Thread serverThread = new Thread(() -> {
                RemotePlayerServer server;
                try {
                    server = new RemotePlayerServer(new GraphicalPlayerAdapter(ip), this);
                    server.receive();
                    server.run();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            });
            serverThread.setDaemon(true);
            serverThread.start();
        });

        StackPane stackPane = new StackPane(gridPane, buttonPane);
        pane.setCenter(stackPane);

        //ImageView javass = new ImageView("javass_main_title.png");
        //pane.setTop(javass);
        //BorderPane.setAlignment(javass, Pos.CENTER);
        pane.setStyle("-fx-background-color: whitesmoke; -fx-padding: 5px; -fx-border-width: 3px 0px; -fx-border-style: solid; -fx-border-color: gray; -fx-alignment: center;");



        Scene sc = new Scene(pane);
        s.setScene(sc);
        s.show();
        s.setTitle("Distant Player");

    }

    public void closeProgram() {
        Platform.runLater(() -> {
            s.close();
        });
    }

}
