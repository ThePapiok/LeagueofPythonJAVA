package com.pliki.leagueofpython;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;

public class Scena
{
    private FXMLLoader fxmlLoader;
    private Scene scene;
    private String css;
    public Scena(String sciezka1,String sciezka2) throws IOException
    {
        fxmlLoader=new FXMLLoader(Scena.class.getResource(sciezka1));
        scene=new Scene(fxmlLoader.load(),1000,600);
        css=Scena.class.getResource(sciezka2).toExternalForm();
        scene.getStylesheets().add(css);
    }
    public Scene getScene()
    {
        return scene;
    }
    public FXMLLoader getFxmlLoader(){return fxmlLoader;}

}
