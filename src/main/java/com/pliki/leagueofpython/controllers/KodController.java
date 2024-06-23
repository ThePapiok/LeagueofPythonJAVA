package com.pliki.leagueofpython.controllers;

import com.pliki.leagueofpython.StageClass;
import com.pliki.leagueofpython.User;
import com.pliki.leagueofpython.baza.WyslaneClass;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.io.IOException;


public class KodController {
    private WyslaneController wyslaneController;
    @FXML
    private TextArea kod;
    @FXML
    private TextArea output;
    private Long id;
    private int tryb;

    public void wroc() throws IOException {
        wyslaneController.resetScene(tryb, id);
    }



    public void setController(WyslaneController wyslaneController)
    {
        this.wyslaneController=wyslaneController;
    }

    public void resetScene(WyslaneClass wyslaneClass,int tryb,Long id)
    {
        this.id=id;
        this.tryb=tryb;
        kod.setText(wyslaneClass.getKod());
        output.setText(wyslaneClass.getOutput());
        StageClass.getStage().setScene(kod.getScene());

    }
}
