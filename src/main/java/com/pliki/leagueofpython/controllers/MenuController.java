package com.pliki.leagueofpython.controllers;

import com.pliki.leagueofpython.StageClass;
import com.pliki.leagueofpython.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;

public class MenuController {

    @FXML
    private Label wiadomosc;
    @FXML
    private Label ilosc;
    @FXML
    private Button guzik_weryfikacja;
    @FXML
    private Button guzik_uzytkownicy;
    @FXML
    private Button guzik_zgloszenia;

    private StartController startController;
    private GrajController grajController;
    private RankingController rankingController;
    private WyslaneController wyslaneController;
    private PrzeslijController przeslijController;
    private PocztaController pocztaController;
    private ZgloszeniaController zgloszeniaController;
    private WeryfikacjaController weryfikacjaController;
    private UzytkownicyController uzytkownicyController;

    public void initialize() {


    }

    public void graj() throws IOException {
        grajController.resetScene(0,"");
    }

    public void ranking() throws IOException {
        rankingController.resetScene();
    }

    public void wyslane() throws IOException {
        wyslaneController.resetScene(0, User.getId());
    }

    public void przeslij() {
        przeslijController.resetScene();
    }

    public void wyloguj() {
        startController.resetScene();
    }

    public void zgloszenia() throws IOException {
        zgloszeniaController.resetScene();
    }

    public void uzytkownicy() throws IOException {
        uzytkownicyController.resetScene(0, "");
    }

    public void poczta() throws IOException {
        pocztaController.resetScene();

    }

    public void weryfikacja() throws IOException {
        weryfikacjaController.resetScene();
    }


    public void resetScene() throws IOException {
        wiadomosc.setText("");
        Socket socket = new Socket("localhost", 8006);
        JSONObject json = new JSONObject();
        json.put("Akcja", "getMenu");
        json.put("Id", User.getId());
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.write(json.toString());
        writer.newLine();
        writer.flush();
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String odpTekst = reader.readLine();
        JSONObject odp = new JSONObject(odpTekst);
        ilosc.setText(String.valueOf(odp.optInt("Ilosc")));
        socket.close();
        if (odp.optString("Ograniczenie").equals("BAN")) {
            startController.resetScene();
            startController.setBlad("Twoje konto zostalo zbanowane.");
        } else {
            StageClass.getStage().setScene(wiadomosc.getScene());
        }
        String uprawnienie=odp.optString("Uprawnienie");
        if(uprawnienie.equals("MOD"))
        {
            guzik_uzytkownicy.setVisible(false);
            guzik_uzytkownicy.setDisable(true);
            guzik_weryfikacja.setVisible(false);
            guzik_weryfikacja.setDisable(true);
            guzik_zgloszenia.setVisible(true);
            guzik_zgloszenia.setDisable(false);
        }
        else if(uprawnienie.equals("USER"))
        {
            guzik_uzytkownicy.setVisible(false);
            guzik_uzytkownicy.setDisable(true);
            guzik_weryfikacja.setVisible(false);
            guzik_weryfikacja.setDisable(true);
            guzik_zgloszenia.setVisible(false);
            guzik_zgloszenia.setDisable(true);
        }
        else if(uprawnienie.equals("ADMIN"))
        {
            guzik_uzytkownicy.setVisible(true);
            guzik_uzytkownicy.setDisable(false);
            guzik_weryfikacja.setVisible(true);
            guzik_weryfikacja.setDisable(false);
            guzik_zgloszenia.setVisible(true);
            guzik_zgloszenia.setDisable(false);
        }


    }

    public void setControllers(StartController startController, GrajController grajController, RankingController rankingController, WyslaneController wyslaneController, PrzeslijController przeslijController, PocztaController pocztaController, UzytkownicyController uzytkownicyController, ZgloszeniaController zgloszeniaController, WeryfikacjaController weryfikacjaController) {
        this.grajController = grajController;
        this.rankingController = rankingController;
        this.wyslaneController = wyslaneController;
        this.przeslijController = przeslijController;
        this.pocztaController = pocztaController;
        this.uzytkownicyController = uzytkownicyController;
        this.zgloszeniaController = zgloszeniaController;
        this.weryfikacjaController = weryfikacjaController;
        this.startController = startController;
    }


}
