package com.pliki.leagueofpython.baza;

import java.time.LocalDate;
import java.util.List;

public class ZadaniaClass {
    private Long id_zad;
    private String nazwa;
    private Long id_gr;
    private String opis;
    private List<String> input;
    private List<String> output;
    private String zmienne;
    private LocalDate data;
    private int limitCzasu;
    private int czas;
    private int podejscia;
    private int ukonczone;

    public Long getId_zad() {
        return id_zad;
    }

    public void setId_zad(Long id_zad) {
        this.id_zad = id_zad;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public Long getId_gr() {
        return id_gr;
    }

    public void setId_gr(Long id_gr) {
        this.id_gr = id_gr;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
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

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public int getLimitCzasu() {
        return limitCzasu;
    }

    public void setLimitCzasu(int limitCzasu) {
        this.limitCzasu = limitCzasu;
    }

    public int getCzas() {
        return czas;
    }

    public void setCzas(int czas) {
        this.czas = czas;
    }

    public int getPodejscia() {
        return podejscia;
    }

    public void setPodejscia(int podejscia) {
        this.podejscia = podejscia;
    }

    public int getUkonczone() {
        return ukonczone;
    }

    public void setUkonczone(int ukonczone) {
        this.ukonczone = ukonczone;
    }

    public String getZmienne() {
        return zmienne;
    }

    public void setZmienne(String zmienne) {
        this.zmienne = zmienne;
    }
}
