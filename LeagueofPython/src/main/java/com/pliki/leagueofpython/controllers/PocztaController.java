package com.pliki.leagueofpython.controllers;

import com.pliki.leagueofpython.Scena;
import com.pliki.leagueofpython.StageClass;

public class PocztaController {
    private Scena menu;

    public void wroc()
    {
        StageClass.getStage().setScene(menu.getScene());
    }

    public void setScene(Scena menu)
    {
        this.menu=menu;
    }
}
