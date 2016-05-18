import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.UnknownHostException;

public class UI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) {
        stage.setTitle("FM");
        Scene scene = new Scene(new Group(), 400, 500);
        stage.getIcons().add(new Image("file: res/icon.png"));

        Group root = (Group) scene.getRoot();
        try {
            root.getChildren().add(new OpeningLayout(scene, "dark.css").getPane());
        } catch (UnknownHostException uhe){
            //no privs
            System.err.print("couldn't get hostname");
            uhe.printStackTrace();
        }
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
        stage.sizeToScene();
    }
}
