package com.pliki.leagueofpython;

import com.pliki.leagueofpython.controllers.*;
import javafx.application.Application;
import javafx.stage.Stage;


import java.io.IOException;

public class Start extends Application {
    private StartController startController;
    private GrajController grajController;
    private PrzeslijController przeslijController;
    private WyslaneController wyslaneController;
    private RankingController rankingController;
    private PocztaController pocztaController;
    private ZgloszeniaController zgloszeniaController;
    private ZgloszenieController zgloszenieController;
    private UzytkownicyController uzytkownicyController;
    private  WeryfikacjaController weryfikacjaController;
    private KodController kodController;
    private MenuController menuController;
    private PoleController poleController;
    private WeryfikacjaSzczegolyController weryfikacjaSzczegolyController;

    @Override
    public void start(Stage stage) throws IOException {

        StageClass.setStage(stage);
        setSceny();
        setControllers();
        LoadFonts loadFonts=new LoadFonts("/fonty/FIRA.ttf","/fonty/PressStart2P.ttf");
        TitleAndImage titleAndImage=new TitleAndImage("icon.png","League of Python",stage);
        startController.resetScene();
        stage.show();

    }
    public void setSceny() throws IOException {
        Scena start=new Scena("start.fxml","/style/styl.css");
        Scena menu=new Scena("menu.fxml","/style/styl.css");
        Scena pole=new Scena("pole.fxml", "/style/styl.css");
        Scena kod=new Scena("kod.fxml","/style/styl.css");
        Scena graj=new Scena("graj.fxml","/style/styl.css");
        Scena ranking=new Scena("ranking.fxml","/style/styl.css");
        Scena wyslane=new Scena("wyslane.fxml","/style/styl.css");
        Scena przeslij=new Scena("przeslij.fxml","/style/styl.css");
        Scena poczta=new Scena("poczta.fxml","/style/styl.css");
        Scena zgloszenia=new Scena("zgloszenia.fxml","/style/styl.css");
        Scena zgloszenie=new Scena("zgloszenie.fxml","/style/styl.css");
        Scena uzytkownicy=new Scena("uzytkownicy.fxml","/style/styl.css");
        Scena weryfikacja=new Scena("weryfikacja.fxml","/style/styl.css");
        Scena weryfikacjaszczegoly=new Scena("weryfikacjaszczegoly.fxml","/style/styl.css");
        startController=start.getFxmlLoader().getController();
        menuController=menu.getFxmlLoader().getController();
        poleController=pole.getFxmlLoader().getController();
        kodController=kod.getFxmlLoader().getController();
        grajController=graj.getFxmlLoader().getController();
        rankingController=ranking.getFxmlLoader().getController();
        wyslaneController=wyslane.getFxmlLoader().getController();
        przeslijController=przeslij.getFxmlLoader().getController();
        pocztaController=poczta.getFxmlLoader().getController();
        zgloszeniaController=zgloszenia.getFxmlLoader().getController();
        zgloszenieController=zgloszenie.getFxmlLoader().getController();
        uzytkownicyController=uzytkownicy.getFxmlLoader().getController();
        weryfikacjaController=weryfikacja.getFxmlLoader().getController();
        weryfikacjaSzczegolyController=weryfikacjaszczegoly.getFxmlLoader().getController();
    }


    public void setControllers()
    {
        startController.setController(menuController);
        wyslaneController.setControllers(menuController,kodController,rankingController,grajController);
        menuController.setControllers(startController,grajController,rankingController,wyslaneController,przeslijController,pocztaController,uzytkownicyController,zgloszeniaController,weryfikacjaController);
        poleController.setController(grajController);
        kodController.setController(wyslaneController);
        grajController.setControllers(menuController,poleController);
        rankingController.setControllers(menuController,wyslaneController);
        przeslijController.setController(menuController);
        pocztaController.setController(menuController);
        zgloszeniaController.setControllers(menuController,zgloszenieController);
        zgloszenieController.setController(zgloszeniaController);
        uzytkownicyController.setController(menuController);
        weryfikacjaController.setControllers(menuController,weryfikacjaSzczegolyController);
        weryfikacjaSzczegolyController.setController(weryfikacjaController);
    }


    public static void main(String[] args)
    {
        launch();
    }
}