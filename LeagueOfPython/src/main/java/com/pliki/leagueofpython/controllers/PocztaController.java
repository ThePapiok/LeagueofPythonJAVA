package com.pliki.leagueofpython.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pliki.leagueofpython.StageClass;
import com.pliki.leagueofpython.User;
import com.pliki.leagueofpython.baza.PocztaClass;
import com.pliki.leagueofpython.baza.ZgloszeniaClass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class PocztaController {
    @FXML
    private ListView wiadomosci;
    private ObservableList<String> observableList;
    @FXML
    private TextArea wiadomosc;
    @FXML
    private RadioButton tryb;

    private List<PocztaClass> listaPoczta;

    private MenuController menuController;

    public void initialize() {
        tryb.selectedProperty().addListener((observable, oldValue, newValue) -> {
            try {

                Socket socket = new Socket("localhost", 8006);
                JSONObject json = new JSONObject();
                json.put("Akcja", "changeTryb");
                json.put("Id",User.getId());
                if (newValue) {
                    json.put("Tryb", true);
                } else {
                    json.put("Tryb", false);
                }
                BufferedWriter writer = null;
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                writer.write(json.toString());
                writer.newLine();
                writer.flush();
                socket.close();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
        wiadomosci.setCellFactory(param -> new ListCell<String>() {
            private TextArea textArea = new TextArea();

            {
                textArea.setEditable(false);
                textArea.setWrapText(false);
                textArea.setMaxHeight(50);
                textArea.setMaxWidth(210);
                textArea.setOnMouseClicked(event -> {
                    wiadomosci.getSelectionModel().select(getIndex());
                    wiadomosci.scrollTo(getIndex());
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    textArea.setText(item);
                    setGraphic(textArea);
                    setPrefHeight(60);
                }
            }
        });
    }

    public void wroc() throws IOException {
        menuController.resetScene();
    }

    public void setController(MenuController menuController)
    {
        this.menuController=menuController;
    }

    public void resetScene() throws IOException {
        wiadomosc.setText("");
        wiadomosci.getItems().clear();
        Socket socket = new Socket("localhost", 8006);
        JSONObject json = new JSONObject();
        json.put("Akcja","getPoczta");
        json.put("Id", User.getId());
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.write(json.toString());
        writer.newLine();
        writer.flush();
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String odpTekst = reader.readLine();
        JSONObject wynik = new JSONObject(odpTekst);
        if(wynik.optBoolean("Wynik"))
        {
            tryb.setSelected(true);
        }
        else
        {
            tryb.setSelected(false);
        }
        String odp = reader.readLine();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        listaPoczta = objectMapper.readValue(odp, new TypeReference<List<PocztaClass>>() {});
        List<String> listaWiadmosci=new ArrayList<>();
        String data;
        for (PocztaClass pocztaClass : listaPoczta) {
            data= String.valueOf(pocztaClass.getData());
            System.out.println(data);
            listaWiadmosci.add(pocztaClass.getId()+" | "+data+" | "+pocztaClass.getTytul());
        }
        observableList = FXCollections.observableArrayList(listaWiadmosci);
        socket.close();
        StageClass.getStage().setScene(wiadomosci.getScene());
        wiadomosci.setItems(observableList);
    }

    public void usun() throws IOException {
        String selected = (String) wiadomosci.getSelectionModel().getSelectedItem();
        wiadomosc.setText("");
        if (selected != null) {
            observableList.remove(selected);
            Long id= Long.valueOf(selected.substring(0,selected.indexOf(" ")));
            Socket socket = new Socket("localhost", 8006);
            JSONObject json = new JSONObject();
            json.put("Akcja","usunWiadomosc");
            json.put("Id", id);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write(json.toString());
            writer.newLine();
            writer.flush();
            socket.close();
        }

    }

    public void sprawdz() throws IOException {
        String selected = (String) wiadomosci.getSelectionModel().getSelectedItem();
        if (selected != null)
        {
            Socket socket = new Socket("localhost", 8006);
            JSONObject json1 = new JSONObject();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            json1.put("Akcja","przeczytanaWiadomosc");
            json1.put("Id",User.getId());
            writer.write(json1.toString());
            writer.newLine();
            writer.flush();
            socket.close();
            Long id= Long.valueOf(selected.substring(0,selected.indexOf(" ")));
            if(tryb.isSelected())
            {
                Socket socket1 = new Socket("localhost", 8006);
                BufferedWriter writer1 = new BufferedWriter(new OutputStreamWriter(socket1.getOutputStream()));
                JSONObject json2 = new JSONObject();
                json2.put("Akcja","usunWiadomosc");
                json2.put("Id", id);
                writer1.write(json2.toString());
                writer1.newLine();
                writer1.flush();
                socket1.close();
            }
            for(PocztaClass pocztaClass:listaPoczta)
            {
                if(pocztaClass.getId()==id)
                {
                    wiadomosc.setText(pocztaClass.getWiadomosc());
                    break;
                }
            }

        }

    }
}
