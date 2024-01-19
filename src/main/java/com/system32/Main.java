package com.system32;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;

import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import java.util.prefs.Preferences;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main extends Application implements NativeKeyListener {
    private String keyText = "F";
    private Timeline timeline;
    private static Preferences prefs = Preferences.userNodeForPackage(Main.class);

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("EasyAutoclick");
        Image icon = new Image(getClass().getResourceAsStream("/icon.png"));
        primaryStage.getIcons().add(icon);

        VBox vbox = new VBox();

        Image sunImage = new Image(getClass().getResourceAsStream("/sun.png"));
        Image moonImage = new Image(getClass().getResourceAsStream("/moon.png"));

        ImageView imageView = new ImageView(moonImage);
        imageView.setFitWidth(30);
        imageView.setFitHeight(30);
        imageView.setPickOnBounds(true);

        imageView.setOnMouseClicked(event -> {
            FadeTransition ft = new FadeTransition(Duration.millis(1000), vbox);
            ft.setFromValue(1.0);
            ft.setToValue(0.0);
            ft.setOnFinished(evt -> {
                if (imageView.getImage() == moonImage) {
                    imageView.setImage(sunImage);
                    vbox.getStylesheets().remove(getClass().getResource("/light-theme.css").toExternalForm());
                    vbox.getStylesheets().add(getClass().getResource("/dark-theme.css").toExternalForm());
                } else {
                    imageView.setImage(moonImage);
                    vbox.getStylesheets().remove(getClass().getResource("/dark-theme.css").toExternalForm());
                    vbox.getStylesheets().add(getClass().getResource("/light-theme.css").toExternalForm());
                }
                FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), vbox);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });
            ft.play();
        });

        vbox.getChildren().add(imageView);
        VBox.setMargin(imageView, new Insets(10, 10, 10, 10));
        VBox.setVgrow(imageView, Priority.ALWAYS);

        Label keyLabel = new Label("Key:");
        TextField keyField = new TextField();
        keyField.setStyle("-fx-caret-color: transparent;");
        keyField.setEditable(false);
        keyField.setText("F");
        keyField.setMaxWidth(200);

        Label timeLabel = new Label("Time (ms):");
        Spinner<Integer> timeSpinner = new Spinner<>(1, Integer.MAX_VALUE, 50);
        TextFormatter<Integer> formatter = new TextFormatter<>(new IntegerStringConverter(), 50, change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        });
        timeSpinner.getEditor().setTextFormatter(formatter);
        timeSpinner.setEditable(true);
        timeSpinner.setStyle("-fx-caret-color: transparent;");

        ToggleButton toggleButton = new ToggleButton("Left Click");
        toggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                toggleButton.setText("Right Click");
            } else {
                toggleButton.setText("Left Click");
            }
        });
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);

        vbox.getChildren().addAll(keyLabel, keyField, timeLabel, timeSpinner, toggleButton);

        Robot robot = new Robot();

        Platform.runLater(() -> vbox.requestFocus());
        timeSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            timeline.stop();
            timeline.getKeyFrames().clear();
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(newValue), e -> {

                if ((toggleButton.isSelected() ? "R" : "L").equals("R")) {
                    robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                    robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                } else {
                    robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                    robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                }
            }));
            timeline.play();
        });
        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);
        
        keyField.addEventHandler(javafx.scene.input.KeyEvent.KEY_TYPED, event -> {
            keyField.setText(event.getCharacter().toUpperCase());
            keyText = event.getCharacter();
            event.consume();
        });

        timeline.getKeyFrames().setAll(new KeyFrame(Duration.millis(timeSpinner.getValue()), e -> {
            if ((toggleButton.isSelected() ? "R" : "L").equals("R")) {
                robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
            } else {
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            }

        }));

        LogManager.getLogManager().reset();
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);

        GlobalScreen.registerNativeHook();
        GlobalScreen.addNativeKeyListener(this);

        primaryStage.setScene(new Scene(vbox, 300, 250));
        primaryStage.show();
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        if (keyText != null && NativeKeyEvent.getKeyText(e.getKeyCode()).toLowerCase().equals(keyText.toLowerCase())) {
            Platform.runLater(() -> {
                if (timeline.getStatus() == Animation.Status.RUNNING) {
                    timeline.stop();
                } else {
                    timeline.play();
                }
            });
        }
    }


    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
    }

    public static void main(String[] args) {
        launch(args);
        // new Stats(prefs);
    }
}
