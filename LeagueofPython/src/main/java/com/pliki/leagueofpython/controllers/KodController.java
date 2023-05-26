package com.pliki.leagueofpython.controllers;

import com.pliki.leagueofpython.Scena;
import com.pliki.leagueofpython.StageClass;

public class KodController {
    private Scena wyslane;

    public void wroc()
    {
        StageClass.getStage().setScene(wyslane.getScene());
    }

    public void setScene(Scena menu)
    {
        this.wyslane=wyslane;
    }
}
