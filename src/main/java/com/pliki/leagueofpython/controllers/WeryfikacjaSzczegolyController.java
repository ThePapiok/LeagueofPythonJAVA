package com.pliki.leagueofpython.controllers;

import com.pliki.leagueofpython.Scena;
import com.pliki.leagueofpython.StageClass;
import com.pliki.leagueofpython.User;
import com.pliki.leagueofpython.baza.WeryfikacjaClass;
import com.pliki.leagueofpython.baza.WyslaneClass;
import com.pliki.leagueofpython.baza.ZgloszeniaClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class WeryfikacjaSzczegolyController {
    @FXML
    private TextArea wiadomoscgracz;
    @FXML
    private Label nick;
    @FXML
    private Label moderator;
    @FXML
    private Label blad;
    @FXML
    private TextField tytul;
    @FXML
    private TextArea tresc;
    @FXML
    private TextArea wiadomoscodmod;
    @FXML
    private TextField czas;
    @FXML
    private TextField limit;
    @FXML
    private TextField zmienne;
    @FXML
    private Label akcja;
    @FXML
    private ListView input;
    @FXML
    private ListView output;
    @FXML
    private TextArea wiadomoscmod;
    @FXML
    private RadioButton pomingracz;
    @FXML
    private RadioButton odblokujgracza;
    @FXML
    private RadioButton zablokujgracza;
    @FXML
    private RadioButton zbanujgracza;
    @FXML
    private RadioButton pominzadanie;
    @FXML
    private RadioButton usunzadanie;
    @FXML
    private RadioButton przywroczadanie;
    @FXML
    private CheckBox odbierzmod;
    @FXML
    private CheckBox zbanujmod;

    public void initialize() {
        ToggleGroup toggleGroup1 = new ToggleGroup();
        pomingracz.setToggleGroup(toggleGroup1);
        odblokujgracza.setToggleGroup(toggleGroup1);
        zablokujgracza.setToggleGroup(toggleGroup1);
        zbanujgracza.setToggleGroup(toggleGroup1);
        ToggleGroup toggleGroup2 = new ToggleGroup();
        pominzadanie.setToggleGroup(toggleGroup2);
        usunzadanie.setToggleGroup(toggleGroup2);
        przywroczadanie.setToggleGroup(toggleGroup2);
    }


    private int index;
    private List<WeryfikacjaClass> listaWeryfikacji;
    private WeryfikacjaClass weryfikacjaClass;
    private WeryfikacjaController weryfikacjaController;

    public void wroc() throws IOException {
        weryfikacjaController.resetScene();
    }


    public void setController(WeryfikacjaController weryfikacjaController) {
        this.weryfikacjaController = weryfikacjaController;
    }

    public void resetScene(int index, List<WeryfikacjaClass> listaWeryfikacji) {

        this.index = index;
        this.listaWeryfikacji = listaWeryfikacji;
        weryfikacjaClass = listaWeryfikacji.get(index);
        pomingracz.setSelected(true);
        pominzadanie.setSelected(true);
        odbierzmod.setSelected(false);
        zbanujmod.setSelected(false);
        wiadomoscgracz.setText("");
        wiadomoscmod.setText("");
        blad.setText("");
        tytul.setText(weryfikacjaClass.getNazwa());
        tresc.setText(weryfikacjaClass.getOpis());
        nick.setText(weryfikacjaClass.getNick_uz());
        moderator.setText(weryfikacjaClass.getNick_mod());
        wiadomoscodmod.setText(weryfikacjaClass.getWiadomosc());
        zmienne.setText(weryfikacjaClass.getZmienne());
        akcja.setText(weryfikacjaClass.getAkcja());
        input.getItems().clear();
        input.getItems().addAll(weryfikacjaClass.getInput());
        output.getItems().clear();
        output.getItems().addAll(weryfikacjaClass.getOutput());
        limit.setText(String.valueOf(weryfikacjaClass.getLimitCzasu()));
        czas.setText(String.valueOf(weryfikacjaClass.getCzas()));
        StageClass.getStage().setScene(wiadomoscgracz.getScene());
    }

    public void wyslij() throws IOException {
        boolean jest = false;
        if (weryfikacjaClass.getAkcja().equals("ZATWIERDZONO") && przywroczadanie.isSelected()) {
            jest = true;
        }
        if (weryfikacjaClass.getAkcja().equals("ODRZUCONO") && usunzadanie.isSelected()) {
            jest = true;
        }
        if (!jest) {
            Socket socket = new Socket("localhost", 8006);
            JSONObject json = new JSONObject();
            json.put("Akcja", "changeWeryfikacja");
            json.put("Id", weryfikacjaClass.getId());
            json.put("Id_gr", weryfikacjaClass.getId_uz());
            json.put("Id_mod", weryfikacjaClass.getId_mod());
            json.put("Id_ad", User.getId());
            json.put("Wiadomosc_gr", wiadomoscgracz.getText());
            json.put("Wiadomosc_mod", wiadomoscmod.getText());
            if (pomingracz.isSelected()) {
                json.put("Gracz", 0);
            } else if (odblokujgracza.isSelected()) {
                json.put("Gracz", 1);
            } else if (zablokujgracza.isSelected()) {
                json.put("Gracz", 2);
            } else if (zbanujgracza.isSelected()) {
                json.put("Gracz", 3);
            }
            if (pominzadanie.isSelected()) {
                json.put("Zadanie", 0);
            } else if (usunzadanie.isSelected()) {
                json.put("Zadanie", 1);
            } else if (przywroczadanie.isSelected()) {
                json.put("Zadanie", 2);
            }
            if (odbierzmod.isSelected()) {
                json.put("Odbierz", true);
            } else {
                json.put("Odbierz", false);
            }
            if (zbanujmod.isSelected()) {
                json.put("Zbanuj", true);
            } else {
                json.put("Zbanuj", false);
            }
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write(json.toString());
            writer.newLine();
            writer.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String odp = reader.readLine();
            JSONObject jsonObject = new JSONObject(odp);
            if (jsonObject.optBoolean("Wynik")) {
                listaWeryfikacji.remove(index);
                if(listaWeryfikacji.size()==0)
                {
                    weryfikacjaController.resetScene();
                }
                else {
                    if (index > listaWeryfikacji.size() - 1) {
                        index = 0;
                    }
                    resetScene(index, listaWeryfikacji);
                }
            } else {
                if (jsonObject.optBoolean("Uprawnienia")) {
                    listaWeryfikacji.remove(index);
                    if(listaWeryfikacji.size()==0)
                    {
                        weryfikacjaController.resetScene();
                        weryfikacjaController.setError("Poprzednia weryfikacja byla nieaktualna");
                    }
                    else {
                        if (index > listaWeryfikacji.size() - 1) {
                            index = 0;
                        }
                        resetScene(index, listaWeryfikacji);
                        blad.setText("Poprzednie weryfikacja byla nieaktualna");
                    }
                } else {
                    blad.setText("Nie masz uprawnien.");
                }
            }
            socket.close();
        }

    }
}
