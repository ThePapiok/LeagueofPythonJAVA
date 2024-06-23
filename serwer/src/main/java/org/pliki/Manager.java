package org.pliki;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Manager {
    private static EntityManagerFactory factory;


    public Manager() {

    }

    public static void setFactory(String name) {
        factory=Persistence.createEntityManagerFactory(name);
    }

    public void close()
    {

        factory.close();
    }

    public static EntityManagerFactory getFactory() {
        return factory;
    }

}
