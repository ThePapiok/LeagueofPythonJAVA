package com.pliki.leagueofpython.controllers;

import com.pliki.leagueofpython.Scena;
import com.pliki.leagueofpython.StageClass;

public class ZgloszeniaController {
    private Scena menu;
    private Scena zgloszenie;

    public void wroc()
    {
        StageClass.getStage().setScene(menu.getScene());
    }

    public void setSceny(Scena menu, Scena zgloszenie)
    {
        this.menu=menu;
        this.zgloszenie=zgloszenie;
    }
}
