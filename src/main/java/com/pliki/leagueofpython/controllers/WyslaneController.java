package com.pliki.leagueofpython.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pliki.leagueofpython.Scena;
import com.pliki.leagueofpython.StageClass;
import com.pliki.leagueofpython.User;
import com.pliki.leagueofpython.baza.RankingClass;
import com.pliki.leagueofpython.baza.WyslaneClass;
import com.pliki.leagueofpython.baza.ZadaniaClass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.util.List;

public class WyslaneController {
    @FXML
    private TableView<WyslaneClass> zadania;
    @FXML
    private TableColumn<WyslaneClass, Long> id_zadania;
    @FXML
    private TableColumn<WyslaneClass, String> nazwa_zadania;
    @FXML
    private TableColumn<WyslaneClass, Boolean> zrobione_zadania;
    @FXML
    private TableColumn<WyslaneClass, Boolean> publiczny_zadania;
    @FXML
    private TableColumn<WyslaneClass, Integer> wynik_zadania;
    @FXML
    private TableColumn<WyslaneClass, Integer> proby_zadania;
    @FXML
    private TableColumn<WyslaneClass, Float> czas_zadania;
    @FXML
    private TableColumn<WyslaneClass, Float> uruchomienie_zadania;
    @FXML
    private TableColumn<WyslaneClass, LocalDate> data_zadania;
    @FXML
    private Button guzik_prywatny;
    @FXML
    private Button guzik_publiczny;
    @FXML
    private Button guzik_zagraj;
    @FXML
    private Label blad;
    private ObservableList<WyslaneClass> observableList;
    private List<WyslaneClass> listaWyslane;

    private MenuController menuController;
    private KodController kodController;
    private RankingController rankingController;
    private GrajController grajController;
    private int tryb;
    private Long id;

    public void initialize() {
        id_zadania.setCellValueFactory(new PropertyValueFactory<>("id_zad"));
        nazwa_zadania.setCellValueFactory(new PropertyValueFactory<>("nazwa"));
        zrobione_zadania.setCellValueFactory(new PropertyValueFactory<>("zrobione"));
        publiczny_zadania.setCellValueFactory(new PropertyValueFactory<>("publiczny"));
        wynik_zadania.setCellValueFactory(new PropertyValueFactory<>("wynik"));
        proby_zadania.setCellValueFactory(new PropertyValueFactory<>("proby"));
        czas_zadania.setCellValueFactory(new PropertyValueFactory<>("czas"));
        uruchomienie_zadania.setCellValueFactory(new PropertyValueFactory<>("czasUruchomienia"));
        data_zadania.setCellValueFactory(new PropertyValueFactory<>("data"));
    }

    public void wroc() throws IOException {
        if (tryb == 0) {
            menuController.resetScene();
        } else {
            rankingController.resetScene();
        }
    }


    public void setControllers(MenuController menuController, KodController kodController, RankingController rankingController, GrajController grajController) {
        this.menuController = menuController;
        this.kodController = kodController;
        this.rankingController = rankingController;
        this.grajController = grajController;
    }

    public void resetScene(int tryb, Long id) throws IOException {
        this.tryb = tryb;
        this.id = id;
        zadania.getItems().clear();
        Socket socket = new Socket("localhost", 8006);
        JSONObject json = new JSONObject();
        json.put("Akcja", "getWyslane");
        if (tryb == 0||id==User.getId()) {
            json.put("Moje", true);
        } else {
            json.put("Moje", false);
        }
        json.put("Id", id);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.write(json.toString());
        writer.newLine();
        writer.flush();
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String odp = reader.readLine();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        listaWyslane = objectMapper.readValue(odp, new TypeReference<List<WyslaneClass>>() {
        });
        observableList = FXCollections.observableArrayList(listaWyslane);
        socket.close();
        zadania.setItems(observableList);
        blad.setText("");
        if (tryb == 0||id==User.getId()) {
            guzik_prywatny.setVisible(true);
            guzik_prywatny.setDisable(false);
            guzik_publiczny.setVisible(true);
            guzik_publiczny.setDisable(false);
            guzik_zagraj.setVisible(true);
            guzik_zagraj.setDisable(false);
        } else {
            guzik_prywatny.setVisible(false);
            guzik_prywatny.setDisable(true);
            guzik_publiczny.setVisible(false);
            guzik_publiczny.setDisable(true);
            guzik_zagraj.setVisible(false);
            guzik_zagraj.setDisable(true);
        }
        StageClass.getStage().setScene(zadania.getScene());
    }

    public void publiczny() throws IOException {
        changeWidocznosc(true);
    }

    public void prywatny() throws IOException {
        changeWidocznosc(false);
    }

    public void changeWidocznosc(Boolean publiczny) throws IOException {
        WyslaneClass selected = zadania.getSelectionModel().getSelectedItem();
        boolean jest = true;
        if (selected.isPubliczny() && publiczny) {
            jest = false;
        } else if (!selected.isPubliczny() && !publiczny) {
            jest = false;
        }
        if (jest) {
            if (selected != null) {
                Socket socket = new Socket("localhost", 8006);
                JSONObject json = new JSONObject();
                json.put("Akcja", "changeWidocznosc");
                json.put("Publiczny", publiczny);
                json.put("Id_gr", User.getId());
                json.put("Id_wys", selected.getId_wys());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                writer.write(json.toString());
                writer.newLine();
                writer.flush();
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String odp = reader.readLine();
                JSONObject jsonObject = new JSONObject(odp);
                if (jsonObject.optBoolean("Wynik")) {
                    int index = observableList.indexOf(selected);
                    if (publiczny) {
                        selected.setPubliczny(true);
                    } else {
                        selected.setPubliczny(false);
                    }
                    observableList.set(index, selected);
                } else {
                    blad.setText("Nie mozesz tego zrobic.");
                }
                socket.close();
            }
        }
    }

    public void zagraj() throws IOException {
        WyslaneClass selected = zadania.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (selected.isZrobione()) {
                blad.setText("Juz robiles to zadanie.");
            } else {
                grajController.resetScene(0,"");
                grajController.scrollTo(selected.getId_zad());
            }
        }
    }

    public void sprawdz() {
        WyslaneClass selected = zadania.getSelectionModel().getSelectedItem();
        if (selected != null) {
            kodController.resetScene(selected, tryb, id);
        }
    }
}
