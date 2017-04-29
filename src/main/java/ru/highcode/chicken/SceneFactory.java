package ru.highcode.chicken;

import java.io.IOException;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.converter.NumberStringConverter;

public class SceneFactory {
    public static Scene textScene(String text, ISceneSwitcher switcher) {
        final VBox pane = new VBox();
        pane.setAlignment(Pos.CENTER);
        final Text t = new Text(text);
        t.setWrappingWidth(800);
        t.setTextAlignment(TextAlignment.JUSTIFY);
        pane.setSpacing(50);
        pane.getChildren().add(t);
        final Button nextButton = new Button("Продолжить");
        pane.getChildren().add(nextButton);
        nextButton.setOnAction(e -> {
            switcher.nextScene();
        });
        return new Scene(pane);
    }

    public static Scene loginScene(ExperimentHistory experiment, ISceneSwitcher switcher) {
        final GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(20);
        pane.setVgap(20);
        pane.add(new Label("Данные об испытуемом"), 0, 0, 2, 1);
        pane.add(new Label("Номер"), 0, 1);

        final TextField playerNumber = new TextField();
        playerNumber.textProperty().bindBidirectional(experiment.playerNumberProperty(), new NumberStringConverter());
        playerNumber.setTextFormatter(new TextFormatter<>(c -> {
            try {
                Long.parseLong(c.getControlNewText());
            } catch (final Exception e) {
                return null;
            }
            return c;
        }));
        pane.add(playerNumber, 1, 1);
        final HBox bbox = new HBox();
        final Button button = new Button("Старт");
        button.setOnAction(e -> {
            switcher.nextScene();
        });
        bbox.getChildren().add(button);
        bbox.setAlignment(Pos.BOTTOM_RIGHT);
        pane.add(bbox, 0, 2, 2, 1);
        return new Scene(pane);
    }

    public static Scene rateGameScene(String gameName, ExperimentHistory experiment, ISceneSwitcher switcher) {
        final GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(20);
        pane.setVgap(20);
        final String RISK_VALUATION_TEXT = "Оцени, насколько рискованным было твое поведение в прошлом раунде от 0 до 10"
                + "\n" + "(где 0 — совсем не рискованное, 10 — очень рискованное).";
        pane.add(new Label(RISK_VALUATION_TEXT), 0, 0, 2, 1);
        pane.add(new Label("Оценка"), 0, 1);
        final TextField playerGameRate = new TextField("");
        // playerNumber.textProperty().bindBidirectional(experiment.playerNumberProperty(),
        // new NumberStringConverter());
        playerGameRate.setTextFormatter(new TextFormatter<>(c -> {
            if (c.getControlNewText().isEmpty()) {
                return c;
            }
            try {
                final int rate = Integer.parseInt(c.getControlNewText());
                if (rate >= 0 && rate <= 10) {
                    return c;
                }
                return null;
            } catch (final Exception e) {
                return null;
            }
        }));
        pane.add(playerGameRate, 1, 1);
        final HBox bbox = new HBox();
        final Button button = new Button("Оценить");
        button.disableProperty().bind(Bindings.isEmpty(playerGameRate.textProperty()));
        button.setOnAction(e -> {
            switcher.nextScene();
        });
        bbox.getChildren().add(button);
        bbox.setAlignment(Pos.BOTTOM_RIGHT);
        pane.add(bbox, 0, 2, 2, 1);
        return new Scene(pane);
    }

    public static Scene gameScene(String gameName, ExperimentHistory experiment, ISceneSwitcher switcher)
            throws IOException {
        final GameRoundScene gscene = new GameRoundScene(gameName, experiment, switcher);
        return gscene.getScene();
    }
}
