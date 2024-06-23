package org.pliki;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Wyslane {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id_wys;
    private boolean zrobione;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uzytkownikWys")
    private Uzytkownicy uzytkownikWys;

    private int wynik;
    private Long id_zad;
    private String nazwa;

    private int proby;

    private float czas;
    private float czasUruchomienia;
    private String kod;
    private String output;
    private boolean publiczny;

    private LocalDate data;

    public Long getId_wys() {
        return id_wys;
    }

    public void setId_wys(Long id_wys) {
        this.id_wys = id_wys;
    }

    public boolean isZrobione() {
        return zrobione;
    }

    public void setZrobione(boolean zrobione) {
        this.zrobione = zrobione;
    }

    public Uzytkownicy getUzytkownikWys() {
        return uzytkownikWys;
    }

    public void setUzytkownikWys(Uzytkownicy uzytkownikWys) {
        this.uzytkownikWys = uzytkownikWys;
    }

    public int getWynik() {
        return wynik;
    }

    public void setWynik(int wynik) {
        this.wynik = wynik;
    }

    public Long getId_zad() {
        return id_zad;
    }

    public void setId_zad(Long id_zad) {
        this.id_zad = id_zad;
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


    public String getKod() {
        return kod;
    }

    public void setKod(String kod) {
        this.kod = kod;
    }

    public boolean isPubliczny() {
        return publiczny;
    }

    public void setPubliczny(boolean publiczny) {
        this.publiczny = publiczny;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }
}
