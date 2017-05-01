package ru.highcode.chicken;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import ru.highcode.chicken.data.Experiment;
import ru.highcode.chicken.data.Round;

public class GameRoundScene {
    // TODO clean layout
    // TODO refactor logic
    // TODO log writer
    private final Scene scene;
    private final double roundTime;
    private final Properties settings = new Properties();
    private final Round round;
    private final Image winImage = new Image("file:win.png");
    private final Image failImage = new Image("file:fail.png");

    private long switchSceneDelay;
    /**
     * @param experiment
     * @param roundTime
     *            round time in seconds.
     * @throws IOException
     * @throws FileNotFoundException
     */
    public GameRoundScene(String gameName, Experiment experiment, ISceneSwitcher switcher)
            throws FileNotFoundException, IOException {
        Font bigFont = new Font(32);
        
        settings.load(new FileReader("game.cfg"));
        this.round = experiment.getRound(gameName);
        this.roundTime = Long.parseLong(settings.getProperty(gameName + ".roundTime"));

        BorderPane pane = new BorderPane();

        final GridPane scorePane = new GridPane();

        final Text currentScoreText = new Text("0");
        currentScoreText.setFont(bigFont);
        final Text totalScoreText = new Text("0");
        totalScoreText.setFont(bigFont);

        // TODO replace with timer
        final Button nextButton = new Button("Продолжить");
        nextButton.setOnAction(e -> {
            switcher.nextScene();
        });
        nextButton.setVisible(false);

//        if (!round.isPractics()) {
            Text totalScoreLabel = new Text("Очки за всю игру: ");
            totalScoreLabel.setFont(bigFont);
            scorePane.add(totalScoreLabel, 0, 0);
            scorePane.add(totalScoreText, 1, 0);
//        }
        Text roundScoreLabel = new Text("Очки за раунд: ");
        roundScoreLabel.setFont(bigFont);
        scorePane.add(roundScoreLabel, 0, 1);
        scorePane.add(currentScoreText, 1, 1);
        scorePane.setPadding(new Insets(20));

        pane.setTop(scorePane);

        VBox wrapper = new VBox();
        final ImageView trafficLight = new ImageView(TrafficLightState.GREEN.getImage());
        wrapper.getChildren().add(trafficLight);
        wrapper.setPadding(new Insets(0, 100, 0, 0));
        pane.setRight(wrapper);

        final CarWay carWay = new CarWay(settings, round);
//        carWay.setBorder(new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.DASHED, null, null)));
        carWay.setPadding(new Insets(0,0,200,0));
        VBox bottom = new VBox();
        bottom.getChildren().add(carWay);
        bottom.getChildren().add(nextButton);
        pane.setBottom(bottom);

        ImageView resultImage = new ImageView();
        pane.setCenter(resultImage);
        final AnimationTimer at = new AnimationTimer() {
            @Override
            public void handle(long currentNanoTime) {
                carWay.update(currentNanoTime);
                if (isRoundStarted(carWay, currentNanoTime)) {
                    if (trafficLight.getImage() != TrafficLightState.YELLOW.getImage()) {
                        trafficLight.setImage(TrafficLightState.YELLOW.getImage());
                    }
                    final double time = (currentNanoTime - carWay.getRoundStartNanoTime()) / 1000000000.0;

                    currentScoreText.setText(String.valueOf(round.getTotalScore()));
                    totalScoreText.setText(String.valueOf(experiment.getTotalScore()));
                }
                if (isRoundEnded(carWay, currentNanoTime)) {
                    if (trafficLight.getImage() != TrafficLightState.RED.getImage()) {
                        trafficLight.setImage(TrafficLightState.RED.getImage());
                    }
                    carWay.stop();
                    nextButton.setVisible(true);
                    nextButton.setFocusTraversable(false);

                    if (carWay.isWin()) {
                        resultImage.setImage(winImage);
//                        roundTimeText.setText("WIN!!!");
                    } else {
                        resultImage.setImage(failImage);
//                        roundTimeText.setText("LOOSE!!!");
                    }
                    if(switchSceneDelay == 0) {
                        switchSceneDelay = System.nanoTime();
                    } else {
                        // 3 sec
                        if((System.nanoTime() - switchSceneDelay) >   3 * 1000000000l) {
                            this.stop();  
                            switcher.nextScene();
                        }
                    }
                }
            }

        };
        at.start();

        scene = new Scene(pane, 800, 600);
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                carWay.startEngine();
            }
        });
        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                carWay.stopEngine();
            }
        });
    }

    private boolean isRoundStarted(final CarWay carWay, long currentNanoTime) {
        if (carWay.getRoundStartNanoTime() > 0) {
            final double time = (currentNanoTime - carWay.getRoundStartNanoTime()) / 1000000000.0;
            if (time <= roundTime) {
                return true;
            }
        }
        return false;
    }

    private boolean isRoundEnded(final CarWay carWay, long currentNanoTime) {
        if (carWay.getRoundStartNanoTime() > 0) {
            final double time = (currentNanoTime - carWay.getRoundStartNanoTime()) / 1000000000.0;
            if (time > roundTime) {
                return true;
            }
        }
        return false;
    }

    public Scene getScene() {
        return scene;
    }
}
