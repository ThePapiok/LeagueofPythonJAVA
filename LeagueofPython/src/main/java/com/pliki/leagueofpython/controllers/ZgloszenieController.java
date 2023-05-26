package com.pliki.leagueofpython.controllers;

import com.pliki.leagueofpython.Scena;
import com.pliki.leagueofpython.StageClass;

public class ZgloszenieController {
    private Scena zgloszenia;

    public void wroc()
    {
        StageClass.getStage().setScene(zgloszenia.getScene());
    }

    public void setScene(Scena menu)
    {
        this.zgloszenia=zgloszenia;
    }
}
