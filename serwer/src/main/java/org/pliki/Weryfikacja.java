package org.pliki;

import org.hibernate.query.Query;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
public class Weryfikacja {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long id_zad;

    private String nazwa;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uzytkownikZad")
    private Uzytkownicy uzytkownikZad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uzytkownikMod")
    private Uzytkownicy uzytkownikMod;
    private String opis;
    @ElementCollection
    @CollectionTable(name = "InputTab3")
    @Column(name = "wynik_input")
    private List<String> input;
    @ElementCollection
    @CollectionTable(name = "OutputTab3")
    @Column(name = "wynik_output")
    private List<String> output;

    private String zmienne;
    private int limitCzasu;
    private int czas;

    private String wiadomosc;

    private String akcja;

    private LocalDate data;

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getAkcja() {
        return akcja;
    }

    public void setAkcja(String akcja) {
        this.akcja = akcja;
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

    public Uzytkownicy getUzytkownikZad() {
        return uzytkownikZad;
    }

    public void setUzytkownikZad(Uzytkownicy uzytkownikZad) {
        this.uzytkownikZad = uzytkownikZad;
    }

    public Uzytkownicy getUzytkownikMod() {
        return uzytkownikMod;
    }

    public void setUzytkownikMod(Uzytkownicy uzytkownikMod) {
        this.uzytkownikMod = uzytkownikMod;
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

    public String getZmienne() {
        return zmienne;
    }

    public void setZmienne(String zmienne) {
        this.zmienne = zmienne;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
