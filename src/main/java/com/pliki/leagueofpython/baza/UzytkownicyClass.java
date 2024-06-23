package com.pliki.leagueofpython.baza;

import java.time.LocalDate;

public class UzytkownicyClass {
    private Long id_uz;
    private String nick;
    private String uprawnienie;
    private String ograniczenie;
    private LocalDate data;

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

    public String getUprawnienie() {
        return uprawnienie;
    }

    public void setUprawnienie(String uprawnienie) {
        this.uprawnienie = uprawnienie;
    }

    public String getOgraniczenie() {
        return ograniczenie;
    }

    public void setOgraniczenie(String ograniczenie) {
        this.ograniczenie = ograniczenie;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }
}
