package org.pliki.KlientClass;

import org.pliki.Uzytkownicy;

import java.time.LocalDate;
import java.util.Date;


public class PocztaClass {
    private Long Id;
    private String tytul;
    private String wiadomosc;

    private Long Id_g;

    private LocalDate data;

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getTytul() {
        return tytul;
    }

    public void setTytul(String tytul) {
        this.tytul = tytul;
    }

    public String getWiadomosc() {
        return wiadomosc;
    }

    public void setWiadomosc(String wiadomosc) {
        this.wiadomosc = wiadomosc;
    }

    public Long getId_g() {
        return Id_g;
    }

    public void setId_g(Long id_g) {
        Id_g = id_g;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }
}
