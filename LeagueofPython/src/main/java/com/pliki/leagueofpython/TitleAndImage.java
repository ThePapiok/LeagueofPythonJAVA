package com.pliki.leagueofpython;

import javafx.scene.image.Image;
import javafx.stage.Stage;

public class TitleAndImage
{

    public TitleAndImage(String sciezka,String tytul, Stage stage)
    {
        ClassLoader classLoader=TitleAndImage.class.getClassLoader();
        Image icon=new Image(classLoader.getResourceAsStream(sciezka));
        stage.getIcons().add(icon);
        stage.setTitle(tytul);
        stage.getIcons().add(icon);
    }


}
