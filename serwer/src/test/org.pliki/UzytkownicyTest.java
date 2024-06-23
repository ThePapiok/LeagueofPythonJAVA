package org.pliki;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.io.IOException;
import java.net.ServerSocket;
import java.time.LocalDate;

public class UzytkownicyTest {

    private ServerSocket serverSocket;
    private Manager manager;

    @BeforeEach
    public void before() throws IOException {
        serverSocket=new ServerSocket(8006);
        manager=new Manager();
        manager.setFactory("Baza");
    }

    @Test
    public void testUser()
    {
        EntityManager entitymanager = Manager.getFactory().createEntityManager();
        EntityTransaction transaction = entitymanager.getTransaction();
        transaction.begin();
        Uzytkownicy uzytkownicy = new Uzytkownicy();
        uzytkownicy.setNick("TestUser");
        uzytkownicy.setHaslo(BCrypt.hashpw("TestUser", BCrypt.gensalt()));
        uzytkownicy.setOgraniczenie("BRAK");
        uzytkownicy.setUprawnienie("USER");
        uzytkownicy.setUsuwanie(true);
        uzytkownicy.setIloscNieodczytanych(0);
        uzytkownicy.setData(LocalDate.now());
        entitymanager.persist(uzytkownicy);
        transaction.commit();
        Uzytkownicy szukany=entitymanager.find(Uzytkownicy.class,uzytkownicy.getId_uz());
        Assertions.assertNotNull(szukany);
        Assertions.assertEquals("TestUser",szukany.getNick());
        Assertions.assertTrue(BCrypt.checkpw("TestUser", szukany.getHaslo()));
        Assertions.assertEquals("BRAK",szukany.getOgraniczenie());
        Assertions.assertEquals("USER",szukany.getUprawnienie());
        Assertions.assertTrue(szukany.isUsuwanie());
        Assertions.assertEquals(LocalDate.now(),szukany.getData());
        transaction.begin();
        entitymanager.remove(szukany);
        transaction.commit();
        szukany=entitymanager.find(Uzytkownicy.class,uzytkownicy.getId_uz());
        Assertions.assertNull(szukany);

    }

    @AfterEach
    public void end() throws IOException {
        serverSocket.close();
        manager.close();

    }

}
