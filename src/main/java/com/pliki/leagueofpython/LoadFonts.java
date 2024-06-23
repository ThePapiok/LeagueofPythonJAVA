package com.pliki.leagueofpython;

import javafx.scene.text.Font;

public class LoadFonts {

    public LoadFonts(String sciezka1,String sciezka2) {
        Font.loadFont(getClass().getResourceAsStream(sciezka1),12);
        Font p=Font.loadFont(getClass().getResourceAsStream(sciezka2),12);
    }

}
