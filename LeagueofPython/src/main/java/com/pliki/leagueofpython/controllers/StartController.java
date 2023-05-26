package com.pliki.leagueofpython.controllers;

import com.pliki.leagueofpython.Manager;
import com.pliki.leagueofpython.StageClass;
import com.pliki.leagueofpython.User;
import com.pliki.leagueofpython.Uzytkownicy;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.hibernate.query.Query;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;

import org.mindrot.jbcrypt.BCrypt;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

import static java.lang.Math.min;

public class StartController {

    private static EntityManagerFactory factory;
    private Scene scene;
    private List<String> dozwolone=Arrays.asList("1","2","3","4","5","6","7","8","9","0","q","w","e","r","t","y","u","i","o","p","a","s","d","f","g","h","j","k","l","z","x","c","v","b","n","m","\b");
    @FXML
    private TextField poleNick;
    @FXML
    private PasswordField poleHaslo;
    @FXML
    private Label blad;



    public void initialize() {
        Manager man=new Manager();
        factory=man.getFactory();
        poleNick.textProperty().addListener((observable, oldValue, newValue) -> {
            sprawdzenie(oldValue,newValue,0,poleNick);
        });
        poleHaslo.textProperty().addListener((observable, oldValue, newValue) -> {
            sprawdzenie(oldValue,newValue,1,poleHaslo);
        });
    }

    public void register()
    {
        if(poleNick.getLength()>=6&&poleHaslo.getLength()>=6)
        {
            EntityManager entitymanager = factory.createEntityManager();
            Query<Long> query = (Query<Long>) entitymanager.createQuery("SELECT COUNT(u) FROM Uzytkownicy u WHERE u.nick = :nick");
            query.setParameter("nick", poleNick.getText());
            Long ilosc=query.getSingleResult();
            if(ilosc==0) {
                EntityTransaction transaction = entitymanager.getTransaction();
                transaction.begin();
                Uzytkownicy uzytkownicy=new Uzytkownicy();
                uzytkownicy.setNick(poleNick.getText());
                uzytkownicy.setHaslo(BCrypt.hashpw(poleHaslo.getText(),BCrypt.gensalt()));
                uzytkownicy.setOgraniczenie("BRAK");
                uzytkownicy.setUprawnienie("USER");
                entitymanager.persist(uzytkownicy);
                transaction.commit();
                poleNick.setText("");
                poleHaslo.setText("");
                blad.setText("Pomyslnie zarejestrowano, mozesz teraz sie zalogowac");
            }
            else
            {
                blad.setText("Taki uzytkownik juz istnieje");
            }
            entitymanager.close();
        }
        else
        {
            blad.setText("Login lub Haslo musi miec co najmniej 6 liter");
        }
    }

    public void login()
    {
        EntityManager entitymanager = factory.createEntityManager();
        Query<Long> query = (Query<Long>) entitymanager.createQuery("SELECT u.id_uz FROM Uzytkownicy u WHERE u.nick = :nick");
        query.setParameter("nick", poleNick.getText());
        try {
            Long id = query.getSingleResult();
            Uzytkownicy uzytkownicy=entitymanager.find(Uzytkownicy.class, id);
            if(BCrypt.checkpw(poleHaslo.getText(), uzytkownicy.getHaslo()))
            {
                User.setId(id);
                poleNick.setText("");
                poleHaslo.setText("");
                StageClass.getStage().setScene(scene);
            }
            else
            {
                blad.setText("Haslo lub Login jest nieprawidlowy");
            }
        }
        catch (NoResultException e)
        {
            blad.setText("Haslo lub Login jest nieprawidlowy");
        }

    }

    public void sprawdzenie(String oldValue, String newValue, int tryb, TextInputControl pole)
    {
        int newLen=newValue.length()-1;
        if(newLen!=-1) {
            int minLen = min(oldValue.length() - 1,newLen);
            String chr = String.valueOf(newValue.charAt(newValue.length() - 1));
            for (int i = 0; i <= minLen; i++) {
                if (newValue.charAt(i) != oldValue.charAt(i)) {
                    chr = String.valueOf(newValue.charAt(i));
                    break;
                }
            }
            if (newLen > 19) {
                pole.setText(oldValue);
                blad.setText("Login lub Haslo musi miec co najwyzej 20 liter");

            }
            else if (!dozwolone.contains(chr.toLowerCase())) {
                pole.setText(oldValue);
                blad.setText("Niedozwolony znak");
            }
        }
    }
    public void setScene(Scene scene)
    {
        this.scene=scene;
    }

}
