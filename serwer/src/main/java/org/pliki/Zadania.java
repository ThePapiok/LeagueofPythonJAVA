package org.pliki;

import org.hibernate.query.Query;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
public class Zadania {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id_zad;

    private String nazwa;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uzytkownikZad")
    private Uzytkownicy uzytkownikZad;
    private String opis;
    @ElementCollection
    @CollectionTable(name = "InputTab2")
    @Column(name = "wynik_input")
    private List<String> input;
    @ElementCollection
    @CollectionTable(name = "OutputTab2")
    @Column(name = "wynik_output")
    private List<String> output;
    private LocalDate data;
    private int limitCzasu;
    private int czas;
    private int podejscia;
    private int ukonczone;
    private String zmienne;

    @PreRemove
    public void onDelete()
    {

        EntityManager entitymanager = Manager.getFactory().createEntityManager();
        EntityTransaction transaction = entitymanager.getTransaction();
        try {
            Query<Long> query1 = (Query<Long>) entitymanager.createQuery("SELECT w.id FROM Weryfikacja w WHERE w.id_zad = :id_zad");
            query1.setParameter("id_zad", id_zad);
            Long id = query1.getSingleResult();
            Weryfikacja weryfikacja = entitymanager.find(Weryfikacja.class, id);
            transaction.begin();
            entitymanager.remove(weryfikacja);
            transaction.commit();
        }
        catch (NoResultException e)
        {

        }
        try {
            Query<Wyslane> query2 = (Query<Wyslane>) entitymanager.createQuery("FROM Wyslane w WHERE w.id_zad = :id_zad");
            query2.setParameter("id_zad", id_zad);
            List<Wyslane> lista = query2.list();
            transaction.begin();
            for (Wyslane wyslane : lista) {
                Ranking ranking = entitymanager.find(Ranking.class, wyslane.getUzytkownikWys().getId_uz());
                ranking.setWynik(ranking.getWynik() - wyslane.getWynik());
                entitymanager.remove(wyslane);
            }
            transaction.commit();
        }
        catch (NoResultException e)
        {

        }
        entitymanager.close();
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

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
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

    public int getPodejscia() {
        return podejscia;
    }

    public void setPodejscia(int podejscia) {
        this.podejscia = podejscia;
    }

    public int getUkonczone() {
        return ukonczone;
    }

    public void setUkonczone(int ukonczone) {
        this.ukonczone = ukonczone;
    }

    public String getZmienne() {
        return zmienne;
    }

    public void setZmienne(String zmienne) {
        this.zmienne = zmienne;
    }
}
