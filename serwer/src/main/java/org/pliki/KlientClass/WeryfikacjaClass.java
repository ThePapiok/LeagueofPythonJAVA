package org.pliki.KlientClass;

import org.pliki.Uzytkownicy;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

public class WeryfikacjaClass {
    private Long id;
    private Long id_zad;
    private String nazwa;
    private Long id_uz;
    private String nick_uz;
    private Long id_mod;
    private String nick_mod;
    private String opis;
    private List<String> input;
    private List<String> output;
    private String zmienne;
    private int limitCzasu;
    private int czas;
    private String wiadomosc;
    private String akcja;
    private LocalDate data;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Long getId_uz() {
        return id_uz;
    }

    public void setId_uz(Long id_uz) {
        this.id_uz = id_uz;
    }

    public String getNick_uz() {
        return nick_uz;
    }

    public void setNick_uz(String nick_uz) {
        this.nick_uz = nick_uz;
    }

    public Long getId_mod() {
        return id_mod;
    }

    public void setId_mod(Long id_mod) {
        this.id_mod = id_mod;
    }

    public String getNick_mod() {
        return nick_mod;
    }

    public void setNick_mod(String nick_mod) {
        this.nick_mod = nick_mod;
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

    public String getZmienne() {
        return zmienne;
    }

    public void setZmienne(String zmienne) {
        this.zmienne = zmienne;
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

    public String getWiadomosc() {
        return wiadomosc;
    }

    public void setWiadomosc(String wiadomosc) {
        this.wiadomosc = wiadomosc;
    }

    public String getAkcja() {
        return akcja;
    }

    public void setAkcja(String akcja) {
        this.akcja = akcja;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }
}
