package org.pliki;

import javax.persistence.*;

@Entity
public class Ranking {

    @Id
    private Long id_rank;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uzytkownikRank")
    private Uzytkownicy uzytkownikRank;
    private int wynik;
    private int rozwiazane;
    private int przeslane;
    private int proby;
    private float czas;


    public float getCzas() {
        return czas;
    }

    public void setCzas(float czas) {
        this.czas = czas;
    }

    public Long getId_rank() {
        return id_rank;
    }

    public void setId_rank(Long id_rank) {
        this.id_rank = id_rank;
    }

    public Uzytkownicy getUzytkownikRank() {
        return uzytkownikRank;
    }

    public void setUzytkownikRank(Uzytkownicy uzytkownikRank) {
        this.uzytkownikRank = uzytkownikRank;
    }

    public int getWynik() {
        return wynik;
    }

    public void setWynik(int wynik) {
        this.wynik = wynik;
    }

    public int getRozwiazane() {
        return rozwiazane;
    }

    public void setRozwiazane(int rozwiazane) {
        this.rozwiazane = rozwiazane;
    }

    public int getPrzeslane() {
        return przeslane;
    }

    public void setPrzeslane(int przeslane) {
        this.przeslane = przeslane;
    }

    public int getProby() {
        return proby;
    }

    public void setProby(int proby) {
        this.proby = proby;
    }


}
