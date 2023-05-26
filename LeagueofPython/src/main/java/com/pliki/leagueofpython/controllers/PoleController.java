package com.pliki.leagueofpython.controllers;

import com.pliki.leagueofpython.Scena;
import com.pliki.leagueofpython.StageClass;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;


import java.io.*;
import java.net.Socket;
import java.util.Collection;
import java.util.Collections;

public class PoleController
{
    private Scena graj;

    @FXML
    private TextArea pythonArea;

    @FXML
    private TextArea pythonOutput;
    @FXML
    protected void onClick( ) throws IOException {
        System.out.println(1);
        Socket socket=new Socket("localhost",8006);
        BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println(2);
        String a="1\n123214\n20:01\n";
        String wiadomosc="";
        String linia;
        a+= pythonArea.getText()+"\n#END\n";
        writer.write(a);
        writer.flush();
        System.out.println(3);

        while((linia=reader.readLine())!=null)
        {
            wiadomosc+=linia+"\n";
        }
        System.out.println(4);
        /*
        while((wiadomosc=reader.readLine())==null)
        {
            wiadomosc+="\n";
        }
        /*
        while((linia=reader.readLine())!=null)
        {
            wiadomosc+=linia+"\n";
        }
        /
         */
        pythonOutput.setText(wiadomosc);

        reader.close();
        writer.close();
        socket.close();

    }

    public void wroc()
    {
        StageClass.getStage().setScene(graj.getScene());
    }

    public void setScene(Scena graj)
    {
        this.graj=graj;
    }




}
