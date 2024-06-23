package com.pliki.leagueofpython.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pliki.leagueofpython.Scena;
import com.pliki.leagueofpython.StageClass;
import com.pliki.leagueofpython.User;
import com.pliki.leagueofpython.baza.ZadaniaClass;
import com.pliki.leagueofpython.baza.ZgloszeniaClass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import org.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class ZgloszeniaController {
    @FXML
    private TableView<ZgloszeniaClass> zgloszenia;

    @FXML
    private TableColumn<ZgloszeniaClass, Long> id_zgloszenia;

    @FXML
    private TableColumn<ZgloszeniaClass, String> nazwa_zgloszenia;

    @FXML
    private TableColumn<ZgloszeniaClass, Long> id_g_zgloszenia;
    @FXML
    private TableColumn<ZgloszeniaClass, String> nazwa_g_zgloszenia;

    @FXML
    private TableColumn<ZgloszeniaClass, Integer> czas_zgloszenia;

    @FXML
    private TableColumn<ZgloszeniaClass, Integer> limit_zgloszenia;

    @FXML
    private TableColumn<ZgloszeniaClass, LocalDate> data_zgloszenia;

    @FXML
    private Label error;
    @FXML
    private TextArea wiadomosc;
    private MenuController menuController;
    private ZgloszenieController zgloszenieController;
    private List<ZgloszeniaClass> listaZgloszen;
    private ObservableList<ZgloszeniaClass> observableList;

    public void wroc() throws IOException {
        menuController.resetScene();
    }

    public void initialize() {
        id_zgloszenia.setCellValueFactory(new PropertyValueFactory<>("Id"));
        nazwa_zgloszenia.setCellValueFactory(new PropertyValueFactory<>("nazwa"));
        id_g_zgloszenia.setCellValueFactory(new PropertyValueFactory<>("Id_g"));
        nazwa_g_zgloszenia.setCellValueFactory(new PropertyValueFactory<>("nazwa_g"));
        czas_zgloszenia.setCellValueFactory(new PropertyValueFactory<>("czas"));
        limit_zgloszenia.setCellValueFactory(new PropertyValueFactory<>("limit"));
        data_zgloszenia.setCellValueFactory(new PropertyValueFactory<>("data"));
    }


    public void setControllers(MenuController menuController, ZgloszenieController zgloszenieController) {
        this.menuController = menuController;
        this.zgloszenieController = zgloszenieController;
    }

    public void resetScene() throws IOException {
        Socket socket = new Socket("localhost", 8006);
        JSONObject json = new JSONObject();
        json.put("Akcja", "getZgloszenia");
        json.put("Id",User.getId());
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.write(json.toString());
        writer.newLine();
        writer.flush();
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String odp = reader.readLine();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        listaZgloszen = objectMapper.readValue(odp, new TypeReference<List<ZgloszeniaClass>>() {
        });
        observableList = FXCollections.observableArrayList(listaZgloszen);
        zgloszenia.setItems(observableList);
        socket.close();
        error.setText("");
        StageClass.getStage().setScene(zgloszenia.getScene());
    }

    public void sprawdz() throws IOException {
        ZgloszeniaClass zgloszeniaClass = zgloszenia.getSelectionModel().getSelectedItem();
        if (zgloszeniaClass != null) {
            Long id = zgloszeniaClass.getId();
            Socket socket = new Socket("localhost", 8006);
            JSONObject json = new JSONObject();
            json.put("Akcja", "isZgloszenie");
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
                observableList.remove(zgloszeniaClass);
                error.setText("Zgloszenie nie jest juz aktualne.");

            } else {
                int index = 0;
                for (ZgloszeniaClass zgloszeniaClass1 : listaZgloszen) {
                    if (zgloszeniaClass1.getId() == id) {
                        zgloszenieController.resetScene(index, listaZgloszen);
                        break;
                    }
                    index++;
                }
            }
        }

    }

    public void zatwierdz() throws IOException {
        ZgloszeniaClass zgloszeniaClass = zgloszenia.getSelectionModel().getSelectedItem();
        if (zgloszeniaClass != null) {
            sendObject("addZadanie",zgloszeniaClass);
        }
    }

    public void odrzuc() throws IOException {
        ZgloszeniaClass zgloszeniaClass = zgloszenia.getSelectionModel().getSelectedItem();
        if (zgloszeniaClass != null) {
            sendObject("rejectZadanie",zgloszeniaClass);
        }
    }

    public void blokuj() throws IOException {
        ZgloszeniaClass zgloszeniaClass = zgloszenia.getSelectionModel().getSelectedItem();
        if (zgloszeniaClass != null) {
            sendObject("blockZadanie",zgloszeniaClass);

        }
    }


    public void sendObject(String akcja,ZgloszeniaClass zgloszeniaClass) throws IOException {
        Socket socket = new Socket("localhost", 8006);
        JSONObject json = new JSONObject();
        json.put("Akcja", akcja);
        json.put("Id", User.getId());
        json.put("Id_g", zgloszeniaClass.getId_g());
        json.put("Wiadomosc", wiadomosc.getText());
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.write(json.toString());
        writer.newLine();
        writer.flush();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String object = mapper.writeValueAsString(zgloszeniaClass);
        writer.write(object);
        writer.newLine();
        writer.flush();
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String odpTekst = reader.readLine();
        JSONObject odp = new JSONObject(odpTekst);
        if (odp.optBoolean("Wynik")) {
            observableList.remove(zgloszeniaClass);
            wiadomosc.setText("");
        } else {
            if (odp.optBoolean("Uprawnienia")) {
                observableList.remove(zgloszeniaClass);
                error.setText("Poprzednie zgloszenie bylo nieaktualne");
            } else {
                error.setText("Nie masz wystarczajacych uprawnien");
            }
        }
        socket.close();
    }


    public void setError(String error) {
        this.error.setText(error);
    }
}
