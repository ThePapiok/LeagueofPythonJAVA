package com.pliki.leagueofpython.controllers;

import com.pliki.leagueofpython.Scena;
import com.pliki.leagueofpython.StageClass;

public class RankingController {
    private Scena menu;
    private Scena wyslane;

    public void wroc()
    {
        StageClass.getStage().setScene(menu.getScene());
    }

    public void setSceny(Scena menu, Scena wyslane)
    {
        this.menu=menu;
        this.wyslane=wyslane;
    }
}
