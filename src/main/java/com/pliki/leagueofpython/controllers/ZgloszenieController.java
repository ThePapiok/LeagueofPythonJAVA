package com.pliki.leagueofpython.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pliki.leagueofpython.Scena;
import com.pliki.leagueofpython.StageClass;
import com.pliki.leagueofpython.User;
import com.pliki.leagueofpython.baza.ZgloszeniaClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ZgloszenieController {
    @FXML
    private TextArea wiadomosc;
    @FXML
    private Label nick;
    @FXML
    private TextField nazwa;
    @FXML
    private TextArea opis;
    @FXML
    private TextField czas;
    @FXML
    private TextField limit;
    @FXML
    private ListView input;
    @FXML
    private ListView output;
    @FXML
    private Label blad;
    @FXML
    private TextField zmienne;

    private ZgloszeniaController zgloszeniaController;
    private List<ZgloszeniaClass> listaZgloszen;
    private ZgloszeniaClass zgloszeniaClass;
    private int index;

    public void wroc() throws IOException {
        zgloszeniaController.resetScene();
    }


    public void setController(ZgloszeniaController zgloszeniaController)
    {
        this.zgloszeniaController=zgloszeniaController;
    }

    public void resetScene(int index, List<ZgloszeniaClass> listaZgloszen)
    {

        this.index=index;
        this.listaZgloszen=listaZgloszen;
        zgloszeniaClass = listaZgloszen.get(index);
        zmienne.setText(zgloszeniaClass.getZmienne());
        nick.setText(zgloszeniaClass.getNazwa_g());
        nazwa.setText(zgloszeniaClass.getNazwa());
        opis.setText(zgloszeniaClass.getOpis());
        czas.setText(String.valueOf(zgloszeniaClass.getCzas()));
        limit.setText(String.valueOf(zgloszeniaClass.getLimit()));
        input.getItems().clear();
        output.getItems().clear();
        input.getItems().addAll(zgloszeniaClass.getInput());
        output.getItems().addAll(zgloszeniaClass.getOutput());

        wiadomosc.setText("");
        blad.setText("");
        StageClass.getStage().setScene(wiadomosc.getScene());
    }

    public void zatwierdz() throws IOException {
        sendObject("addZadanie");
    }
    public void odrzuc() throws IOException {
        sendObject("rejectZadanie");
    }
    public void block() throws IOException {
        sendObject("blockZadanie");
    }

    public void sendObject(String akcja) throws IOException {
        Socket socket = new Socket("localhost", 8006);
        JSONObject json = new JSONObject();
        json.put("Akcja",akcja);
        json.put("Id", User.getId());
        json.put("Id_g",zgloszeniaClass.getId_g());
        json.put("Wiadomosc",wiadomosc.getText());
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
        if(odp.optBoolean("Wynik"))
        {
            listaZgloszen.remove(index);
            if(listaZgloszen.size()==0)
            {
                zgloszeniaController.resetScene();
            }
            else {
                if (index > listaZgloszen.size() - 1) {
                    index = 0;
                }
                resetScene(index, listaZgloszen);
            }
        }
        else
        {
            if(odp.optBoolean("Uprawnienia"))
            {
                listaZgloszen.remove(index);
                if(listaZgloszen.size()==0)
                {
                    zgloszeniaController.resetScene();
                    zgloszeniaController.setError("Poprzednie zgloszenie bylo nieaktualne");
                }
                else {
                    if (index > listaZgloszen.size() - 1) {
                        index = 0;
                    }
                    resetScene(index, listaZgloszen);
                    blad.setText("Poprzednie zgloszenie bylo nieaktualne");
                }
            }
            else {
                blad.setText("Nie masz wystarczajacych uprawnien");
            }
        }
        socket.close();
    }



}
