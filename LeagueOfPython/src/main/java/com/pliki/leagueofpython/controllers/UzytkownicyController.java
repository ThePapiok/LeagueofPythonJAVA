package com.pliki.leagueofpython.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pliki.leagueofpython.Scena;
import com.pliki.leagueofpython.StageClass;
import com.pliki.leagueofpython.User;
import com.pliki.leagueofpython.baza.UzytkownicyClass;
import com.pliki.leagueofpython.baza.ZadaniaClass;
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

public class UzytkownicyController {
    @FXML
    private TextField szukaj;
    @FXML
    private TableView<UzytkownicyClass> gracze;
    @FXML
    private TableColumn<UzytkownicyClass, Long> id_gracze;
    @FXML
    private TableColumn<UzytkownicyClass, String> nazwa_gracze;
    @FXML
    private TableColumn<UzytkownicyClass, String> ograniczenia_gracze;
    @FXML
    private TableColumn<UzytkownicyClass, String> uprawnienia_gracze;
    @FXML
    private TableColumn<UzytkownicyClass, LocalDate> data_gracze;
    @FXML
    private Label blad;
    @FXML
    private TextArea wiadomosc;
    private MenuController menuController;
    private ObservableList<UzytkownicyClass> observableList;
    private List<UzytkownicyClass> listaUzytkownikow;

    public void initialize() {
        id_gracze.setCellValueFactory(new PropertyValueFactory<>("id_uz"));
        nazwa_gracze.setCellValueFactory(new PropertyValueFactory<>("nick"));
        ograniczenia_gracze.setCellValueFactory(new PropertyValueFactory<>("ograniczenie"));
        uprawnienia_gracze.setCellValueFactory(new PropertyValueFactory<>("uprawnienie"));
        data_gracze.setCellValueFactory(new PropertyValueFactory<>("data"));
    }

    public void wroc() throws IOException {
        menuController.resetScene();
    }


    public void setController(MenuController menuController) {
        this.menuController = menuController;
    }

    public void resetScene(int tryb,String nick) throws IOException {
        blad.setText("");
        wiadomosc.setText("");
        gracze.getItems().clear();
        Socket socket = new Socket("localhost", 8006);
        JSONObject json = new JSONObject();
        json.put("Akcja", "getUzytkownicy");
        json.put("Tryb",tryb);
        if(tryb==2)
        {
            json.put("Nick",nick);
        }
        else if(tryb==1)
        {
            json.put("Pozycja",Long.parseLong(nick));
        }
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.write(json.toString());
        writer.newLine();
        writer.flush();
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String odp = reader.readLine();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        listaUzytkownikow = objectMapper.readValue(odp, new TypeReference<List<UzytkownicyClass>>() {
        });
        observableList = FXCollections.observableArrayList(listaUzytkownikow);
        socket.close();
        gracze.setItems(observableList);
        StageClass.getStage().setScene(szukaj.getScene());
    }

    public void zbanuj() throws IOException {
        changeUzytkownik("zbanuj");
    }

    public void odbanuj() throws IOException {
        changeUzytkownik("odbanuj");
    }

    public void zablokuj() throws IOException {
        changeUzytkownik("zablokuj");
    }

    public void odblokuj() throws IOException {
        changeUzytkownik("odblokuj");
    }

    public void odbierzMod() throws IOException {
        changeUzytkownik("odbierzMod");

    }

    public void dajMod() throws IOException {
        changeUzytkownik("dajMod");

    }

    public void szukaj() throws IOException {
        int tryb=0;
        String nick="";
        try {
            Long pozycja=Long.parseLong(szukaj.getText());
            tryb=1;
            nick=String.valueOf(pozycja);
        }
        catch (NumberFormatException e)
        {
            if(!(szukaj.getText().length() ==0))
            {
                tryb=2;
                nick=szukaj.getText();
            }
        }
        resetScene(tryb,nick);
    }

    public void changeUzytkownik(String akcja) throws IOException {
        UzytkownicyClass selected = gracze.getSelectionModel().getSelectedItem();
        boolean jest = false;
        if (selected != null) {
            if (akcja.equals("zbanuj") && selected.getOgraniczenie().equals("BAN")) {
                jest = true;
            } else if (akcja.equals("odbanuj") && !selected.getOgraniczenie().equals("BAN")) {
                jest = true;
            } else if (akcja.equals("zablokuj") && selected.getOgraniczenie().equals("BLOCK")) {
                jest = true;
            } else if (akcja.equals("odblokuj") && !selected.getOgraniczenie().equals("BLOCK")) {
                jest = true;
            } else if (akcja.equals("dajMod") && selected.getUprawnienie().equals("MOD")) {
                jest = true;
            } else if (akcja.equals("odbierzMod") && !selected.getUprawnienie().equals("MOD")) {
                jest = true;
            }
            if (!jest) {
                wiadomosc.setText("");
                Socket socket = new Socket("localhost", 8006);
                JSONObject json = new JSONObject();
                json.put("Akcja", "changeUzytkownik");
                json.put("Typ", akcja);
                json.put("Id_ad", User.getId());
                json.put("Id_gr", selected.getId_uz());
                json.put("Wiadomosc", wiadomosc.getText());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                writer.write(json.toString());
                writer.newLine();
                writer.flush();
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String odp = reader.readLine();
                JSONObject jsonObject = new JSONObject(odp);
                socket.close();
                if (!jsonObject.optBoolean("Wynik")) {
                    blad.setText("Nie masz takich uprawnien.");
                } else {
                    int index=observableList.indexOf(selected);
                    if(akcja.equals("zbanuj"))
                    {
                        selected.setOgraniczenie("BAN");
                    }
                    else if(akcja.equals("odbanuj"))
                    {
                        selected.setOgraniczenie("BRAK");
                    }
                    else if(akcja.equals("zablokuj"))
                    {
                        selected.setOgraniczenie("BLOCK");
                    }
                    else if(akcja.equals("odblokuj"))
                    {
                        selected.setOgraniczenie("BRAK");
                    }
                    else if(akcja.equals("dajMod"))
                    {
                        selected.setUprawnienie("MOD");
                    }
                    else if(akcja.equals("odbierzMod"))
                    {
                        selected.setUprawnienie("USER");
                    }
                    observableList.set(index,selected);
                    wiadomosc.setText("");

                }
            }
        }
    }

}
