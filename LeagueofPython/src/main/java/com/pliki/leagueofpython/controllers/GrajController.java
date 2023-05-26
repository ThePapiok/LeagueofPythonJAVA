package com.pliki.leagueofpython.controllers;

import com.pliki.leagueofpython.Scena;
import com.pliki.leagueofpython.StageClass;
import javafx.stage.Stage;

public class GrajController {

    private Scena menu;
    private Scena pole;

    public void wroc()
    {
        StageClass.getStage().setScene(menu.getScene());
    }

    public void setSceny(Scena menu, Scena pole)
    {
        this.menu=menu;
        this.pole=pole;
    }


}
