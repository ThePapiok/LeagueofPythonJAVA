package org.pliki;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Poczta {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id_wiad;

    private String tytul;
    private String wiadomosc;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doUzytkownik")
    private Uzytkownicy doUzytkownik;

    private LocalDate data;

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Long getId_wiad() {
        return id_wiad;
    }

    public void setId_wiad(Long id_wiad) {
        this.id_wiad = id_wiad;
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

    public Uzytkownicy getDoUzytkownik() {
        return doUzytkownik;
    }

    public void setDoUzytkownik(Uzytkownicy doUzytkownik) {
        this.doUzytkownik = doUzytkownik;
    }

}
