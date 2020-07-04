package ch.epfl.javass;

import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class TestPane extends Application {

    private static String[] args = new String[5];

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        BorderPane mainPane = new BorderPane();
        BorderPane topPane = new BorderPane();
        topPane.setTop(new ImageView("javass_front_page.png"));
        topPane.setStyle("-fx-background-color: whitesmoke; -fx-padding: 5px; -fx-border-width: 3px 0px; -fx-border-style: solid; -fx-border-color: gray; -fx-alignment: center;");
        mainPane.setTop(topPane);
        StackPane distantPane = new StackPane();
        
        GridPane distantP1 = new GridPane();
        mainPane.setCenter(distantP1);
        
        //ImageView distant1 = new ImageView("distant.png");
        //distantP1.add(distant1, 1, 0);
        
        Button distant1 = new Button("Distant Player");
        distantP1.add(distant1, 1, 0);
        
//        ImageView imageTest = new ImageView("javass_front_page.png");
//        mainPane.setTop(imageTest);
//        BorderPane.setAlignment(imageTest, Pos.CENTER);
//        
//        GridPane gridPane = new GridPane();
//
//        GridPane joueur1 = new GridPane();
//        ImageView imgJoueur1 = new ImageView("joueur_1.png");
//        Text blank1 = new Text("          ");
//        ImageView humain1 = new ImageView("human.png");
//        Text humanPlayer1 = new Text("Joueur humain");
//        joueur1.add(blank1, 1, 1);
//        joueur1.add(humain1, 1, 2);
//        joueur1.add(humanPlayer1, 1, 3);
//        GridPane.setHalignment(humain1, HPos.CENTER);
//        GridPane.setHalignment(humanPlayer1, HPos.CENTER);
//        joueur1.add(imgJoueur1, 1, 0);
//        gridPane.add(joueur1, 0, 0);
//        
//        
//
//        GridPane joueur2 = new GridPane();
//        ImageView imgJoueur2 = new ImageView("joueur_2.png");
//        Text blank2 = new Text("          ");
//        ImageView distant2 = new ImageView("distant.png");
//        Text distantPlayer2 = new Text("Joueur distant");
//        joueur2.add(blank2, 1, 1);
//        joueur2.add(distant2, 1, 2);
//        joueur2.add(distantPlayer2, 1, 3);
//        GridPane.setHalignment(distant2, HPos.CENTER);
//        GridPane.setHalignment(distantPlayer2, HPos.CENTER);
//        joueur2.add(imgJoueur2, 1, 0);
//        gridPane.add(joueur2, 1, 0);
//        TextField giveIp = new TextField();
//        joueur2.add(giveIp, 1, 4);
//        
//
//        GridPane joueur3 = new GridPane();
//        ImageView imgJoueur3 = new ImageView("joueur_3.png");
//        Text blank3 = new Text("          ");
//        ImageView simulate3 = new ImageView("simulate.png");
//        Text simulatePlayer3 = new Text("Joueur simulé");
//        joueur3.add(blank3, 1, 1);
//        joueur3.add(simulate3, 1, 2);
//        joueur3.add(simulatePlayer3, 1, 3);
//        GridPane.setHalignment(simulate3, HPos.CENTER);
//        GridPane.setHalignment(simulatePlayer3, HPos.CENTER);
//        joueur3.add(imgJoueur3, 1, 0);
//        gridPane.add(joueur3, 2, 0);
//
//        GridPane joueur4 = new GridPane();
//        ImageView imgJoueur4 = new ImageView("joueur_4.png");
//        Text blank4 = new Text("          ");
//        ImageView humain4 = new ImageView("human.png");
//        Text humanPlayer4 = new Text("Joueur humain");
//        joueur4.add(blank4, 1, 1);
//        joueur4.add(humain4, 1, 2);
//        joueur4.add(humanPlayer4, 1, 3);
//        GridPane.setHalignment(humain4, HPos.CENTER);
//        GridPane.setHalignment(humanPlayer4, HPos.CENTER);
//        joueur4.add(imgJoueur4, 1, 0);
//        gridPane.add(joueur4, 3, 0);
        
        
        
        //mainPane.setLeft(gridPane);
        //        imageTest.setOnMouseClicked(e -> {
        //            imageTest.opacityProperty().set(0);
        //            args[0] = "test";
        //        });

        Stage s = createStage(mainPane);
        s.show();
        s.setTitle("Javass");
    }

    private Stage createStage(Pane pane) {
        Stage s = new Stage();
        Scene scene = new Scene(pane);
        s.setScene(scene);
        return s;
    }
    public void test(List<String> e) {
        
   }
}
