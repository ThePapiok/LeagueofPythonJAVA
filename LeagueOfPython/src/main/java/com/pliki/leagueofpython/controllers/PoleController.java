package com.pliki.leagueofpython.controllers;

import com.pliki.leagueofpython.StageClass;
import com.pliki.leagueofpython.User;
import com.pliki.leagueofpython.baza.ZadaniaClass;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class PoleController
{
    private GrajController grajController;
    private int hours;
    private int minutes;
    private int seconds;
    private Timeline timeline;

    @FXML
    private TextArea pythonArea;

    @FXML
    private TextArea pythonOutput;
    @FXML
    private Label blad;
    @FXML
    private Label autor;
    @FXML
    private Label tytul;
    @FXML
    private Label iloscProb;
    @FXML
    private Label licznikCzasu;
    @FXML
    private TextArea tresc;
    @FXML
    private RadioButton prywatny;
    @FXML
    private RadioButton publiczny;

    private List<ZadaniaClass> listaZadan;
    private ZadaniaClass zadaniaClass;
    private int index;

    public void initialize() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), this::updateTime));
        timeline.setCycleCount(Timeline.INDEFINITE);
        ToggleGroup toggleGroup = new ToggleGroup();
        publiczny.setToggleGroup(toggleGroup);
        prywatny.setToggleGroup(toggleGroup);
    }
    public void sprawdz( ) throws IOException {
        Socket socket=new Socket("localhost",8006);
        BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        JSONObject json=new JSONObject();
        json.put("Akcja","sprawdzZadanie");
        json.put("Id_zad",zadaniaClass.getId_zad());
        json.put("Id_gr", User.getId());
        json.put("Czas",hours*60+minutes);
        json.put("Proby",Integer.parseInt(iloscProb.getText()));
        json.put("Kod",pythonArea.getText());
        if(publiczny.isSelected())
        {
            json.put("Publiczny",true);
        }
        else
        {
            json.put("Publiczny",false);
        }
        writer.write(json.toString());
        writer.newLine();
        writer.flush();
        BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String odpTekst = reader.readLine();
        JSONObject wynik = new JSONObject(odpTekst);
        if(wynik.optBoolean("Istnieje")) {
            if(wynik.optBoolean("Robione"))
            {
                blad.setText("Zadanie juz zostalo przez ciebie rozwiazane.");
            }
            else {
                if (wynik.optBoolean("Wynik")) {
                    pythonOutput.setText(wynik.optString("Wiadomosc"));
                    timeline.stop();
                    listaZadan.remove(index);
                    index--;
                } else {
                    pythonOutput.setText(wynik.optString("Wiadomosc"));
                    int intIlosc = Integer.parseInt(iloscProb.getText());
                    intIlosc++;
                    iloscProb.setText(String.valueOf(intIlosc));
                }
            }
        }
        else
        {
            blad.setText("Zadanie nie jest aktualne.");
        }
        socket.close();

    }

    public void wroc() throws IOException {
        timeline.stop();
        grajController.resetScene(0,"");
    }

    public void setController(GrajController grajController)
    {
        this.grajController=grajController;
    }

    public void resetScene(int index,List<ZadaniaClass> listaZadan) throws IOException {
        this.index=index;
        this.listaZadan=listaZadan;
        zadaniaClass=listaZadan.get(index);
        Socket socket = new Socket("localhost", 8006);
        JSONObject json = new JSONObject();
        json.put("Akcja","isZadanie");
        json.put("Id",User.getId());
        json.put("Id_zad",zadaniaClass.getId_zad());
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.write(json.toString());
        writer.newLine();
        writer.flush();
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String odp = reader.readLine();
        JSONObject jsonObject=new JSONObject(odp);
        socket.close();
        if(jsonObject.optBoolean("IstniejeZadanie")==false)
        {
            listaZadan.remove(index);
            index--;
            nastepne();
        }
        else
        {
            if(jsonObject.optBoolean("IstniejeWyslane")==true)
            {
                if(jsonObject.optBoolean("Zrobione")==true)
                {
                    listaZadan.remove(index);
                    index--;
                    nastepne();
                }
                else
                {
                    pythonArea.setText(jsonObject.optString("Kod"));
                    hours=(int)jsonObject.optFloat("Czas");
                    minutes=(int)(jsonObject.optFloat("Czas")*60);
                    seconds=0;
                    System.out.println(jsonObject.optInt("Czas"));
                    System.out.println(hours);
                    System.out.println(minutes);
                    iloscProb.setText(String.valueOf(1+jsonObject.optInt("Proby")));
                    if(jsonObject.optBoolean("Publiczny"))
                    {
                        publiczny.setSelected(true);
                    }
                    else
                    {
                        prywatny.setSelected(true);
                    }
                    pythonOutput.setText(jsonObject.optString("Output"));
                    timeline.play();
                    autor.setText(jsonObject.getString("Nick"));
                    tytul.setText(zadaniaClass.getNazwa());
                    tresc.setText(zadaniaClass.getOpis());
                    StageClass.getStage().setScene(pythonArea.getScene());
                }
            }
            else
            {
                hours = 0;
                minutes = 0;
                seconds = 0;
                licznikCzasu.setText("00:00");
                iloscProb.setText("1");
                publiczny.setSelected(true);
                blad.setText("");
                pythonArea.setText("def "+zadaniaClass.getNazwa()+"("+zadaniaClass.getZmienne()+"):\n\treturn");
                pythonOutput.setText("");
                timeline.play();
                autor.setText(jsonObject.getString("Nick"));
                tytul.setText(zadaniaClass.getNazwa());
                tresc.setText(zadaniaClass.getOpis());
                StageClass.getStage().setScene(pythonArea.getScene());

            }
        }

    }

    public void poprzednie() throws IOException {

        index--;
        if (index < 0) {
            index = listaZadan.size()-1;
        }
        resetScene(index, listaZadan);
    }

    public void nastepne() throws IOException {

        index++;
        if(listaZadan.size()==0)
        {
            grajController.resetScene(0,"");
        }
        else {
            if (index > listaZadan.size() - 1) {
                index = 0;
            }
            resetScene(index, listaZadan);
        }
    }


    private void updateTime(ActionEvent event) {
        seconds++;
        if (seconds >= 60) {
            seconds = 0;
            minutes++;
            if (minutes >= 60) {
                minutes = 0;
                hours++;
                if (hours >= 60) {
                    timeline.stop();
                }
            }
        }
        String time = String.format("%02d:%02d", hours, minutes);
        licznikCzasu.setText(time);
    }
}
