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

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class OpeningLayout implements Displayable {

    //elements
    final Label title = new Label(InetAddress.getLocalHost().getHostName());
    final Button home = new Button("home");
    final Button refresh = new Button("refresh");
    final Button back = new Button("<<");
    final TextArea output = new TextArea();
    final TextField workingDir = new TextField();
    private ObservableList<String> dirs = FXCollections.observableArrayList();
    private ListView<String> listView = new ListView<>(dirs);

    private String homedir = "";
    private String curdir = "";
    private String stylesheet = "light.css";

    public OpeningLayout(Scene scene, String ss) throws UnknownHostException {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(ss);
        stylesheet = ss;
        //setup elements
        title.setId("title");
        //listview
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
        //working dir Text Field Action
        workingDir.setOnKeyPressed((k) -> {
            if (k.getCode() == KeyCode.ENTER) {
                if (new File(workingDir.getText()).exists()) {
                    curdir = workingDir.getText();
                    gotoDir(curdir);
                }
            }
        });

        //output Text Area
        output.setPadding(new Insets(5, 5, 5, 5));
        output.setWrapText(true);
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
        //left

        //top
        HBox top = new HBox(new Group());
        top.getChildren().add(title);
        top.getChildren().add(home);
        top.getChildren().add(refresh);
        top.getChildren().add(back);
        bp.setTop(top);
        //bottom

        //center
        VBox center = new VBox();
        center.getChildren().add(workingDir);
        center.getChildren().add(listView);
        workingDir.setText(getCurentDir());
        bp.setCenter(center);
        //right
        HBox right = new HBox();
        right.getChildren().add(output);
        bp.setRight(right);
        return bp;
    }

    private String[] getCurrent() {
        File f = new File(getCurentDir());
        if (f.isFile()) {
            //might be a dir
            if (f.canRead()) {
                try {
                    BufferedReader br = new BufferedReader(new FileReader(f));
                    String content = "";
                    int lines = 0;
                    while (br.ready() && lines < 50) {
                        content += br.readLine() + "\n";
                        lines++;
                    }
                    output.setText(content);
                } catch (FileNotFoundException fnf) {
                    fnf.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                output.setText("couldn't read this file! try permissions");
            }
        }
        String[] local = f.list();
        if (local != null) {
            Arrays.sort(local);
        }
        return local;

    }

    private void back() {
        if (getCurentDir().lastIndexOf('/') != 0) {
            gotoDir(getCurentDir().substring(0, getCurentDir().lastIndexOf('/')));
        } else {
            gotoDir("/");
        }
    }

    private void gotoDir(String dir) {
        if (dir == null || dir.equals("")) {
            gotoDir("/");
        }
        curdir = dir;
        workingDir.setText(getCurentDir());
        showDirs(getCurrent());
    }

    private void navigate(String d) {
        if (d != null) {
            //this 4 is to skip the "(l) " in front of each list element
            if (getCurentDir().equals("/")) {
                curdir += d.substring(4, d.length());
            } else {
                // we need to check if we're going to a dir
                if (!new File(getCurentDir() + "/" + d).isFile()) {
                    curdir += "/" + d.substring(4, d.length());
                }
                // if not, do nothing
            }
            workingDir.setText(getCurentDir());
            showDirs(getCurrent());
        }
    }

    private void showDirs(String[] loc) {
        if (loc != null) {
            //label dirs vs files
            for (int i = 0; i < loc.length; i++) {
                if (new File(getCurentDir() + "/" + loc[i]).isDirectory()) {
                    loc[i] = "(d) " + loc[i];
                } else {
                    loc[i] = "(f) " + loc[i];
                }
            }
            dirs.clear();
            dirs.addAll(loc);
        } else {
            dirs.clear();
        }
    }

    private String getCurentDir() {
        return curdir;
    }

}
