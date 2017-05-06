package ru.highcode.chicken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.application.Application;
import javafx.stage.Stage;
import ru.highcode.chicken.data.Experiment;

public class Game extends Application implements IGame {
    public static final String PRACTICS_NAME_1 = "practics1";
    public static final String PRACTICS_NAME_2 = "practics2";
    private final List<IChickenScene> scenario = new ArrayList<>();
    private Stage primaryStage;
    private int currentScene = 0;
    final Map<String, String> texts = GameData.load().getTexts();
    private Experiment experiment = new Experiment();

    public Game() throws IOException {
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Chicken Game");
         primaryStage.setFullScreen(true);
        // primaryStage.setMaximized(true);

        recreateScenario();

        primaryStage.setScene(scenario.get(currentScene).getScene());
        primaryStage.show();
    }

    private void recreateScenario() {
        scenario.clear();
        addScene(SceneFactory.loginScene(this));
        addScene(SceneFactory.textScene(texts.get("1"), this));
        addScene(SceneFactory.textScene(texts.get("2"), this));
        addScene(SceneFactory.gameScene(PRACTICS_NAME_1, this));
        addScene(SceneFactory.textScene(texts.get("3"), this));
        addScene(SceneFactory.gameScene(PRACTICS_NAME_2, this));
        addScene(SceneFactory.textScene(texts.get("4"), this));
        for (int i = 1; i <= 15; i++) {
            final String gameName = String.valueOf(i);
            addScene(SceneFactory.gameScene(gameName, this));
            addScene(SceneFactory.gameRoundResult(gameName, this));
            addScene(SceneFactory.rateGameScene(gameName, this));
        }
        addScene(SceneFactory.totalScoreScene(this));
        addScene(SceneFactory.textScene(texts.get("5"), "Завершить", this));
    }

    private void addScene(IChickenScene scene) {
        scenario.add(scene);
    }

    @Override
    public void nextScene() {
        currentScene = currentScene + 1;
        if (currentScene == scenario.size()) {
            currentScene = 0;
            experiment.save("data.csv");
            experiment = new Experiment();
            recreateScenario();
        }
        final IChickenScene scene = scenario.get(currentScene);
        primaryStage.hide();
        scene.activated();
        primaryStage.setScene(scene.getScene());
        primaryStage.show();
    }

    @Override
    public Experiment getExperiment() {
        return experiment;
    }

}
