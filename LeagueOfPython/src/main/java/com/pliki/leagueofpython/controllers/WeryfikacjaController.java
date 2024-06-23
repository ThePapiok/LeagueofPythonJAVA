package com.pliki.leagueofpython.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pliki.leagueofpython.Scena;
import com.pliki.leagueofpython.StageClass;
import com.pliki.leagueofpython.User;
import com.pliki.leagueofpython.baza.WeryfikacjaClass;
import com.pliki.leagueofpython.baza.ZgloszeniaClass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.util.List;

public class WeryfikacjaController {
    @FXML
    private TableView<WeryfikacjaClass> lista;
    @FXML
    private TableColumn<WeryfikacjaClass,Long> id_mod_lista;
    @FXML
    private TableColumn<WeryfikacjaClass,String> nazwa_mod_lista;
    @FXML
    private TableColumn<WeryfikacjaClass,Long> id_gr_lista;
    @FXML
    private TableColumn<WeryfikacjaClass,String> nazwa_gr_lista;
    @FXML
    private TableColumn<WeryfikacjaClass,String> nazwa_zad_lista;
    @FXML
    private TableColumn<WeryfikacjaClass,String> akcja_lista;
    @FXML
    private TableColumn<WeryfikacjaClass, LocalDate> data_lista;
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
    @FXML
    private TextArea wiadomoscgracz;
    @FXML
    private TextArea wiadomoscmod;
    @FXML
    private Label blad;
    private MenuController menuController;
    private WeryfikacjaSzczegolyController weryfikacjaSzczegolyController;
    private List<WeryfikacjaClass> listaWeryfikacji;
    private ObservableList<WeryfikacjaClass> observableList;


    public void initialize() {
        id_mod_lista.setCellValueFactory(new PropertyValueFactory<>("id_mod"));
        nazwa_mod_lista.setCellValueFactory(new PropertyValueFactory<>("nick_mod"));
        id_gr_lista.setCellValueFactory(new PropertyValueFactory<>("id_uz"));
        nazwa_gr_lista.setCellValueFactory(new PropertyValueFactory<>("nick_uz"));
        nazwa_zad_lista.setCellValueFactory(new PropertyValueFactory<>("nazwa"));
        akcja_lista.setCellValueFactory(new PropertyValueFactory<>("akcja"));
        data_lista.setCellValueFactory(new PropertyValueFactory<>("data"));
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

    public void wroc() throws IOException {
        menuController.resetScene();
    }


    public void setControllers(MenuController menuController,WeryfikacjaSzczegolyController weryfikacjaSzczegolyController)
    {
        this.menuController=menuController;
        this.weryfikacjaSzczegolyController=weryfikacjaSzczegolyController;
    }

    public void resetScene() throws IOException {
        Socket socket = new Socket("localhost", 8006);
        JSONObject json = new JSONObject();
        json.put("Akcja", "getWeryfikacja");
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.write(json.toString());
        writer.newLine();
        writer.flush();
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String odp = reader.readLine();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        listaWeryfikacji = objectMapper.readValue(odp, new TypeReference<List<WeryfikacjaClass>>() {});
        observableList = FXCollections.observableArrayList(listaWeryfikacji);
        lista.setItems(observableList);
        socket.close();
        blad.setText("");
        pomingracz.setSelected(true);
        pominzadanie.setSelected(true);
        odbierzmod.setSelected(false);
        zbanujmod.setSelected(false);
        wiadomoscgracz.setText("");
        wiadomoscmod.setText("");
        StageClass.getStage().setScene(lista.getScene());
    }

    public void sprawdz() throws IOException {
        WeryfikacjaClass weryfikacjaClass = lista.getSelectionModel().getSelectedItem();
        if (weryfikacjaClass != null) {
            Long id = weryfikacjaClass.getId();
            Socket socket = new Socket("localhost", 8006);
            JSONObject json = new JSONObject();
            json.put("Akcja", "isWeryfikacja");
            json.put("Id", id);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write(json.toString());
            writer.newLine();
            writer.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String odp = reader.readLine();
            JSONObject jsonObject = new JSONObject(odp);
            socket.close();
            if (!jsonObject.optBoolean("Wynik")) {
                observableList.remove(weryfikacjaClass);
                blad.setText("Weryfikacja nie jest juz aktualna.");

            } else {
                int index = 0;
                for (WeryfikacjaClass weryfikacjaClass1 : listaWeryfikacji) {
                    if (weryfikacjaClass1.getId() == id) {
                        weryfikacjaSzczegolyController.resetScene(index, listaWeryfikacji);
                        break;
                    }
                    index++;
                }
            }
        }
    }

    public void wyslij() throws IOException {
        WeryfikacjaClass weryfikacjaClass = lista.getSelectionModel().getSelectedItem();
        if (weryfikacjaClass != null) {
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
                    observableList.remove(weryfikacjaClass);

                } else {
                    if (jsonObject.optBoolean("Uprawnienia")) {
                        observableList.remove(weryfikacjaClass);
                        blad.setText("Juz to nie istnieje.");
                    } else {
                        blad.setText("Nie masz uprawnien.");
                    }
                }
                pomingracz.setSelected(true);
                pominzadanie.setSelected(true);
                odbierzmod.setSelected(false);
                zbanujmod.setSelected(false);
                wiadomoscgracz.setText("");
                wiadomoscmod.setText("");
                socket.close();
            }
        }
    }

    public void setError(String string)
    {
        blad.setText(string);
    }
}
