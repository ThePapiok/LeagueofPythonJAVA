package org.pliki;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hibernate.Hibernate;
import org.hibernate.query.Query;
import org.json.JSONArray;
import org.mindrot.jbcrypt.BCrypt;
import org.pliki.KlientClass.*;
import org.python.core.PyException;
import org.python.util.PythonInterpreter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONObject;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;


public class Watek extends Thread {
    private Socket socket;

    public Watek(Socket socket) {
        this.socket = socket;

    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            String tekst = reader.readLine();
            JSONObject json = new JSONObject(tekst);
            JSONObject odp = new JSONObject();
            String Akcja = json.optString("Akcja");
            if (Akcja.equals("Register")) {
                sprawdzRegister(writer, json, odp);
            } else if (Akcja.equals("Login")) {
                sprawdzLogin(writer, json, odp);
            } else if (Akcja.equals("Przeslij")) {
                dodajZgloszenie(writer, json, odp);
            } else if (Akcja.equals("getZgloszenia")) {
                getZgloszenia(writer,json);
            } else if (Akcja.equals("addZadanie")) {
                addZadanie(reader, writer, json, odp);
            } else if (Akcja.equals("rejectZadanie")) {
                rejectZadanie(reader, writer, json, odp);
            } else if (Akcja.equals("blockZadanie")) {
                blockZadanie(reader, writer, json, odp);
            } else if (Akcja.equals("getPoczta")) {
                getPoczta(writer, json);
            } else if (Akcja.equals("usunWiadomosc")) {
                usunWiadomosc(json);
            } else if (Akcja.equals("changeTryb")) {
                changeTryb(json);
            } else if (Akcja.equals("getZadania")) {
                getZadania(writer, json);
            } else if (Akcja.equals("usunZadanie")) {
                usunZadanie(writer, json);
            } else if (Akcja.equals("isZgloszenie")) {
                isZgloszenie(writer, json);
            } else if (Akcja.equals("isZadanie")) {
                isZadanie(writer, json);
            } else if (Akcja.equals("sprawdzZadanie")) {
                sprawdzZadanie(writer, json);
            } else if (Akcja.equals("getRanking")) {
                getRanking(writer, json);
            } else if (Akcja.equals("przeczytanaWiadomosc")) {
                przeczytanaWiadomosc(json);
            } else if (Akcja.equals("getMenu")) {
                getMenu(writer, json);
            } else if (Akcja.equals("getWyslane")) {
                getWyslane(writer, json);
            } else if (Akcja.equals("changeWidocznosc")) {
                changeWidocznosc(writer, json);
            } else if (Akcja.equals("getUzytkownicy")) {
                getUzytkownicy(writer, json);
            } else if (Akcja.equals("changeUzytkownik")) {
                changeUzytkownik(writer, json);
            } else if (Akcja.equals("getWeryfikacja")) {
                getWeryfikacja(writer);
            } else if (Akcja.equals("changeWeryfikacja")) {
                changeWeryfikacja(writer, json);
            }
            else if(Akcja.equals("isWeryfikacja"))
            {
                isWeryfikacja(writer,json);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    public void sprawdzRegister(BufferedWriter writer, JSONObject json, JSONObject odp) throws IOException {
        String login = json.optString("Login");
        String haslo = json.optString("Haslo");
        Long ilosc = 0l;
        EntityManager entitymanager = Manager.getFactory().createEntityManager();
        try {
            Query<Long> query = (Query<Long>) entitymanager.createQuery("SELECT COUNT(u) FROM Uzytkownicy u WHERE u.nick = :nick");
            query.setParameter("nick", login);
            ilosc = query.getSingleResult();
        } catch (NoResultException e) {

        }
        if (ilosc == 0) {
            EntityTransaction transaction = entitymanager.getTransaction();
            transaction.begin();
            Uzytkownicy uzytkownicy = new Uzytkownicy();
            uzytkownicy.setNick(login);
            uzytkownicy.setHaslo(BCrypt.hashpw(haslo, BCrypt.gensalt()));
            uzytkownicy.setOgraniczenie("BRAK");
            uzytkownicy.setUprawnienie("USER");
            uzytkownicy.setUsuwanie(true);
            uzytkownicy.setIloscNieodczytanych(0);
            uzytkownicy.setData(LocalDate.now());
            entitymanager.persist(uzytkownicy);
            transaction.commit();
            transaction.begin();
            Ranking ranking = new Ranking();
            ranking.setUzytkownikRank(uzytkownicy);
            ranking.setWynik(0);
            ranking.setRozwiazane(0);
            ranking.setProby(0);
            ranking.setCzas(0);
            ranking.setId_rank(uzytkownicy.getId_uz());
            entitymanager.persist(ranking);
            transaction.commit();
            odp.put("Wiadomosc", "zarejestrowano, mozesz teraz sie zalogowac");
        } else {
            odp.put("Wiadomosc", "Taki uzytkownik juz istnieje.");
        }
        writer.write(odp.toString());
        writer.newLine();
        writer.flush();
        entitymanager.close();
    }

    public void sprawdzLogin(BufferedWriter writer, JSONObject json, JSONObject odp) throws IOException {
        String login = json.optString("Login");
        String haslo = json.optString("Haslo");
        EntityManager entitymanager = Manager.getFactory().createEntityManager();
        try {
            Query<Long> query = (Query<Long>) entitymanager.createQuery("SELECT u.id_uz FROM Uzytkownicy u WHERE u.nick = :nick");
            query.setParameter("nick", login);
            Long id = query.getSingleResult();
            Uzytkownicy uzytkownicy = entitymanager.find(Uzytkownicy.class, id);
            if (uzytkownicy.getOgraniczenie().equals("BAN")) {
                odp.put("Wynik", false);
                odp.put("Wiadomosc", "Twoje konto jest zbanowane. Kontakt mail@gmail.com");
            } else if (BCrypt.checkpw(haslo, uzytkownicy.getHaslo())) {
                odp.put("Wynik", true);
                odp.put("Id", id);
                EntityTransaction transaction = entitymanager.getTransaction();
                transaction.begin();
                uzytkownicy.beforeUpdate();
                uzytkownicy.setData(LocalDate.now());
                transaction.commit();
            } else {
                odp.put("Wynik", false);
                odp.put("Wiadomosc", "Haslo lub Login jest nieprawidlowy");
            }
        } catch (NoResultException e) {
            odp.put("Wynik", false);
            odp.put("Wiadomosc", "Haslo lub Login jest nieprawidlowy");
        } finally {
            writer.write(odp.toString());
            writer.newLine();
            writer.flush();
            entitymanager.close();
        }
    }

    public void dodajZgloszenie(BufferedWriter writer, JSONObject json, JSONObject odp) throws IOException {
        String tytul = json.optString("Tytul");
        Long ilosc1 = 0l, Id, ilosc2 = 0l;
        EntityManager entitymanager = Manager.getFactory().createEntityManager();
        try {
            Query<Long> query = (Query<Long>) entitymanager.createQuery("SELECT COUNT(u) FROM Zgloszenia u WHERE u.tytul = :tytul");
            query.setParameter("tytul", tytul);
            ilosc1 = query.getSingleResult();
            Query<Long> query2 = (Query<Long>) entitymanager.createQuery("SELECT COUNT(u) FROM Zadania u WHERE u.nazwa = :nazwa");
            query2.setParameter("nazwa", tytul);
            ilosc2 = query2.getSingleResult();

        } catch (NoResultException e) {

        }
        if (ilosc1 == 0 && ilosc2 == 0) {
            List<String> input = new ArrayList<>();
            List<String> ouput = new ArrayList<>();
            EntityTransaction transaction = entitymanager.getTransaction();
            transaction.begin();
            Zgloszenia zgloszenia = new Zgloszenia();
            Uzytkownicy uzytkownicy = entitymanager.find(Uzytkownicy.class, json.optLong("Id"));
            if (!uzytkownicy.getOgraniczenie().equals("BLOCK")) {
                zgloszenia.setUzytkownikZgl(uzytkownicy);
                zgloszenia.setTytul(tytul);
                zgloszenia.setOpis(json.optString("Opis"));
                zgloszenia.setLimitCzasu(Integer.parseInt(json.optString("Limit")));
                zgloszenia.setPunktowanyCzas(Integer.parseInt(json.optString("Czas")));
                zgloszenia.setData(LocalDate.now());
                zgloszenia.setZmienne(json.optString("Zmienne"));
                JSONArray inputTab = json.getJSONArray("Input");
                for (int i = 0; i < inputTab.length(); i++) {
                    input.add((String) inputTab.get(i));
                }
                JSONArray outputTab = json.getJSONArray("Output");
                for (int i = 0; i < outputTab.length(); i++) {
                    ouput.add((String) outputTab.get(i));
                }
                zgloszenia.setInput(input);
                zgloszenia.setOutput(ouput);
                entitymanager.persist(zgloszenia);
                transaction.commit();
                odp.put("Wynik", true);
                odp.put("Wiadomosc", "Udalo sie wyslac zadanie.");
            } else {
                odp.put("Wynik", false);
                odp.put("Wiadomosc", "Masz zablokowane wysylanie zadan.");
            }
        } else {
            odp.put("Wynik", false);
            odp.put("Wiadomosc", "Taki tytul zadania juz istnieje.");
        }
        writer.write(odp.toString());
        writer.newLine();
        writer.flush();
        entitymanager.close();
    }

    public void getZgloszenia(BufferedWriter writer,JSONObject json) throws IOException {
        EntityManager entitymanager = Manager.getFactory().createEntityManager();
        Query<Zgloszenia> query = (Query<Zgloszenia>) entitymanager.createQuery("FROM Zgloszenia");
        Long id=json.optLong("Id");
        Uzytkownicy uzytkownicy=entitymanager.find(Uzytkownicy.class,id);
        boolean admin=false;
        if(uzytkownicy.getUprawnienie().equals("ADMIN"))
        {
            admin=true;
        }
        List<Zgloszenia> lista = query.list();
        List<ZgloszeniaClass> listaWysylana = new ArrayList<>();
        for (Zgloszenia zgloszenia : lista) {
            if(!admin&&zgloszenia.getUzytkownikZgl().getId_uz()==id)
            {
                continue;
            }
            ZgloszeniaClass zgloszeniaClass = new ZgloszeniaClass();
            zgloszeniaClass.setId(zgloszenia.getId_zg());
            zgloszeniaClass.setNazwa(zgloszenia.getTytul());
            zgloszeniaClass.setId_g(zgloszenia.getUzytkownikZgl().getId_uz());
            zgloszeniaClass.setNazwa_g(zgloszenia.getUzytkownikZgl().getNick());
            zgloszeniaClass.setCzas(zgloszenia.getPunktowanyCzas());
            zgloszeniaClass.setLimit(zgloszenia.getLimitCzasu());
            zgloszeniaClass.setOpis(zgloszenia.getOpis());
            zgloszeniaClass.setData(zgloszenia.getData());
            zgloszeniaClass.setInput(zgloszenia.getInput());
            zgloszeniaClass.setOutput(zgloszenia.getOutput());
            zgloszeniaClass.setZmienne(zgloszenia.getZmienne());
            listaWysylana.add(zgloszeniaClass);
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String wynik = mapper.writeValueAsString(listaWysylana);
        writer.write(wynik);
        writer.newLine();
        writer.flush();
        entitymanager.close();

    }

    public void addZadanie(BufferedReader reader, BufferedWriter writer, JSONObject json, JSONObject odp) throws IOException {
        EntityManager entitymanager = Manager.getFactory().createEntityManager();
        Uzytkownicy uzytkownicy_mod = entitymanager.find(Uzytkownicy.class, json.optLong("Id"));
        Uzytkownicy uzytkownicy_gracz = entitymanager.find(Uzytkownicy.class, json.optLong("Id_g"));
        String jsonObject = reader.readLine();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        ZgloszeniaClass zgloszeniaClass = objectMapper.readValue(jsonObject, ZgloszeniaClass.class);
        Zgloszenia zgloszenia = entitymanager.find(Zgloszenia.class, zgloszeniaClass.getId());
        EntityTransaction transaction = entitymanager.getTransaction();
        if (uzytkownicy_mod.getUprawnienie().equals("USER")) {
            odp.put("Wynik", false);
            odp.put("Uprawnienia", false);
            writer.write(odp.toString());
            writer.newLine();
            writer.flush();
        } else if (zgloszenia == null) {
            odp.put("Wynik", false);
            odp.put("Uprawnienia", true);
            writer.write(odp.toString());
            writer.newLine();
            writer.flush();
        } else {
            transaction.begin();
            Zadania zadania = new Zadania();
            zadania.setNazwa(zgloszeniaClass.getNazwa());
            zadania.setOpis(zgloszeniaClass.getOpis());
            zadania.setLimitCzasu(zgloszeniaClass.getLimit());
            zadania.setData(LocalDate.now());
            zadania.setCzas(zgloszeniaClass.getCzas());
            zadania.setInput(zgloszeniaClass.getInput());
            zadania.setOutput(zgloszeniaClass.getOutput());
            zadania.setZmienne(zgloszeniaClass.getZmienne());
            zadania.setPodejscia(0);
            zadania.setUkonczone(0);
            zadania.setUzytkownikZad(uzytkownicy_gracz);
            entitymanager.persist(zadania);
            transaction.commit();
            Query<Long> query2 = (Query<Long>) entitymanager.createQuery("SELECT r.id FROM Ranking r WHERE  r.uzytkownikRank = :uzytkownikRank");
            query2.setParameter("uzytkownikRank", uzytkownicy_gracz);
            Long idR = query2.getSingleResult();
            Ranking ranking = entitymanager.find(Ranking.class, idR);
            transaction.begin();
            ranking.setPrzeslane(ranking.getPrzeslane() + 1);
            transaction.commit();
            addPocztaandDeleteZgloszenie(0, zgloszeniaClass, transaction, uzytkownicy_mod, uzytkownicy_gracz, json, odp, entitymanager, writer, zgloszenia, zadania.getId_zad());
        }
        entitymanager.close();
    }


    public void rejectZadanie(BufferedReader reader, BufferedWriter writer, JSONObject json, JSONObject odp) throws IOException {
        EntityManager entitymanager = Manager.getFactory().createEntityManager();
        Uzytkownicy uzytkownicy_mod = entitymanager.find(Uzytkownicy.class, json.optLong("Id"));
        Uzytkownicy uzytkownicy_gracz = entitymanager.find(Uzytkownicy.class, json.optLong("Id_g"));
        String jsonObject = reader.readLine();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        ZgloszeniaClass zgloszeniaClass = objectMapper.readValue(jsonObject, ZgloszeniaClass.class);
        Zgloszenia zgloszenia = entitymanager.find(Zgloszenia.class, zgloszeniaClass.getId());
        EntityTransaction transaction = entitymanager.getTransaction();
        if (uzytkownicy_mod.getUprawnienie().equals("USER")) {
            odp.put("Wynik", false);
            odp.put("Uprawnienia", false);
            writer.write(odp.toString());
            writer.newLine();
            writer.flush();
        } else if (zgloszenia == null) {
            odp.put("Wynik", false);
            odp.put("Uprawnienia", true);
            writer.write(odp.toString());
            writer.newLine();
            writer.flush();
        } else {
            addPocztaandDeleteZgloszenie(1, zgloszeniaClass, transaction, uzytkownicy_mod, uzytkownicy_gracz, json, odp, entitymanager, writer, zgloszenia, 0l);
        }
        entitymanager.close();
    }

    public void blockZadanie(BufferedReader reader, BufferedWriter writer, JSONObject json, JSONObject odp) throws IOException {
        EntityManager entitymanager = Manager.getFactory().createEntityManager();
        Uzytkownicy uzytkownicy_mod = entitymanager.find(Uzytkownicy.class, json.optLong("Id"));
        Uzytkownicy uzytkownicy_gracz = entitymanager.find(Uzytkownicy.class, json.optLong("Id_g"));
        String jsonObject = reader.readLine();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        ZgloszeniaClass zgloszeniaClass = objectMapper.readValue(jsonObject, ZgloszeniaClass.class);
        Zgloszenia zgloszenia = entitymanager.find(Zgloszenia.class, zgloszeniaClass.getId());
        EntityTransaction transaction = entitymanager.getTransaction();
        if (uzytkownicy_mod.getUprawnienie().equals("USER")) {
            odp.put("Wynik", false);
            odp.put("Uprawnienia", false);
            writer.write(odp.toString());
            writer.newLine();
            writer.flush();
        } else if (zgloszenia == null) {
            odp.put("Wynik", false);
            odp.put("Uprawnienia", true);
            writer.write(odp.toString());
            writer.newLine();
            writer.flush();
        } else {
            transaction.begin();
            uzytkownicy_gracz.setOgraniczenie("BLOCK");
            transaction.commit();
            addPocztaandDeleteZgloszenie(2, zgloszeniaClass, transaction, uzytkownicy_mod, uzytkownicy_gracz, json, odp, entitymanager, writer, zgloszenia, 0l);
        }
        entitymanager.close();

    }


    public void addPocztaandDeleteZgloszenie(int tryb, ZgloszeniaClass zgloszeniaClass, EntityTransaction transaction, Uzytkownicy uzytkownicy_mod, Uzytkownicy uzytkownicy_gracz, JSONObject json, JSONObject odp, EntityManager entitymanager, BufferedWriter writer, Zgloszenia zgloszenia, Long id_zad) throws IOException {
        transaction.begin();
        uzytkownicy_gracz.setIloscNieodczytanych(uzytkownicy_gracz.getIloscNieodczytanych() + 1);
        Poczta poczta = new Poczta();
        if (tryb == 0) {
            poczta.setTytul(zgloszeniaClass.getNazwa() + " - ZATWIERDZONO");
            poczta.setWiadomosc("Twoje zadanie zostalo przyjete:\n" + json.optString("Wiadomosc") + "\nPozdrawiam " + uzytkownicy_mod.getNick());

        } else if (tryb == 1) {
            poczta.setTytul(zgloszeniaClass.getNazwa() + " - ODRZUCONO");
            poczta.setWiadomosc("Twoje zadanie zostalo odrzucone:\n" + json.optString("Wiadomosc") + "\nPozdrawiam " + uzytkownicy_mod.getNick());

        } else {
            poczta.setTytul(zgloszeniaClass.getNazwa() + " - ZABLOKOWONO");
            poczta.setWiadomosc("Wysylanie zadan zostalo ci zablokowane:\n" + json.optString("Wiadomosc") + "\nPozdrawiam " + uzytkownicy_mod.getNick());

        }
        poczta.setDoUzytkownik(uzytkownicy_gracz);
        poczta.setData(LocalDate.now());
        entitymanager.persist(poczta);
        transaction.commit();
        if (uzytkownicy_mod.getUprawnienie().equals("MOD")) {
            transaction.begin();
            Weryfikacja weryfikacja = new Weryfikacja();
            weryfikacja.setId_zad(id_zad);
            weryfikacja.setNazwa(zgloszeniaClass.getNazwa());
            weryfikacja.setOpis(zgloszeniaClass.getOpis());
            weryfikacja.setLimitCzasu(zgloszeniaClass.getLimit());
            weryfikacja.setCzas(zgloszeniaClass.getCzas());
            weryfikacja.setInput(zgloszeniaClass.getInput());
            weryfikacja.setOutput(zgloszeniaClass.getOutput());
            weryfikacja.setWiadomosc(json.optString("Wiadomosc"));
            weryfikacja.setUzytkownikZad(uzytkownicy_gracz);
            weryfikacja.setZmienne(zgloszenia.getZmienne());
            weryfikacja.setUzytkownikMod(uzytkownicy_mod);
            weryfikacja.setData(LocalDate.now());
            if (tryb == 0) {
                weryfikacja.setAkcja("ZATWIERDZONO");
            } else if (tryb == 1) {
                weryfikacja.setAkcja("ODRZUCONO");
            } else {
                weryfikacja.setAkcja("ZABLOKOWANO");
            }
            entitymanager.persist(weryfikacja);
            transaction.commit();
        }
        transaction.begin();
        entitymanager.remove(zgloszenia);
        transaction.commit();
        odp.put("Wynik", true);
        writer.write(odp.toString());
        writer.newLine();
        writer.flush();
    }

    public void getPoczta(BufferedWriter writer, JSONObject json) throws IOException {
        EntityManager entitymanager = Manager.getFactory().createEntityManager();
        Uzytkownicy uzytkownicy = entitymanager.find(Uzytkownicy.class, json.optLong("Id"));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Wynik", uzytkownicy.isUsuwanie());
        writer.write(jsonObject.toString());
        writer.newLine();
        writer.flush();
        List<Poczta> lista = null;
        try {
            Query<Poczta> query = (Query<Poczta>) entitymanager.createQuery("FROM Poczta p WHERE p.doUzytkownik=:id");
            query.setParameter("id", uzytkownicy);
            lista = query.list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<PocztaClass> listaWysylana = new ArrayList<>();
        for (Poczta poczta : lista) {
            PocztaClass pocztaClass = new PocztaClass();
            pocztaClass.setId(poczta.getId_wiad());
            pocztaClass.setTytul(poczta.getTytul());
            pocztaClass.setWiadomosc(poczta.getWiadomosc());
            pocztaClass.setData(poczta.getData());
            pocztaClass.setId_g(poczta.getDoUzytkownik().getId_uz());
            listaWysylana.add(pocztaClass);
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String wynik = mapper.writeValueAsString(listaWysylana);
        writer.write(wynik);
        writer.newLine();
        writer.flush();
        entitymanager.close();
    }

    public void usunWiadomosc(JSONObject json) throws IOException {
        EntityManager entitymanager = Manager.getFactory().createEntityManager();
        Poczta poczta = entitymanager.find(Poczta.class, json.optLong("Id"));
        if (poczta != null) {
            EntityTransaction transaction = entitymanager.getTransaction();
            transaction.begin();
            entitymanager.remove(poczta);
            transaction.commit();
        }
        entitymanager.close();
    }

    public void changeTryb(JSONObject json) throws IOException {
        EntityManager entitymanager = Manager.getFactory().createEntityManager();
        Uzytkownicy uzytkownicy = entitymanager.find(Uzytkownicy.class, json.optLong("Id"));
        EntityTransaction transaction = entitymanager.getTransaction();
        transaction.begin();
        uzytkownicy.setUsuwanie(json.getBoolean("Tryb"));
        transaction.commit();
        entitymanager.close();
    }

    public void getZadania(BufferedWriter writer, JSONObject json) throws IOException {
        EntityManager entitymanager = Manager.getFactory().createEntityManager();
        int tryb = json.optInt("Tryb");
        Query<Zadania> query = null;
        if (tryb == 0) {
            query = (Query<Zadania>) entitymanager.createQuery("FROM Zadania");
        } else if (tryb == 1) {
            query = (Query<Zadania>) entitymanager.createQuery("FROM Zadania");
            query.setFirstResult(json.optInt("Pozycja") - 1);
            query.setMaxResults(1);
        } else if (tryb == 2) {
            query = (Query<Zadania>) entitymanager.createQuery("FROM Zadania z WHERE LOWER(z.nazwa) LIKE LOWER(:pattern)");
            query.setParameter("pattern", json.optString("Nazwa") + "%");
        }
        Uzytkownicy uzytkownicy = entitymanager.find(Uzytkownicy.class, json.optLong("Id"));
        List<Zadania> lista = query.list();
        List<ZadaniaClass> listaWysylana = new ArrayList<>();
        Long ilosc;
        boolean admin = false;
        if (uzytkownicy.getUprawnienie().equals("ADMIN")) {
            admin = true;
        }
        for (Zadania zadania : lista) {
            ilosc = 0l;
            if (!admin) {
                try {
                    Query<Long> query2 = (Query<Long>) entitymanager.createQuery("SELECT COUNT(w) FROM Wyslane w WHERE w.id_zad = :id_zad AND w.uzytkownikWys = :uzytkownikWys AND w.zrobione= true");
                    query2.setParameter("id_zad", zadania.getId_zad());
                    query2.setParameter("uzytkownikWys", uzytkownicy);
                    ilosc = query2.getSingleResult();

                } catch (NoResultException e) {
                }
            }
            if (ilosc != 0) {
                continue;
            }
            ZadaniaClass zadaniaClass = new ZadaniaClass();
            zadaniaClass.setId_zad(zadania.getId_zad());
            zadaniaClass.setNazwa(zadania.getNazwa());
            zadaniaClass.setOpis(zadania.getOpis());
            zadaniaClass.setData(zadania.getData());
            zadaniaClass.setLimitCzasu(zadania.getLimitCzasu());
            zadaniaClass.setCzas(zadania.getCzas());
            zadaniaClass.setId_gr(zadania.getUzytkownikZad().getId_uz());
            zadaniaClass.setPodejscia(zadania.getPodejscia());
            zadaniaClass.setUkonczone(zadania.getUkonczone());
            zadaniaClass.setInput(zadania.getInput());
            zadaniaClass.setOutput(zadania.getOutput());
            zadaniaClass.setZmienne(zadania.getZmienne());
            listaWysylana.add(zadaniaClass);
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String wynik = mapper.writeValueAsString(listaWysylana);
        writer.write(wynik);
        writer.newLine();
        writer.flush();
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("Uprawnienie",uzytkownicy.getUprawnienie());
        writer.write(jsonObject.toString());
        writer.newLine();
        writer.flush();
        entitymanager.close();
    }

    public void usunZadanie(BufferedWriter writer, JSONObject json) throws IOException {
        EntityManager entitymanager = Manager.getFactory().createEntityManager();
        Zadania zadania = entitymanager.find(Zadania.class, json.optLong("Id_zad"));
        Uzytkownicy uzytkownicy = entitymanager.find(Uzytkownicy.class, json.optLong("Id_gr"));
        JSONObject odp = new JSONObject();
        if (!uzytkownicy.getUprawnienie().equals("ADMIN")) {
            odp.put("Wynik", false);
            odp.put("Wiadomosc", "Nie masz wystarczajacych uprawnien");
        } else {
            if (zadania == null) {
                odp.put("Wynik", false);
                odp.put("Wiadomosc", "Nie ma takiego zadania");
            } else {
                EntityTransaction transaction = entitymanager.getTransaction();
                transaction.begin();
                zadania.getUzytkownikZad().setIloscNieodczytanych(zadania.getUzytkownikZad().getIloscNieodczytanych()+1);
                Poczta poczta = new Poczta();
                poczta.setTytul(zadania.getNazwa() + " - USUNIETO");
                poczta.setWiadomosc("Twoje zadanie zostalo usuniete:\n" + json.optString("Wiadomosc") + "\nPozdrawiam " + uzytkownicy.getNick());
                poczta.setDoUzytkownik(zadania.getUzytkownikZad());
                poczta.setData(LocalDate.now());
                entitymanager.persist(poczta);
                entitymanager.remove(zadania);
                transaction.commit();
                odp.put("Wynik", true);
            }
        }
        writer.write(odp.toString());
        writer.newLine();
        writer.flush();
        entitymanager.close();

    }

    public void isZgloszenie(BufferedWriter writer, JSONObject json) throws IOException {
        EntityManager entitymanager = Manager.getFactory().createEntityManager();
        Zgloszenia zgloszenia = entitymanager.find(Zgloszenia.class, json.optLong("Id"));
        JSONObject odp = new JSONObject();
        if (zgloszenia != null) {
            odp.put("Wynik", true);
        } else {
            odp.put("Wynik", false);
        }
        writer.write(odp.toString());
        writer.newLine();
        writer.flush();
        entitymanager.close();
    }

    public void sprawdzZadanie(BufferedWriter writer, JSONObject json) throws InterruptedException, IOException {
        EntityManager entitymanager = Manager.getFactory().createEntityManager();
        EntityTransaction transaction = entitymanager.getTransaction();
        Uzytkownicy uzytkownicy = entitymanager.find(Uzytkownicy.class, json.optLong("Id_gr"));
        Zadania zadania = entitymanager.find(Zadania.class, json.optLong("Id_zad"));
        JSONObject jsonObject = new JSONObject();
        boolean admin = false;
        if (uzytkownicy.getUprawnienie().equals("ADMIN")) {
            admin = true;
        }
        if (zadania == null) {
            jsonObject.put("Istnieje", false);
            writer.write(jsonObject.toString());
            writer.newLine();
            writer.flush();
        } else {
            Long ilosc = 0l;
            if (!admin) {
                try {
                    Query<Long> query = (Query<Long>) entitymanager.createQuery("SELECT COUNT(w) FROM Wyslane w WHERE w.id_zad = :id_zad AND w.uzytkownikWys = :uzytkownikWys AND w.zrobione=true");
                    query.setParameter("id_zad", zadania.getId_zad());
                    query.setParameter("uzytkownikWys", uzytkownicy);
                    ilosc = query.getSingleResult();
                } catch (NoResultException e) {

                }
            }

            if (ilosc == 1) {
                jsonObject.put("Istnieje", true);
                jsonObject.put("Robione", true);
                writer.write(jsonObject.toString());
                writer.newLine();
                writer.flush();
            } else {
                ilosc = 0l;
                Long id = null;
                try {
                    Query<Long> query = (Query<Long>) entitymanager.createQuery("SELECT w.id_wys FROM Wyslane w WHERE w.id_zad = :id_zad AND w.uzytkownikWys = :uzytkownikWys");
                    query.setParameter("id_zad", zadania.getId_zad());
                    query.setParameter("uzytkownikWys", uzytkownicy);
                    id = query.getSingleResult();
                    ilosc = 1l;
                } catch (NoResultException e) {
                }
                if (ilosc == 0) {
                    transaction.begin();
                    zadania.setPodejscia(zadania.getPodejscia() + 1);
                    transaction.commit();
                }
                AtomicInteger itest = new AtomicInteger(0);
                AtomicInteger negatywne = new AtomicInteger(0);
                AtomicBoolean akceptuje = new AtomicBoolean(true);
                float[] czas = {0};
                StringBuilder wynik = new StringBuilder();
                if (zadania.getInput().size() == 0) {
                    for (int i = 0; i < zadania.getOutput().size(); i++) {
                        testInput(json, zadania, "", akceptuje, negatywne, itest, wynik, czas);
                    }
                } else {
                    for (String input : zadania.getInput()) {
                        testInput(json, zadania, input, akceptuje, negatywne, itest, wynik, czas);
                    }
                }
                wynik.append("[" + String.valueOf(itest.get() - negatywne.get()) + "/" + String.valueOf(itest.get()) + "]\n");

                int wynikall = 0;
                float wczas = (float) json.optInt("Czas") / (float) zadania.getCzas();
                int ilProb = json.optInt("Proby");
                float srCzas = czas[0] / ((float) itest.get() * zadania.getLimitCzasu());
                transaction.begin();
                Ranking ranking = entitymanager.find(Ranking.class, json.optLong("Id_gr"));
                Wyslane wyslane;
                if (id == null) {
                    wyslane = new Wyslane();
                } else {
                    wyslane = entitymanager.find(Wyslane.class, id);
                }
                if (akceptuje.get() == true) {
                    int pktCzas;
                    int pktLimit;
                    int pktProby;
                    jsonObject.put("Wynik", true);
                    wynik.append("- SUKCES");
                    zadania.setUkonczone(zadania.getUkonczone() + 1);
                    if (wczas >= 0 && wczas < 1 / 3.0) {
                        pktCzas = 3;
                    } else if (wczas >= 1 / 3.0 && wczas < 2 / 3.0) {
                        pktCzas = 2;
                    } else if (wczas >= 2 / 3.0 && wczas <= 1) {
                        pktCzas = 1;
                    } else {
                        pktCzas = 0;
                    }
                    if (ilProb == 1) {
                        pktProby = 3;
                    } else if (ilProb == 2) {
                        pktProby = 2;
                    } else if (ilProb == 3) {
                        pktProby = 1;
                    } else {
                        pktProby = 0;
                    }
                    if (srCzas >= 0 && srCzas < 1 / 4.0) {
                        pktLimit = 3;
                    } else if (srCzas >= 1 / 4.0 && srCzas < 2 / 4.0) {
                        pktLimit = 2;
                    } else if (srCzas >= 2 / 4.0 && srCzas <= 3 / 4.0) {
                        pktLimit = 1;
                    } else {
                        pktLimit = 0;
                    }
                    wynikall = 5 + pktCzas + pktLimit + pktProby;
                    wyslane.setZrobione(true);
                    ranking.setRozwiazane(ranking.getRozwiazane() + 1);
                    ranking.setWynik(ranking.getWynik() + wynikall);

                } else {
                    wynik.append("- BLAD");
                    jsonObject.put("Wynik", false);
                    wyslane.setZrobione(false);
                }
                float czash = (float) (json.optInt("Czas") / 60.0);
                ranking.setProby(ranking.getProby() + ilProb);
                ranking.setCzas(ranking.getCzas() + czash);
                wyslane.setId_zad(json.optLong("Id_zad"));
                wyslane.setUzytkownikWys(uzytkownicy);
                wyslane.setWynik(wynikall);
                wyslane.setNazwa(zadania.getNazwa());
                wyslane.setCzasUruchomienia(srCzas);
                wyslane.setCzas(czash);
                wyslane.setData(LocalDate.now());
                wyslane.setPubliczny(json.getBoolean("Publiczny"));
                wyslane.setProby(ilProb);
                wyslane.setKod(json.optString("Kod"));
                wyslane.setOutput(wynik.toString());
                if (id == null) {
                    entitymanager.persist(wyslane);
                }
                transaction.commit();
                jsonObject.put("Istnieje", true);
                jsonObject.put("Robione", false);
                jsonObject.put("Wiadomosc", wynik.toString());
                writer.write(jsonObject.toString());
                writer.newLine();
                writer.flush();
            }
        }
        entitymanager.close();

    }

    public void isZadanie(BufferedWriter writer, JSONObject json) throws IOException {
        EntityManager entitymanager = Manager.getFactory().createEntityManager();
        Zadania zadania = entitymanager.find(Zadania.class, json.optLong("Id_zad"));
        Uzytkownicy uzytkownicy = entitymanager.find(Uzytkownicy.class, json.optLong("Id"));
        Wyslane wyslane = null;
        JSONObject jsonObject = new JSONObject();
        Long id = null;
        int il = 0;
        try {
            Query<Long> query = (Query<Long>) entitymanager.createQuery("SELECT w.id_wys FROM Wyslane w WHERE w.id_zad = :id_zad AND w.uzytkownikWys = :uzytkownikWys");
            query.setParameter("id_zad", zadania.getId_zad());
            query.setParameter("uzytkownikWys", uzytkownicy);
            id = query.getSingleResult();
            wyslane = entitymanager.find(Wyslane.class, id);
            il = 1;
        } catch (Exception e) {
        }
        if (zadania != null) {
            jsonObject.put("IstniejeZadanie", true);
            jsonObject.put("Nick", zadania.getUzytkownikZad().getNick());
            if (il == 0) {
                jsonObject.put("IstniejeWyslane", false);

            } else {
                if (wyslane.isZrobione()) {
                    jsonObject.put("IstniejeWyslane", true);
                    jsonObject.put("Zrobione", true);
                } else {
                    jsonObject.put("IstniejeWyslane", true);
                    jsonObject.put("Zrobione", false);
                    jsonObject.put("Kod", wyslane.getKod());
                    jsonObject.put("Czas", wyslane.getCzas());
                    jsonObject.put("Proby", wyslane.getProby());
                    jsonObject.put("Publiczny", wyslane.isPubliczny());
                    jsonObject.put("Output", wyslane.getOutput());
                }
            }

        } else {
            jsonObject.put("IstniejeZadanie", false);
        }
        writer.write(jsonObject.toString());
        writer.newLine();
        writer.flush();
        entitymanager.close();

    }

    public void testInput(JSONObject json, Zadania zadania, String input, AtomicBoolean akceptuje, AtomicInteger negatywne, AtomicInteger itest, StringBuilder wynik, float[] czas) throws InterruptedException {
        AtomicBoolean limitCzasu = new AtomicBoolean(true);
        String kod = json.optString("Kod");
        kod += "\nwynik=" + zadania.getNazwa() + "(" + input + ")";
        kod += "\nif isinstance(wynik, str):";
        kod += "\n\tprint('\"' + wynik + '\"')";
        kod += "\nelse:";
        kod += "\n\tprint(wynik)";
        Test test = new Test(kod, limitCzasu, zadania, negatywne, akceptuje, wynik, itest, czas);
        test.start();
        Thread.sleep(zadania.getLimitCzasu());
        if (limitCzasu.get() == true) {
            test.interrupt();
            akceptuje.set(false);
            wynik.append("Test " + String.valueOf(itest.get() + 1) + " [BLAD]\n\tPrzekroczono limit czasu.\n");
            negatywne.incrementAndGet();

        }
        itest.incrementAndGet();
    }

    public void getRanking(BufferedWriter writer, JSONObject json) throws IOException {
        EntityManager entitymanager = Manager.getFactory().createEntityManager();
        Query<Ranking> query = null;
        Boolean istnieje = false;
        if (json.optInt("Tryb") == 0) {
            query = (Query<Ranking>) entitymanager.createQuery("FROM Ranking r ORDER BY r.wynik DESC");
        } else if (json.optInt("Tryb") == 1) {
            query = (Query<Ranking>) entitymanager.createQuery("FROM Ranking r ORDER BY r.wynik DESC");
            query.setFirstResult(json.optInt("Pozycja") - 1);
            query.setMaxResults(1);
        } else if (json.optInt("Tryb") == 2) {
            query = (Query<Ranking>) entitymanager.createQuery("FROM Ranking r WHERE LOWER(r.uzytkownikRank.nick) LIKE LOWER(:pattern) ORDER BY r.wynik DESC ");
            query.setParameter("pattern", json.optString("Nick") + "%");
        }
        List<Ranking> lista = query.list();
        List<RankingClass> listaWysylana = new ArrayList<>();
        String wynik2 = null;
        int index = 0;
        int pozycja = 0;
        for (Ranking ranking : lista) {
            RankingClass rankingClass = new RankingClass();
            rankingClass.setId_uz(ranking.getUzytkownikRank().getId_uz());
            rankingClass.setNick(ranking.getUzytkownikRank().getNick());
            rankingClass.setProby(ranking.getProby());
            rankingClass.setPrzeslane(ranking.getPrzeslane());
            rankingClass.setRozwiazane(ranking.getRozwiazane());
            rankingClass.setWynik(ranking.getWynik());
            rankingClass.setCzas(ranking.getCzas());
            listaWysylana.add(rankingClass);
            if (rankingClass.getId_uz() == json.optLong("Id")) {
                pozycja = index;
                istnieje = true;

            }
            index++;
        }
        if (istnieje) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Istnieje", true);
            jsonObject.put("Pozycja", pozycja);
            writer.write(jsonObject.toString());
            writer.newLine();
            writer.flush();
        } else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Istnieje", false);
            writer.write(jsonObject.toString());
            writer.newLine();
            writer.flush();
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String wynik = mapper.writeValueAsString(listaWysylana);
        writer.write(wynik);
        writer.newLine();
        writer.flush();
        entitymanager.close();
    }

    public void przeczytanaWiadomosc(JSONObject json) throws IOException {
        EntityManager entitymanager = Manager.getFactory().createEntityManager();
        Uzytkownicy uzytkownicy = entitymanager.find(Uzytkownicy.class, json.optLong("Id"));
        EntityTransaction transaction = entitymanager.getTransaction();
        transaction.begin();
        uzytkownicy.setIloscNieodczytanych(uzytkownicy.getIloscNieodczytanych() - 1);
        transaction.commit();
        entitymanager.close();
    }

    public void getMenu(BufferedWriter writer, JSONObject json) throws IOException {
        EntityManager entitymanager = Manager.getFactory().createEntityManager();
        Uzytkownicy uzytkownicy = entitymanager.find(Uzytkownicy.class, json.optLong("Id"));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Ilosc", uzytkownicy.getIloscNieodczytanych());
        jsonObject.put("Uprawnienie", uzytkownicy.getUprawnienie());
        jsonObject.put("Ograniczenie", uzytkownicy.getOgraniczenie());
        writer.write(jsonObject.toString());
        writer.newLine();
        writer.flush();
        entitymanager.close();
    }

    public void getWyslane(BufferedWriter writer, JSONObject json) throws IOException {
        EntityManager entitymanager = Manager.getFactory().createEntityManager();
        Query<Wyslane> query = (Query<Wyslane>) entitymanager.createQuery("FROM Wyslane");
        List<Wyslane> lista = query.list();
        List<WyslaneClass> listaWysylana = new ArrayList<>();
        Long ilosc;
        Boolean moje = json.optBoolean("Moje");
        Long id = json.optLong("Id");
        boolean jest;
        for (Wyslane wyslane : lista) {
            jest = false;
            if (moje) {
                if (wyslane.getUzytkownikWys().getId_uz() == id) {
                    jest = true;
                }

            } else {
                if (wyslane.getUzytkownikWys().getId_uz() == id && wyslane.isPubliczny() == true) {
                    jest = true;
                }
            }
            if (jest) {
                WyslaneClass wyslaneClass = new WyslaneClass();
                wyslaneClass.setId_zad(wyslane.getId_zad());
                wyslaneClass.setNazwa(wyslane.getNazwa());
                wyslaneClass.setZrobione(wyslane.isZrobione());
                wyslaneClass.setPubliczny(wyslane.isPubliczny());
                wyslaneClass.setCzas(wyslane.getCzas());
                wyslaneClass.setData(wyslane.getData());
                wyslaneClass.setKod(wyslane.getKod());
                wyslaneClass.setOutput(wyslane.getOutput());
                wyslaneClass.setProby(wyslane.getProby());
                wyslaneClass.setCzasUruchomienia(wyslane.getCzasUruchomienia());
                wyslaneClass.setWynik(wyslane.getWynik());
                wyslaneClass.setId_uz(wyslane.getUzytkownikWys().getId_uz());
                wyslaneClass.setId_wys(wyslane.getId_wys());
                listaWysylana.add(wyslaneClass);
            }
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String wynik = mapper.writeValueAsString(listaWysylana);
        writer.write(wynik);
        writer.newLine();
        writer.flush();
        entitymanager.close();
    }

    public void changeWidocznosc(BufferedWriter writer, JSONObject json) throws IOException {
        EntityManager entitymanager = Manager.getFactory().createEntityManager();
        Wyslane wyslane = entitymanager.find(Wyslane.class, json.optLong("Id_wys"));
        EntityTransaction transaction = entitymanager.getTransaction();
        JSONObject jsonObject = new JSONObject();
        if (wyslane.getUzytkownikWys().getId_uz() != json.optLong("Id_gr")) {
            jsonObject.put("Wynik", false);

        } else {
            transaction.begin();
            if (json.optBoolean("Publiczny")) {
                wyslane.setPubliczny(true);
            } else {
                wyslane.setPubliczny(false);
            }
            transaction.commit();
            entitymanager.close();
            jsonObject.put("Wynik", true);
        }
        writer.write(jsonObject.toString());
        writer.newLine();
        writer.flush();
        entitymanager.close();
    }

    public void getUzytkownicy(BufferedWriter writer, JSONObject json) throws IOException {
        EntityManager entitymanager = Manager.getFactory().createEntityManager();
        int tryb = json.optInt("Tryb");
        Query<Uzytkownicy> query = null;
        if (tryb == 0) {
            query = (Query<Uzytkownicy>) entitymanager.createQuery("FROM Uzytkownicy");
        } else if (tryb == 1) {
            query = (Query<Uzytkownicy>) entitymanager.createQuery("FROM Uzytkownicy");
            query.setFirstResult(json.optInt("Pozycja") - 1);
            query.setMaxResults(1);
        } else if (tryb == 2) {
            query = (Query<Uzytkownicy>) entitymanager.createQuery("FROM Uzytkownicy u WHERE LOWER(u.nick) LIKE LOWER(:pattern)");
            query.setParameter("pattern", json.optString("Nick") + "%");
        }
        List<Uzytkownicy> lista = query.list();
        List<UzytkownicyClass> listaWysylana = new ArrayList<>();
        for (Uzytkownicy uzytkownicy : lista) {
            UzytkownicyClass uzytkownicyClass = new UzytkownicyClass();
            uzytkownicyClass.setId_uz(uzytkownicy.getId_uz());
            uzytkownicyClass.setNick(uzytkownicy.getNick());
            uzytkownicyClass.setOgraniczenie(uzytkownicy.getOgraniczenie());
            uzytkownicyClass.setUprawnienie(uzytkownicy.getUprawnienie());
            uzytkownicyClass.setData(uzytkownicy.getData());
            listaWysylana.add(uzytkownicyClass);
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String wynik = mapper.writeValueAsString(listaWysylana);
        writer.write(wynik);
        writer.newLine();
        writer.flush();
        entitymanager.close();
    }

    public void changeUzytkownik(BufferedWriter writer, JSONObject json) throws IOException {
        EntityManager entitymanager = Manager.getFactory().createEntityManager();
        Uzytkownicy uzytkownicy_gracz = entitymanager.find(Uzytkownicy.class, json.optLong("Id_gr"));
        Uzytkownicy uzytkownicy_admin = entitymanager.find(Uzytkownicy.class, json.optLong("Id_ad"));
        JSONObject jsonObject = new JSONObject();
        EntityTransaction transaction = entitymanager.getTransaction();
        if (!uzytkownicy_admin.getUprawnienie().equals("ADMIN")) {
            jsonObject.put("Wynik", false);
        } else {
            String typ = json.optString("Typ");
            transaction.begin();
            uzytkownicy_gracz.setIloscNieodczytanych(uzytkownicy_gracz.getIloscNieodczytanych() + 1);
            Poczta poczta = new Poczta();
            if (typ.equals("zbanuj")) {
                uzytkownicy_gracz.setOgraniczenie("BAN");
                poczta.setTytul("BAN");
                poczta.setWiadomosc("Twoje konto zostalo zbanowane:\n" + json.optString("Wiadomosc") + "\nPozdrawiam " + uzytkownicy_admin.getNick());
            } else if (typ.equals("odbanuj")) {
                uzytkownicy_gracz.setOgraniczenie("BRAK");
                poczta.setTytul("UNBAN");
                poczta.setWiadomosc("Twoje konto zostalo odbanowane:\n" + json.optString("Wiadomosc") + "\nPozdrawiam " + uzytkownicy_admin.getNick());
            } else if (typ.equals("zablokuj")) {
                uzytkownicy_gracz.setOgraniczenie("BLOCK");
                poczta.setTytul("BLOCK");
                poczta.setWiadomosc("Twoje konto zostalo zablokowane:\n" + json.optString("Wiadomosc") + "\nPozdrawiam " + uzytkownicy_admin.getNick());
            } else if (typ.equals("odblokuj")) {
                uzytkownicy_gracz.setOgraniczenie("BRAK");
                poczta.setTytul("UNBLOCK");
                poczta.setWiadomosc("Twoje konto zostalo odblokowane:\n" + json.optString("Wiadomosc") + "\nPozdrawiam " + uzytkownicy_admin.getNick());
            } else if (typ.equals("dajMod")) {
                uzytkownicy_gracz.setUprawnienie("MOD");
                poczta.setTytul("MOD");
                poczta.setWiadomosc("Gratulacje, zyskales uprawnienie Moderatora:\n" + json.optString("Wiadomosc") + "\nPozdrawiam " + uzytkownicy_admin.getNick());
            } else if (typ.equals("odbierzMod")) {
                uzytkownicy_gracz.setUprawnienie("USER");
                poczta.setTytul("UNMOD");
                poczta.setWiadomosc("Twoje uprawninie Moderatora zostalo cofniete:\n" + json.optString("Wiadomosc") + "\nPozdrawiam " + uzytkownicy_admin.getNick());
            }
            poczta.setDoUzytkownik(uzytkownicy_gracz);
            poczta.setData(LocalDate.now());
            entitymanager.persist(poczta);
            transaction.commit();
            jsonObject.put("Wynik", true);
        }
        writer.write(jsonObject.toString());
        writer.newLine();
        writer.flush();
        entitymanager.close();
    }

    public void getWeryfikacja(BufferedWriter writer) throws IOException {
        EntityManager entitymanager = Manager.getFactory().createEntityManager();
        Query<Weryfikacja> query = (Query<Weryfikacja>) entitymanager.createQuery("FROM Weryfikacja");
        List<Weryfikacja> lista = query.list();
        List<WeryfikacjaClass> listaWysylana = new ArrayList<>();
        for (Weryfikacja weryfikacja : lista) {
            WeryfikacjaClass weryfikacjaClass = new WeryfikacjaClass();
            weryfikacjaClass.setId(weryfikacja.getId());
            weryfikacjaClass.setId_mod(weryfikacja.getUzytkownikMod().getId_uz());
            weryfikacjaClass.setNick_mod(weryfikacja.getUzytkownikMod().getNick());
            weryfikacjaClass.setId_uz(weryfikacja.getUzytkownikZad().getId_uz());
            weryfikacjaClass.setNick_uz(weryfikacja.getUzytkownikZad().getNick());
            weryfikacjaClass.setNazwa(weryfikacja.getNazwa());
            weryfikacjaClass.setInput(weryfikacja.getInput());
            weryfikacjaClass.setOutput(weryfikacja.getOutput());
            weryfikacjaClass.setOpis(weryfikacja.getOpis());
            weryfikacjaClass.setAkcja(weryfikacja.getAkcja());
            weryfikacjaClass.setCzas(weryfikacja.getCzas());
            weryfikacjaClass.setData(weryfikacja.getData());
            weryfikacjaClass.setWiadomosc(weryfikacja.getWiadomosc());
            weryfikacjaClass.setLimitCzasu(weryfikacja.getLimitCzasu());
            weryfikacjaClass.setZmienne(weryfikacja.getZmienne());
            weryfikacjaClass.setId_zad(weryfikacja.getId_zad());
            listaWysylana.add(weryfikacjaClass);
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String wynik = mapper.writeValueAsString(listaWysylana);
        writer.write(wynik);
        writer.newLine();
        writer.flush();
        entitymanager.close();
    }

    public void changeWeryfikacja(BufferedWriter writer, JSONObject json) throws IOException {
        EntityManager entitymanager = Manager.getFactory().createEntityManager();
        Uzytkownicy uzytkownicy_admin = entitymanager.find(Uzytkownicy.class, json.optLong("Id_ad"));
        Uzytkownicy uzytkownicy_gracz = entitymanager.find(Uzytkownicy.class, json.optLong("Id_gr"));
        Uzytkownicy uzytkownicy_mod = entitymanager.find(Uzytkownicy.class, json.optLong("Id_mod"));
        JSONObject jsonObject = new JSONObject();
        EntityTransaction transaction = entitymanager.getTransaction();
        if (!uzytkownicy_admin.getUprawnienie().equals("ADMIN")) {
            jsonObject.put("Wynik", false);
            jsonObject.put("Uprawnienia", false);
        } else {
            Weryfikacja weryfikacja = entitymanager.find(Weryfikacja.class, json.optLong("Id"));
            if (weryfikacja == null) {
                jsonObject.put("Wynik", false);
                jsonObject.put("Uprawnienia", true);
            } else {
                int gracztyp = json.optInt("Gracz");
                int zadanietyp = json.optInt("Zadanie");
                boolean odbierz = json.optBoolean("Odbierz");
                boolean zbanuj = json.getBoolean("Zbanuj");
                String tytul2 = "";
                String wiadomosc2 = "";
                transaction.begin();
                if (gracztyp > 0 || zadanietyp > 0) {
                    String tytul = "";
                    String wiadomosc = "";
                    if (gracztyp == 1) {
                        tytul = "ODBLOKOWANO";
                        tytul2 = "ODBLOKOWANO " + uzytkownicy_gracz.getNick();
                        wiadomosc = "Zostales Odblokwany";
                        wiadomosc2 = "Gracz " + uzytkownicy_gracz.getNick() + " zostal Odblokwany";
                        uzytkownicy_gracz.setOgraniczenie("BRAK");

                    } else if (gracztyp == 2) {
                        tytul = "ZABLOKOWANO";
                        tytul2 = "ZABLOKOWANO " + uzytkownicy_gracz.getNick();
                        wiadomosc = "Zostales Zablokowany";
                        wiadomosc2 = "Gracz " + uzytkownicy_gracz.getNick() + " zostal Zablokowany";
                        uzytkownicy_gracz.setOgraniczenie("BLOCK");
                    } else if (gracztyp == 3) {
                        tytul = "ZBANOWANO";
                        tytul2 = "ZBANOWANO " + uzytkownicy_gracz.getNick();
                        wiadomosc = "Zostales Zbanowany";
                        wiadomosc2 = "Gracz " + uzytkownicy_gracz.getNick() + " zostal Zbanowany";
                        uzytkownicy_gracz.setOgraniczenie("BAN");
                    }
                    if (gracztyp > 0 && zadanietyp > 0) {
                        tytul += " | ";
                        tytul2 += " | ";
                        wiadomosc += ", ";
                        wiadomosc2 += ", ";
                    }
                    if (zadanietyp == 1) {
                        tytul += weryfikacja.getNazwa() + " -  USUNIETO";
                        tytul2 += weryfikacja.getNazwa() + " -  USUNIETO zadanie " + uzytkownicy_gracz.getNick();
                        wiadomosc += "Przykro mi twoje zadanie " + weryfikacja.getNazwa() + " zostalo usuniete";
                        wiadomosc2 = "Zadanie " + weryfikacja.getNazwa() + " gracza " + uzytkownicy_gracz.getNick() + " zostalo usuniete";
                        Zadania zadania = entitymanager.find(Zadania.class, weryfikacja.getId_zad());
                        entitymanager.remove(zadania);
                    } else if (zadanietyp == 2) {
                        tytul += weryfikacja.getNazwa() + " - PRZYWROCONO";
                        tytul2 += weryfikacja.getNazwa() + " - PRZYWROCONO " + uzytkownicy_gracz.getNick();
                        wiadomosc += "Twoje zadanie " + weryfikacja.getNazwa() + " zostalo przywrocone";
                        wiadomosc2 += "Zadanie " + weryfikacja.getNazwa() + " gracza " + uzytkownicy_gracz.getNick() + " zostalo przywrocone";
                        String nazwa_zadania = weryfikacja.getNazwa();
                        Long ilosc = 0l;
                        do {
                            ilosc = 0l;
                            try {
                                Query<Long> query = (Query<Long>) entitymanager.createQuery("SELECT COUNT(z) FROM Zadania z WHERE z.nazwa=:nazwa");
                                query.setParameter("nazwa", nazwa_zadania);
                                ilosc = query.getSingleResult();
                            } catch (NoResultException e) {

                            }
                            if (ilosc != 0) {
                                nazwa_zadania += "2";
                            }
                        }
                        while (!(ilosc == 0));
                        Zadania zadania = new Zadania();
                        zadania.setNazwa(nazwa_zadania);
                        zadania.setOpis(weryfikacja.getOpis());
                        zadania.setLimitCzasu(weryfikacja.getLimitCzasu());
                        zadania.setData(LocalDate.now());
                        zadania.setCzas(weryfikacja.getCzas());
                        zadania.setInput(new ArrayList<>(weryfikacja.getInput()));
                        zadania.setOutput(new ArrayList<>(weryfikacja.getOutput()));
                        zadania.setZmienne(weryfikacja.getZmienne());
                        zadania.setPodejscia(0);
                        zadania.setUkonczone(0);
                        zadania.setUzytkownikZad(uzytkownicy_gracz);
                        entitymanager.persist(zadania);

                    }
                    Poczta poczta = new Poczta();
                    uzytkownicy_gracz.setIloscNieodczytanych(uzytkownicy_gracz.getIloscNieodczytanych() + 1);
                    poczta.setTytul(tytul);
                    wiadomosc += ":\n" + json.optString("Wiadomosc_gr") + "\nPozdrawiam " + uzytkownicy_admin.getNick();
                    poczta.setWiadomosc(wiadomosc);
                    poczta.setDoUzytkownik(uzytkownicy_gracz);
                    poczta.setData(LocalDate.now());
                    entitymanager.persist(poczta);

                }
                if (gracztyp > 0 || zadanietyp > 0 || odbierz == true || zbanuj == true) {
                    if ((gracztyp > 0 || zadanietyp > 0) && odbierz) {
                        tytul2 += " | ";
                        wiadomosc2 += ", ";
                    }
                    if (odbierz) {
                        tytul2 += "ODEBRANO MOD";
                        wiadomosc2 += "Przykro mi odebrano ci Moderatora";
                        uzytkownicy_mod.setUprawnienie("USER");
                    }
                    if ((gracztyp > 0 || zadanietyp > 0 || odbierz) && zbanuj) {
                        tytul2 += " | ";
                        wiadomosc2 += ", ";
                    }
                    if (zbanuj) {
                        tytul2 += "ZBANOWANO";
                        wiadomosc2 += "Zostales Zbanowany";
                        uzytkownicy_mod.setOgraniczenie("BAN");
                    }
                    Poczta poczta = new Poczta();
                    uzytkownicy_mod.setIloscNieodczytanych(uzytkownicy_mod.getIloscNieodczytanych() + 1);
                    poczta.setTytul(tytul2);
                    wiadomosc2 += ":\n" + json.optString("Wiadomosc_mod") + "\nPozdrawiam " + uzytkownicy_admin.getNick();
                    poczta.setWiadomosc(wiadomosc2);
                    poczta.setDoUzytkownik(uzytkownicy_mod);
                    poczta.setData(LocalDate.now());
                    entitymanager.persist(poczta);

                }
                jsonObject.put("Wynik", true);
                if(zadanietyp!=1) {
                    entitymanager.remove(weryfikacja);
                }
                transaction.commit();
            }
        }
        writer.write(jsonObject.toString());
        writer.newLine();
        writer.flush();
        entitymanager.close();
    }

    public  void isWeryfikacja(BufferedWriter writer, JSONObject json) throws IOException
    {
        EntityManager entitymanager = Manager.getFactory().createEntityManager();
        Weryfikacja weryfikacja = entitymanager.find(Weryfikacja.class, json.optLong("Id"));
        JSONObject odp = new JSONObject();
        if (weryfikacja != null) {
            odp.put("Wynik", true);
        } else {
            odp.put("Wynik", false);
        }
        writer.write(odp.toString());
        writer.newLine();
        writer.flush();
        entitymanager.close();
    }

}
