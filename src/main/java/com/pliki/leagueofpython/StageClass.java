package com.pliki.leagueofpython;

import javafx.stage.Stage;

public class StageClass {
    private static Stage stage;

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        StageClass.stage = stage;
    }
}
