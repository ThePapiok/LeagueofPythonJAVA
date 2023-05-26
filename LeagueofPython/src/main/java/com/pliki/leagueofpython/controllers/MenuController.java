package com.pliki.leagueofpython.controllers;

import com.pliki.leagueofpython.Scena;
import com.pliki.leagueofpython.StageClass;
import com.pliki.leagueofpython.User;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class MenuController {

    private List<Scena> sceny=new ArrayList<>();

    public void initialize() {


    }

    public void graj()
    {
        StageClass.getStage().setScene(sceny.get(3).getScene());
    }
    public void ranking()
    {
        StageClass.getStage().setScene(sceny.get(4).getScene());
    }
    public void wyslane()
    {
        StageClass.getStage().setScene(sceny.get(5).getScene());
    }
    public void przeslij()
    {
        StageClass.getStage().setScene(sceny.get(6).getScene());
    }
    public void wyloguj()
    {
        User.setId(null);
        StageClass.getStage().setScene(sceny.get(0).getScene());
    }
    public void zgloszenia()
    {
        StageClass.getStage().setScene(sceny.get(9).getScene());
    }
    public void uzytkownicy()
    {

    }

    public void poczta()
    {
        StageClass.getStage().setScene(sceny.get(7).getScene());

    }

    public void setSceny(List<Scena> sceny) {
        this.sceny = sceny;
    }

}
