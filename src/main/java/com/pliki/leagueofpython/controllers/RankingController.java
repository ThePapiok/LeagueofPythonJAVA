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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class RankingController {
    @FXML
    private TextField szukaj;
    @FXML
    private TableView<RankingClass> gracze;
    @FXML
    private TableColumn<RankingClass,Long> id_gracze;
    @FXML
    private TableColumn<RankingClass,String> nazwa_gracze;
    @FXML
    private TableColumn<RankingClass,Integer> wynik_gracze;
    @FXML
    private TableColumn<RankingClass,Integer> roz_gracze;
    @FXML
    private TableColumn<RankingClass,Integer> prze_gracze;
    @FXML
    private TableColumn<RankingClass,Integer> prob_gracze;
    @FXML
    private TableColumn<RankingClass,Float> czas_gracze;
    private ObservableList<RankingClass> observableList;
    private List<RankingClass> listaRanking;
    private MenuController menuController;
    private WyslaneController wyslaneController;

    public void wroc() throws IOException {
        menuController.resetScene();
    }

    public void initialize() {
        id_gracze.setCellValueFactory(new PropertyValueFactory<>("id_uz"));
        nazwa_gracze.setCellValueFactory(new PropertyValueFactory<>("nick"));
        wynik_gracze.setCellValueFactory(new PropertyValueFactory<>("wynik"));
        roz_gracze.setCellValueFactory(new PropertyValueFactory<>("rozwiazane"));
        prze_gracze.setCellValueFactory(new PropertyValueFactory<>("przeslane"));
        prob_gracze.setCellValueFactory(new PropertyValueFactory<>("proby"));
        czas_gracze.setCellValueFactory(new PropertyValueFactory<>("czas"));
    }


    public void setControllers(MenuController menuController,WyslaneController wyslaneController)
    {
        this.menuController=menuController;
        this.wyslaneController=wyslaneController;
    }

    public void resetScene() throws IOException {

        szukaj.setText("");
        szukajRanking(0,0,"");
        StageClass.getStage().setScene(szukaj.getScene());

    }

    public void sprawdz() throws IOException {
        RankingClass selected = gracze.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Long id = selected.getId_uz();
            wyslaneController.resetScene(1,id);
        }

    }

    public void szukajWynik() throws IOException {

        try {
            Integer pozycja=Integer.parseInt(szukaj.getText());
            szukajRanking(1,pozycja,"");

        }
        catch (NumberFormatException e)
        {
            if(szukaj.getText().length()==0)
            {
                szukajRanking(0,0,"");
            }
            else {
                String nick = szukaj.getText();
                szukajRanking(2, 0, nick);
            }
        }
    }

    public void szukajRanking(int tryb,long pozycja,String nick) throws IOException {
        gracze.getItems().clear();
        Socket socket = new Socket("localhost", 8006);
        JSONObject json = new JSONObject();
        json.put("Akcja","getRanking");
        json.put("Tryb",tryb);
        if(tryb==1)
        {
            json.put("Pozycja",pozycja);
        }
        else if(tryb==2)
        {
            json.put("Nick",nick);
        }
        json.put("Id",User.getId());
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.write(json.toString());
        writer.newLine();
        writer.flush();
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String odp = reader.readLine();
        JSONObject jsonObject=new JSONObject(odp);
        RankingClass rankingClass=null;
        odp=reader.readLine();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        listaRanking = objectMapper.readValue(odp, new TypeReference<List<RankingClass>>() {});
        observableList = FXCollections.observableArrayList(listaRanking);
        socket.close();
        gracze.setItems(observableList);
        if(jsonObject.optBoolean("Istnieje")) {
            int selectedIndex = jsonObject.getInt("Pozycja");
            gracze.scrollTo(selectedIndex);
            gracze.getSelectionModel().select(selectedIndex);
        }
    }
}
