package org.pliki.KlientClass;

import org.pliki.Uzytkownicy;

import javax.persistence.*;
import java.time.LocalDate;

public class WyslaneClass {

    private Long id_zad;
    private Long id_wys;
    private String nazwa;
    private boolean zrobione;
    private boolean publiczny;
    private int wynik;
    private Long id_uz;
    private int proby;
    private float czas;
    private float czasUruchomienia;
    private LocalDate data;
    private String kod;
    private String output;

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

    public boolean isZrobione() {
        return zrobione;
    }

    public void setZrobione(boolean zrobione) {
        this.zrobione = zrobione;
    }

    public boolean isPubliczny() {
        return publiczny;
    }

    public void setPubliczny(boolean publiczny) {
        this.publiczny = publiczny;
    }

    public int getWynik() {
        return wynik;
    }

    public void setWynik(int wynik) {
        this.wynik = wynik;
    }

    public Long getId_uz() {
        return id_uz;
    }

    public void setId_uz(Long id_uz) {
        this.id_uz = id_uz;
    }

    public int getProby() {
        return proby;
    }

    public void setProby(int proby) {
        this.proby = proby;
    }

    public float getCzas() {
        return czas;
    }

    public void setCzas(float czas) {
        this.czas = czas;
    }

    public float getCzasUruchomienia() {
        return czasUruchomienia;
    }

    public void setCzasUruchomienia(float czasUruchomienia) {
        this.czasUruchomienia = czasUruchomienia;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getKod() {
        return kod;
    }

    public void setKod(String kod) {
        this.kod = kod;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public Long getId_wys() {
        return id_wys;
    }

    public void setId_wys(Long id_wys) {
        this.id_wys = id_wys;
    }
}
