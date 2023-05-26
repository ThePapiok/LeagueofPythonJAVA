package com.pliki.leagueofpython.controllers;

import com.pliki.leagueofpython.Scena;
import com.pliki.leagueofpython.StageClass;

public class WyslaneController {
    private Scena menu;
    private Scena kod;

    public void wroc()
    {
        StageClass.getStage().setScene(menu.getScene());
    }

    public void setSceny(Scena menu,Scena kod)
    {
        this.menu=menu;
        this.kod=kod;
    }
}
