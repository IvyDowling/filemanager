import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class OpeningLayout implements Displayable {

    //elements
    final Label title = new Label(InetAddress.getLocalHost().getHostName());
    final Button home = new Button("home");
    final Button refresh = new Button("refresh");
    final Button back = new Button("<<");
    private ObservableList<String> dirs = FXCollections.observableArrayList();
    private ListView<String> listView = new ListView<>(dirs);

    private String homedir = "";
    private String curdir = "";
    private String stylesheet = "dark.css";

    public OpeningLayout(Scene scene, String ss) throws UnknownHostException {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(ss);
        stylesheet = ss;
        //setup elements
        title.setId("title");
        listView.setOnMouseClicked((e) -> {
            navigate(listView.getSelectionModel().getSelectedItem());
        });
        listView.setOnKeyPressed((k) -> {
            if (k.getCode() == KeyCode.ENTER || k.getCode() == KeyCode.KP_RIGHT) {
                navigate(listView.getSelectionModel().getSelectedItem());
            }
            if (k.getCode() == KeyCode.KP_LEFT) {
                back();
            }
        });
        //fire on ENTER
        home.setOnKeyPressed((k) -> {
            if (k.getCode() == KeyCode.ENTER) {
                curdir = homedir;
                dirs.clear();
                dirs.addAll(getCurrent());
            }
        });
        refresh.setOnKeyPressed((k) -> {
            if (k.getCode() == KeyCode.ENTER) {
                dirs.clear();
                dirs.addAll(getCurrent());
            }
        });
        back.setOnKeyPressed((k) -> {
            if (k.getCode() == KeyCode.ENTER) {
                back();
            }
        });
        //setup click action
        home.setOnMouseReleased((e) -> {
            curdir = homedir;
            dirs.clear();
            dirs.addAll(getCurrent());
        });
        refresh.setOnMouseReleased((e) -> {
            dirs.clear();
            dirs.addAll(getCurrent());
        });
        back.setOnMouseReleased((e) -> {
            back();
        });
        //get init data
        curdir = homedir = System.getProperty("user.home");
        dirs.addAll(getCurrent());
    }

    public Pane getPane() {
        //gonna use a border layout for this scene
        BorderPane bp = new BorderPane();
        //format
        bp.setPadding(new Insets(5, 5, 5, 5));
        //add
        VBox vBox = new VBox(new Group());
        vBox.getChildren().add(home);
        vBox.getChildren().add(refresh);
        vBox.getChildren().add(back);
        vBox.setPadding(new Insets(0, 5, 5, 0));
        bp.setLeft(vBox);
        bp.setTop(title);
        bp.setCenter(listView);
        return bp;
    }

    private String[] getCurrent() {
        File f = new File(curdir);
        if (f != null) {
            return f.list();
        } else {
            return new String[]{};
        }
    }

    private void back() {
        gotoDir(curdir.substring(0, curdir.lastIndexOf('/')));
    }

    private void gotoDir(String dir) {
        curdir = dir;
        dirs.clear();
        dirs.addAll(getCurrent());
    }

    private void navigate(String d) {
        curdir += "/" + d;
        dirs.clear();
        dirs.addAll(getCurrent());
    }


}
