package com.webstudio.view;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.flowless.VirtualizedScrollPane;
import javafx.scene.web.WebView;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ViewController {

    @FXML
    public BorderPane mainWindow;
    @FXML
    private StackPane mainPane;
    @FXML
    private Pane titlePane;

    private File file;

    private double x, y;

    private Stage stage;

    @FXML
    private ImageView btnClose, btnMinimize, btnFullWindow, btnPlay;
    private final CustomCodeArea codeArea;

    private final int btnCloseX = 960;

    public ViewController() {
        mainWindow = new BorderPane();
        mainPane = new StackPane();
        titlePane = new Pane();
        mainWindow.getStyleClass().add("mainWindow");
        mainPane.getStyleClass().add("mainPane");
        codeArea = new CustomCodeArea();
    }

    public void init(Stage st) {
        stage = st;
        codeArea.getStyleClass().add("codeArea");
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        VirtualizedScrollPane<CustomCodeArea> vsPane = new VirtualizedScrollPane<>(codeArea);
        mainPane.getChildren().add(vsPane);
        vsPane.getStyleClass().add("vsPane");
    }

    public void closeWindow(Stage stage) {
        btnClose.setOnMouseClicked(mouseEvent -> {
            if (codeArea.getText().isEmpty()) {
                stage.close();
                return;
            }
            Alert alert = new Alert(Alert.AlertType.NONE, "Exit without saving?", ButtonType.YES,
                ButtonType.NO);
            alert.setTitle("Confirm");
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) stage.close();
            if (alert.getResult() == ButtonType.NO) {
                save();
            }
        });
    }

    public void minimizeWindow(Stage stage) {
        btnMinimize.setOnMouseClicked(mouseEvent -> stage.setIconified(true));
    }

    public void pushPlay() {
        btnPlay.setOnMouseClicked(mouseEvent -> {
            WebView webView = new WebView();
            WebEngine webEngine = webView.getEngine();
            webEngine.loadContent(codeArea.getText(), "text/html");
            VBox vBox = new VBox(webView);
            Scene scene = new Scene(vBox, 960, 600);
            Stage stage2 = new Stage();
            stage2.initModality(Modality.APPLICATION_MODAL);
            stage2.setScene(scene);
            stage2.show();
        });
    }

    public void pusESC(Stage stage) {
        stage.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode().getName().equals("Esc")) {
                relocateButtons(stage);
            }
        });
    }

    public void fullWindow(Stage stage) {
        btnFullWindow.setOnMouseClicked(mouseEvent ->{
            stage.setFullScreen(true);
            relocateButtons(stage);
        });
    }

    public void turnWindow(Stage stage) {
        titlePane.setOnMousePressed(mouseEvent -> {
            x = mouseEvent.getSceneX();
            y = mouseEvent.getSceneY();
        });
        titlePane.setOnMouseDragged(mouseEvent -> {
            stage.setX(mouseEvent.getScreenX() - x);
            stage.setY(mouseEvent.getScreenY() - y);

        });
    }

    public void resizeWindow(Stage stage) {
        mainPane.setOnMouseDragged(mouseEvent -> {
            if (mouseEvent.getX() >= 1000 && mouseEvent.getY() >= 800) {
                stage.setWidth(mouseEvent.getX());
                stage.setHeight(mouseEvent.getY());
            }

            if ((mouseEvent.getX() - 40) > btnCloseX) {
                relocateButtons(stage);
            }
        });
    }

    private void relocateButtons(Stage stage) {
        btnClose.relocate(stage.getWidth() - 40, btnClose.getLayoutY());
        btnMinimize.relocate(stage.getWidth() - 67, btnMinimize.getLayoutY());
        btnFullWindow.relocate(stage.getWidth() - 92, btnFullWindow.getLayoutY());
    }

    @FXML
    private void saveAs() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save As");
            file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                BufferedWriter out = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
                out.write(codeArea.getText());
                out.close();
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: " + e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void save() {
        if (file != null) {
            try {
                BufferedWriter out = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
                out.write(codeArea.getText());
                out.close();
            } catch (FileNotFoundException e) {
                System.out.println("Error: " + e);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            saveAs();
        }
    }

    @FXML
    private void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            codeArea.clear();
            readText(file);
        }
    }

    private void readText(File file) {
        String text;

        try (BufferedReader buffReader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            while ((text = buffReader.readLine()) != null) {
                codeArea.appendText(text + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader buffReader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            while ((text = buffReader.readLine()) != null) {
                codeArea.appendText(text + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void newFile() {
        codeArea.clear();
    }

}