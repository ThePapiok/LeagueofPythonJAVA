package org.pliki;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
public class Zgloszenia {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id_zg;
    private String tytul;
    private String opis;
    private int limitCzasu;
    private int punktowanyCzas;
    private LocalDate data;
    private String zmienne;



    @ElementCollection
    @CollectionTable(name = "InputTab")
    @Column(name = "wynik_input")
    private List<String> input;
    @ElementCollection
    @CollectionTable(name = "OutputTab")
    @Column(name = "wynik_output")
    private List<String> output;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uzytkownikZgl")
    private Uzytkownicy uzytkownikZgl;

    public String getZmienne() {
        return zmienne;
    }

    public void setZmienne(String zmienne) {
        this.zmienne = zmienne;
    }

    public Long getId_zg() {
        return id_zg;
    }

    public void setId_zg(Long id_zg) {
        this.id_zg = id_zg;
    }

    public String getTytul() {
        return tytul;
    }

    public void setTytul(String tytul) {
        this.tytul = tytul;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public int getLimitCzasu() {
        return limitCzasu;
    }

    public void setLimitCzasu(int limitCzasu) {
        this.limitCzasu = limitCzasu;
    }

    public int getPunktowanyCzas() {
        return punktowanyCzas;
    }

    public void setPunktowanyCzas(int punktowanyCzas) {
        this.punktowanyCzas = punktowanyCzas;
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

    public Uzytkownicy getUzytkownikZgl() {
        return uzytkownikZgl;
    }

    public void setUzytkownikZgl(Uzytkownicy uzytkownikZgl) {
        this.uzytkownikZgl = uzytkownikZgl;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }
}
