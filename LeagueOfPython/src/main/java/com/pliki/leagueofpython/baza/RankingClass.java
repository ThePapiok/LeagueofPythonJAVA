package com.pliki.leagueofpython.baza;

public class RankingClass {

    private Long id_uz;
    private String nick;
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

    public Long getId_uz() {
        return id_uz;
    }

    public void setId_uz(Long id_uz) {
        this.id_uz = id_uz;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
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
