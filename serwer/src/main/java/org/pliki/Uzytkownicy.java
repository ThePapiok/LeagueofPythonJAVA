package org.pliki;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Uzytkownicy {
    @Id
    @GeneratedValue
    private Long id_uz;
    private String nick;
    private String haslo;
    private int uprawnienie;
    private int ograniczenie;

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

    public String getHaslo() {
        return haslo;
    }

    public void setHaslo(String haslo) {
        this.haslo = haslo;
    }

    public int getUprawnienie() {
        return uprawnienie;
    }

    public void setUprawnienie(int uprawnienie) {
        this.uprawnienie = uprawnienie;
    }

    public int getOgraniczenie() {
        return ograniczenie;
    }

    public void setOgraniczenie(int ograniczenie) {
        this.ograniczenie = ograniczenie;
    }
}
