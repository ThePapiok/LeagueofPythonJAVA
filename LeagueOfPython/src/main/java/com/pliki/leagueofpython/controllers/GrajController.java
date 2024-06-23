package com.pliki.leagueofpython.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pliki.leagueofpython.StageClass;
import com.pliki.leagueofpython.User;
import com.pliki.leagueofpython.baza.PocztaClass;
import com.pliki.leagueofpython.baza.ZadaniaClass;
import com.pliki.leagueofpython.baza.ZgloszeniaClass;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class GrajController {

    private MenuController menuController;
    private PoleController poleController;
    @FXML
    private TableView<ZadaniaClass> zadania;
    @FXML
    private TableColumn<ZadaniaClass, Long> id_graj;
    @FXML
    private TableColumn<ZadaniaClass, String> nazwa_graj;
    @FXML
    private TableColumn<ZadaniaClass, Integer> limit_graj;
    @FXML
    private TableColumn<ZadaniaClass, Integer> czas_graj;
    @FXML
    private TableColumn<ZadaniaClass, Integer> podejscia_graj;
    @FXML
    private TableColumn<ZadaniaClass, Integer> ukonczone_graj;
    @FXML
    private TableColumn<ZadaniaClass, LocalDate> data_graj;
    @FXML
    private Label blad;
    @FXML
    private TextField szukaj;
    @FXML
    private Button guzik_usun;
    private ObservableList<ZadaniaClass> observableList;
    private List<ZadaniaClass> listaZadan;
    @FXML
    private TextArea wiadomosc;
    @FXML
    private  Label tekst;


    public void initialize() {
        id_graj.setCellValueFactory(new PropertyValueFactory<>("id_zad"));
        nazwa_graj.setCellValueFactory(new PropertyValueFactory<>("nazwa"));
        limit_graj.setCellValueFactory(new PropertyValueFactory<>("limitCzasu"));
        czas_graj.setCellValueFactory(new PropertyValueFactory<>("czas"));
        podejscia_graj.setCellValueFactory(new PropertyValueFactory<>("podejscia"));
        ukonczone_graj.setCellValueFactory(new PropertyValueFactory<>("ukonczone"));
        data_graj.setCellValueFactory(new PropertyValueFactory<>("data"));

    }

    public void wroc() throws IOException {
        menuController.resetScene();
    }

    public void setControllers(MenuController menuController, PoleController poleController) {
        this.menuController = menuController;
        this.poleController = poleController;
    }

    public void resetScene(int tryb,String nazwa) throws IOException {

        zadania.getItems().clear();
        Socket socket = new Socket("localhost", 8006);
        JSONObject json = new JSONObject();
        json.put("Akcja", "getZadania");
        json.put("Id", User.getId());
        json.put("Tryb",tryb);
        if(tryb==1)
        {
            json.put("Pozycja",Long.parseLong(nazwa));
        }
        else if(tryb==2)
        {
            json.put("Nazwa",nazwa);
        }
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.write(json.toString());
        writer.newLine();
        writer.flush();
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String odp = reader.readLine();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        listaZadan = objectMapper.readValue(odp, new TypeReference<List<ZadaniaClass>>() {
        });
        odp=reader.readLine();
        JSONObject jsonObject=new JSONObject(odp);
        if(jsonObject.optString("Uprawnienie").equals("ADMIN"))
        {
            wiadomosc.setDisable(false);
            wiadomosc.setVisible(true);
            guzik_usun.setVisible(true);
            guzik_usun.setDisable(false);
            tekst.setVisible(true);
        }
        else
        {
            wiadomosc.setDisable(true);
            wiadomosc.setVisible(false);
            guzik_usun.setVisible(false);
            guzik_usun.setDisable(true);
            tekst.setVisible(true);
            tekst.setVisible(false);

        }
        observableList = FXCollections.observableArrayList(listaZadan);
        socket.close();
        zadania.setItems(observableList);
        StageClass.getStage().setScene(zadania.getScene());
    }

    public void zagraj() throws IOException {
        ZadaniaClass selected = zadania.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Long id = selected.getId_zad();
            int index = 0;
            for (ZadaniaClass zadaniaClass : listaZadan) {
                if (zadaniaClass.getId_zad() == id) {
                    poleController.resetScene(index, listaZadan);
                    break;
                }
                index++;
            }


        }

    }

    public void usun() throws IOException {

        ZadaniaClass selected = zadania.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Socket socket = new Socket("localhost", 8006);
            JSONObject json = new JSONObject();
            json.put("Akcja", "usunZadanie");
            json.put("Id_zad", selected.getId_zad());
            json.put("Id_gr",User.getId());
            json.put("Wiadomosc",wiadomosc.getText());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write(json.toString());
            writer.newLine();
            writer.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String odp = reader.readLine();
            JSONObject jsonObject = new JSONObject(odp);
            socket.close();
            if (!jsonObject.optBoolean("Wynik")) {
                blad.setText("Zadanie nie jest juz aktualne.");
            }
            else
            {
                observableList.remove(selected);
                wiadomosc.setText("");

            }
        }
    }

    public void scrollTo(Long id) {
        int index=0;
        int selectedIndex=0;
        for (ZadaniaClass zadaniaClass : observableList) {
            if(zadaniaClass.getId_zad()==id)
            {
                selectedIndex=index;
                break;
            }
            index++;
        }
        zadania.scrollTo(selectedIndex);
        zadania.getSelectionModel().select(selectedIndex);
    }

    public void szukajguzik() throws IOException {
        int tryb=0;
        String nazwa="";
        try {
            Long pozycja=Long.parseLong(szukaj.getText());
            tryb=1;
            nazwa=String.valueOf(pozycja);
        }
        catch (NumberFormatException e)
        {
            if(!(szukaj.getText().length() ==0))
            {
                tryb=2;
                nazwa=szukaj.getText();
            }
        }
        resetScene(tryb,nazwa);
    }


}
