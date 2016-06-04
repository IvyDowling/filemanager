import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class OpeningLayout implements Displayable {

    //elements
    final Label title = new Label(InetAddress.getLocalHost().getHostName());
    final Button home = new Button("home");
    final Button refresh = new Button("refresh");
    final Button back = new Button("<<");
    final Label cmd = new Label("cmd:");
    final TextField commandField = new TextField();
    final TextArea output = new TextArea();
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
        listView.setMinSize(300, 400);
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
                showDirs(getCurrent());
            }
        });
        refresh.setOnKeyPressed((k) -> {
            if (k.getCode() == KeyCode.ENTER) {
                showDirs(getCurrent());
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
            showDirs(getCurrent());
        });
        refresh.setOnMouseReleased((e) -> {
            showDirs(getCurrent());
        });
        back.setOnMouseReleased((e) -> {
            back();
        });

        //text field exe
        commandField.setMinWidth(300);
        commandField.setOnKeyPressed((k) -> {
            if (k.getCode() == KeyCode.ENTER) {
                commandField.setText("");
                visit(commandField.getText());
            }
        });
        //output Text Area
        output.setPadding(new Insets(5, 5, 5, 5));
        output.setEditable(false);
        //get init data
        curdir = homedir = System.getProperty("user.home");
        showDirs(getCurrent());
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
        HBox bottom = new HBox(new Group());
        bottom.setPadding(new Insets(5, 5, 5, 5));
        bp.setBottom(bottom);
        bottom.getChildren().add(cmd);
        cmd.setPadding(new Insets(5, 5, 0, 0));
        bottom.getChildren().add(commandField);
        vBox.setPadding(new Insets(0, 5, 5, 0));
        bp.setLeft(vBox);
        bp.setTop(title);
        bp.setCenter(listView);
        bp.setRight(output);
        return bp;
    }

    private String[] getCurrent() {
        File f = new File(curdir);
        if (f != null) {
            String[] local = f.list();
            Arrays.sort(local);
            return local;
        } else {
            return new String[]{};
        }
    }

    private void back() {
        System.out.println(curdir);
        if (curdir.lastIndexOf('/') != 0) {
            gotoDir(curdir.substring(0, curdir.lastIndexOf('/')));
        } else {
            gotoDir("/");
        }
    }

    private void gotoDir(String dir) {
        if (dir == null || dir.equals("")) {
            gotoDir("/");
        }
        curdir = dir;
        showDirs(getCurrent());
    }

    private void navigate(String d) {
        //this 4 is to skip the "(l) " in front of each list element
        if (curdir.equals("/")) {
            curdir += d.substring(4, d.length());
        } else {
            curdir += "/" + d.substring(4, d.length());
        }
        if (new File(curdir).isFile()) {
//            showDirs();
        }
        showDirs(getCurrent());
    }

    private void showDirs(String[] loc) {
        //label dirs vs files
        for (int i = 0; i < loc.length; i++) {
            if (new File(curdir + "/" + loc[i]).isDirectory()) {
                loc[i] = "(d) " + loc[i];
            } else {
                loc[i] = "(f) " + loc[i];
            }
        }
        dirs.clear();
        dirs.addAll(loc);
    }

    private void visit(String command) {
        System.setProperty("user.dir", curdir);
        String[] args = new String[]{"/bin/bash", "-c", command};
        output.clear();
        try {
            Process proc = new ProcessBuilder(args).start();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(proc.getInputStream()));
            List<String> out = new LinkedList<>();
            String init = reader.readLine();
            while (init != null) {
                output.setText(output.getText() + init);
                init = reader.readLine();
            }
        } catch (IOException e) {
            System.out.println("command: " + command);
        }
    }


}
