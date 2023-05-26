package com.pliki.leagueofpython;

import javafx.scene.text.Font;

public class LoadFonts {
    public LoadFonts() {
    }

    public void load()
    {

        Font.loadFont(getClass().getResourceAsStream("/fonty/FIRA.ttf"),12);
        Font p=Font.loadFont(getClass().getResourceAsStream("/fonty/PressStart2P.ttf"),12);
        System.out.println(p);
    }
}
