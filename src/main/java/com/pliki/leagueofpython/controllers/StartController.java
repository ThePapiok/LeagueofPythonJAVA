package com.pliki.leagueofpython.controllers;

import com.pliki.leagueofpython.Scena;
import com.pliki.leagueofpython.StageClass;
import com.pliki.leagueofpython.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;


import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import org.json.JSONObject;

import static java.lang.Math.min;

public class StartController {

    private List<String> dozwolone=Arrays.asList("1","2","3","4","5","6","7","8","9","0","q","w","e","r","t","y","u","i","o","p","a","s","d","f","g","h","j","k","l","z","x","c","v","b","n","m","\b");
    @FXML
    private TextField poleNick;
    @FXML
    private PasswordField poleHaslo;
    @FXML
    private Label blad;
    private MenuController menuController;


    public void initialize() {
        poleNick.textProperty().addListener((observable, oldValue, newValue) -> {
            sprawdzenie(oldValue,newValue,poleNick);
        });
        poleHaslo.textProperty().addListener((observable, oldValue, newValue) -> {
            sprawdzenie(oldValue,newValue,poleHaslo);
        });
    }

    public void register() throws IOException {
        if(poleNick.getLength()>=6&&poleHaslo.getLength()>=6)
        {
            Socket socket = new Socket("localhost", 8006);
            JSONObject json = new JSONObject();
            json.put("Akcja","Register");
            json.put("Login",poleNick.getText());
            json.put("Haslo",poleHaslo.getText());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write(json.toString());
            writer.newLine();
            writer.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String odpTekst = reader.readLine();
            JSONObject odp = new JSONObject(odpTekst);
            poleNick.setText("");
            poleHaslo.setText("");
            blad.setText(odp.optString("Wiadomosc"));
            socket.close();
        }
        else
        {
            blad.setText("Login lub Haslo musi miec co najmniej 6 liter");
        }
    }

    public void login() throws IOException {
        Socket socket = new Socket("localhost", 8006);
        JSONObject json = new JSONObject();
        json.put("Akcja","Login");
        json.put("Login",poleNick.getText());
        json.put("Haslo",poleHaslo.getText());
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.write(json.toString());
        writer.newLine();
        writer.flush();
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String odpTekst = reader.readLine();
        JSONObject odp = new JSONObject(odpTekst);
        if(odp.optBoolean("Wynik"))
        {
            User.setId(odp.optLong("Id"));
            menuController.resetScene();
        }
        else
        {
            blad.setText(odp.optString("Wiadomosc"));
            poleNick.setText("");
            poleHaslo.setText("");
        }
        socket.close();

    }

    public void sprawdzenie(String oldValue, String newValue, TextInputControl pole)
    {
        int newLen=newValue.length()-1;
        if(newLen!=-1) {
            int minLen = min(oldValue.length() - 1,newLen);
            String chr = String.valueOf(newValue.charAt(newValue.length() - 1));
            for (int i = 0; i <= minLen; i++) {
                if (newValue.charAt(i) != oldValue.charAt(i)) {
                    chr = String.valueOf(newValue.charAt(i));
                    break;
                }
            }
            if (newLen > 19) {
                pole.setText(oldValue);
                blad.setText("Login lub Haslo musi miec co najwyzej 20 liter");

            }
            else if (!dozwolone.contains(chr.toLowerCase())) {
                pole.setText(oldValue);
                blad.setText("Niedozwolony znak");
            }
        }
    }
    public void setController(MenuController menuController)
    {
        this.menuController=menuController;
    }

    public void resetScene()
    {
        poleNick.setText("");
        poleHaslo.setText("");
        blad.setText("");
        User.setId(null);
        StageClass.getStage().setScene(blad.getScene());
    }

    public void setBlad(String string)
    {
        blad.setText(string);
    }

}
