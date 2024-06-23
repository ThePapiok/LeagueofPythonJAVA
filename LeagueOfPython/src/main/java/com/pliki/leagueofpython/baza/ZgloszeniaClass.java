package com.pliki.leagueofpython.baza;

import java.time.LocalDate;
import java.util.List;

public class ZgloszeniaClass {
    private long Id;
    private String nazwa;
    private Long Id_g;
    private String nazwa_g;
    private int czas;
    private int limit;
    private String opis;
    private LocalDate data;
    private List<String> input;
    private List<String> output;
    private String zmienne;

    public ZgloszeniaClass() {
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public Long getId_g() {
        return Id_g;
    }

    public void setId_g(Long id_g) {
        Id_g = id_g;
    }

    public String getNazwa_g() {
        return nazwa_g;
    }

    public void setNazwa_g(String nazwa_g) {
        this.nazwa_g = nazwa_g;
    }

    public int getCzas() {
        return czas;
    }

    public void setCzas(int czas) {
        this.czas = czas;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public List<String> getInput() {
        return input;
    }

    public void setInput(List<String> input) {
        this.input = input;
    }

    public List<String> getOutput() {
        return output;
    }

    public void setOutput(List<String> output) {
        this.output = output;
    }

    public String getZmienne() {
        return zmienne;
    }

    public void setZmienne(String zmienne) {
        this.zmienne = zmienne;
    }
}
