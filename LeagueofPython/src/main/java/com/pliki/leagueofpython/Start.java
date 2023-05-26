package com.pliki.leagueofpython;

import com.pliki.leagueofpython.controllers.*;
import javafx.application.Application;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Start extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        StageClass.setStage(stage);
        Manager manager=new Manager();
        manager.setFactory("Baza");
        List<Scena> sceny=new ArrayList<>();
        sceny.add(new Scena("start.fxml","styl2.css"));
        sceny.add(new Scena("menu.fxml","styl2.css"));
        sceny.add(new Scena("pole.fxml","styl1.css"));
        sceny.add(new Scena("graj.fxml","styl2.css"));
        sceny.add(new Scena("ranking.fxml","styl2.css"));
        sceny.add(new Scena("wyslane.fxml","styl2.css"));
        sceny.add(new Scena("przeslij.fxml","styl2.css"));
        sceny.add(new Scena("poczta.fxml","styl2.css"));
        sceny.add(new Scena("kod.fxml","styl2.css"));
        sceny.add(new Scena("zgloszenia.fxml","styl2.css"));
        sceny.add(new Scena("zgloszenie.fxml","styl2.css"));
        sceny.add(new Scena("uzytkownicy.fxml","styl2.css"));
        StartController startController= sceny.get(0).getFxmlLoader().getController();
        startController.setScene(sceny.get(1).getScene());
        MenuController menuController=sceny.get(1).getFxmlLoader().getController();
        menuController.setSceny(sceny);
        LoadFonts loadFonts=new LoadFonts();
        loadFonts.load();
        TitleAndImage titleAndImage=new TitleAndImage("icon.png","League of Python",stage);
        stage.setScene(sceny.get(4).getScene());
        stage.show();
        GrajController grajController=sceny.get(3).getFxmlLoader().getController();
        grajController.setSceny(sceny.get(1),sceny.get(2));
        PrzeslijController przeslijController=sceny.get(6).getFxmlLoader().getController();
        przeslijController.setScene(sceny.get(1));
        RankingController rankingController=sceny.get(4).getFxmlLoader().getController();
        rankingController.setSceny(sceny.get(1),sceny.get(5));
        WyslaneController wyslaneController=sceny.get(5).getFxmlLoader().getController();
        wyslaneController.setSceny(sceny.get(1),sceny.get(8));
        PocztaController pocztaController=sceny.get(7).getFxmlLoader().getController();
        pocztaController.setScene(sceny.get(1));
        KodController kodController=sceny.get(8).getFxmlLoader().getController();
        kodController.setScene(sceny.get(5));
        ZgloszeniaController zgloszeniaController=sceny.get(9).getFxmlLoader().getController();
        zgloszeniaController.setSceny(sceny.get(1),sceny.get(1));
        ZgloszenieController zgloszenieController=sceny.get(10).getFxmlLoader().getController();
        zgloszenieController.setScene(sceny.get(9));
        UzytkownicyController uzytkownicyController=sceny.get(11).getFxmlLoader().getController();
        uzytkownicyController.setScene(sceny.get(1));
        stage.setOnCloseRequest(event -> {
            manager.close();
        });



    }


    public static void main(String[] args)
    {
        launch();
    }
}