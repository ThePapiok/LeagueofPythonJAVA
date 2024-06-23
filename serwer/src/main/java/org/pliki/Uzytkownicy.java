package org.pliki;

import org.hibernate.query.Query;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.logging.Logger;

@Entity
public class Uzytkownicy {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id_uz;
    private String nick;
    private String haslo;
    private String uprawnienie;
    private String ograniczenie;

    private boolean usuwanie;
    private LocalDate data;
    private int iloscNieodczytanych;

    public void beforeUpdate()
    {
        checkMod();
        checkMails();
    }


    public void checkMod()
    {
        Period period = Period.between(data, LocalDate.now());
        boolean coNajmniejMiesiac = period.toTotalMonths() >= 1;
        EntityManager entitymanager = Manager.getFactory().createEntityManager();
        EntityTransaction transaction = entitymanager.getTransaction();
        transaction.begin();
        if(coNajmniejMiesiac)
        {
            if(uprawnienie.equals("MOD"))
            {
                setUprawnienie("USER");
            }
        }
        transaction.commit();
    }

    public void checkMails()
    {
        EntityManager entitymanager = Manager.getFactory().createEntityManager();
        EntityTransaction transaction = entitymanager.getTransaction();
        Query<Poczta> query1 = (Query<Poczta>) entitymanager.createQuery("FROM Poczta p WHERE p.doUzytkownik.id_uz = :id_uz");
        query1.setParameter("id_uz", getId_uz());
        List<Poczta> resultList = query1.getResultList();
        transaction.begin();
        for(Poczta poczta:resultList)
        {
            Period period = Period.between(poczta.getData(), LocalDate.now());
            boolean coNajmniej3Miesiace = period.toTotalMonths() >= 3;
            if(coNajmniej3Miesiace)
            {
                entitymanager.remove(poczta);
                iloscNieodczytanych-=1;
            }

        }
        transaction.commit();

    }



    @OneToMany(mappedBy = "uzytkownikRank", fetch = FetchType.LAZY)
    private List<Ranking> ranking;

    @OneToMany(mappedBy = "uzytkownikZgl", fetch = FetchType.LAZY)
    private List<Zgloszenia> zgloszenia;

    @OneToMany(mappedBy = "uzytkownikWys", fetch = FetchType.LAZY)
    private List<Wyslane> wyslane;

    @OneToMany(mappedBy = "uzytkownikZad", fetch = FetchType.LAZY)
    private List<Zadania> zadania;
    @OneToMany(mappedBy = "uzytkownikMod", fetch = FetchType.LAZY)
    private List<Weryfikacja> weryfikacja;

    @OneToMany(mappedBy = "uzytkownikZad", fetch = FetchType.LAZY)
    private List<Weryfikacja> weryfikacjaUz;


    @OneToMany(mappedBy = "doUzytkownik", fetch = FetchType.LAZY)
    private List<Poczta> doUzytkownik;

    public boolean isUsuwanie() {
        return usuwanie;
    }

    public void setUsuwanie(boolean usuwanie) {
        this.usuwanie = usuwanie;
    }

    public List<Weryfikacja> getWeryfikacja() {
        return weryfikacja;
    }

    public void setWeryfikacja(List<Weryfikacja> weryfikacja) {
        this.weryfikacja = weryfikacja;
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

    public List<Zadania> getZadania() {
        return zadania;
    }

    public void setZadania(List<Zadania> zadania) {
        this.zadania = zadania;
    }

    public String getHaslo() {
        return haslo;
    }

    public void setHaslo(String haslo) {
        this.haslo = haslo;
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

    public List<Zgloszenia> getZgloszenia() {
        return zgloszenia;
    }

    public void setZgloszenia(List<Zgloszenia> zgloszenia) {
        this.zgloszenia = zgloszenia;
    }

    public List<Weryfikacja> getWeryfikacjaUz() {
        return weryfikacjaUz;
    }

    public void setWeryfikacjaUz(List<Weryfikacja> weryfikacjaUz) {
        this.weryfikacjaUz = weryfikacjaUz;
    }

    public List<Poczta> getDoUzytkownik() {
        return doUzytkownik;
    }

    public void setDoUzytkownik(List<Poczta> doUzytkownik) {
        this.doUzytkownik = doUzytkownik;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public List<Wyslane> getWyslane() {
        return wyslane;
    }

    public void setWyslane(List<Wyslane> wyslane) {
        this.wyslane = wyslane;
    }

    public List<Ranking> getRanking() {
        return ranking;
    }

    public void setRanking(List<Ranking> ranking) {
        this.ranking = ranking;
    }

    public int getIloscNieodczytanych() {
        return iloscNieodczytanych;
    }

    public void setIloscNieodczytanych(int iloscNieodczytanych) {
        this.iloscNieodczytanych = iloscNieodczytanych;
    }

}
