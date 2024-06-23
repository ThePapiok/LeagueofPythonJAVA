package com.pliki.leagueofpython.controllers;

import com.pliki.leagueofpython.Scena;
import com.pliki.leagueofpython.StageClass;
import com.pliki.leagueofpython.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.time.Instant;

public class PrzeslijController {
    @FXML
    private Label wiadomosc;
    @FXML
    private TextField limitCzasu;
    @FXML
    private TextField czas;
    @FXML
    private TextField tytul;
    @FXML
    private TextField poleInput;
    @FXML
    private TextField poleOutput;
    @FXML
    private TextArea opis;
    @FXML
    private ListView input;
    @FXML
    private ListView output;
    @FXML
    private TextField zmienne;

    private MenuController menuController;

    public void wroc() throws IOException {
        menuController.resetScene();
    }

    public void setController(MenuController menuController) {
        this.menuController = menuController;
    }

    public void dodajInput() {
        if (poleInput.getText().length() != 0) {
            input.getItems().add(poleInput.getText());
            poleInput.setText("");
        }
    }

    public void usunInput() {
        String selectedItem = (String) input.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            input.getItems().remove(selectedItem);
        }
    }

    public void dodajOutput() {
        if (poleOutput.getText().length() != 0) {
            output.getItems().add(poleOutput.getText());
            poleOutput.setText("");
        }
    }

    public void usunOutput() {
        String selectedItem = (String) output.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            output.getItems().remove(selectedItem);
        }
    }

    public void wyslij() throws IOException {
        boolean poprawny = true;
        if (limitCzasu.getText().length() == 0) {
            wiadomosc.setText("Pole Limit czasu nie moze byc puste.");
            poprawny = false;
        } else if (czas.getText().length() == 0) {
            wiadomosc.setText("Pole Punktowany czas nie moze byc puste.");
            poprawny = false;
        } else if (tytul.getText().length() == 0) {
            wiadomosc.setText("Pole Tytul nie moze byc puste.");
            poprawny = false;
        } else if (opis.getText().length() == 0) {
            wiadomosc.setText("Pole Opis nie moze byc puste.");
            poprawny = false;
        } else if (input.getItems().isEmpty() && zmienne.getText().length() != 0) {
            wiadomosc.setText("Pole Input nie moze byc puste.");
            poprawny = false;
        } else if (!input.getItems().isEmpty() && zmienne.getText().length() == 0) {
            wiadomosc.setText("Pole Zmienne nie moze byc puste.");
            poprawny = false;
        } else if (output.getItems().isEmpty()) {
            wiadomosc.setText("Pole Output nie moze byc puste.");
            poprawny = false;
        } else if (tytul.getText().contains(" ")) {
            wiadomosc.setText("W polu tytul nie moze byc spacji");
            poprawny = false;
        }
        else if(tytul.getText().length()>=30)
        {
            wiadomosc.setText("Tytul nie moze byc taki dlugi");
            poprawny=false;
        }
        try {
            String firstchar = String.valueOf(tytul.getText().charAt(0));
            Integer.parseInt(firstchar);
            wiadomosc.setText("Pierwszy znak tytuly nie moze byc liczba.");
            poprawny = false;
        } catch (NumberFormatException e) {

        }
        try {
            Integer.parseInt(czas.getText());
        } catch (NumberFormatException e) {
            wiadomosc.setText("Pole Punktowany czas musi byc typu int.");
            poprawny = false;
        }
        try {
            Integer.parseInt(limitCzasu.getText());
        } catch (NumberFormatException e) {
            wiadomosc.setText("Pole Limit czasu musi byc typu int.");
            poprawny = false;
        }
        if (poprawny) {
            Socket socket = new Socket("localhost", 8006);
            JSONObject json = new JSONObject();
            json.put("Akcja", "Przeslij");
            json.put("Id", User.getId());
            json.put("Tytul", tytul.getText());
            json.put("Opis", opis.getText());
            json.put("Limit", limitCzasu.getText());
            json.put("Czas", czas.getText());
            json.put("Zmienne", zmienne.getText());
            JSONArray tablicaInput = new JSONArray();
            input.getItems().forEach(element -> {
                tablicaInput.put(element);
            });
            json.put("Input", tablicaInput);
            JSONArray tablicaOutput = new JSONArray();
            output.getItems().forEach(element -> {
                tablicaOutput.put(element);
            });
            json.put("Output", tablicaOutput);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write(json.toString());
            writer.newLine();
            writer.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String odpTekst = reader.readLine();
            JSONObject odp = new JSONObject(odpTekst);
            if (odp.optBoolean("Wynik")) {
                resetScene();
            }
            wiadomosc.setText(odp.optString("Wiadomosc"));
            socket.close();
        }
    }

    public void resetScene() {
        tytul.setText("");
        opis.setText("");
        limitCzasu.setText("");
        czas.setText("");
        poleOutput.setText("");
        poleInput.setText("");
        wiadomosc.setText("");
        zmienne.setText("");
        input.getItems().clear();
        output.getItems().clear();
        StageClass.getStage().setScene(wiadomosc.getScene());
    }
}
