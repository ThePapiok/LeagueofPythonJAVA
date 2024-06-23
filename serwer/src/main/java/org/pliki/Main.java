package org.pliki;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException
    {
        ServerSocket serverSocket=new ServerSocket(8006);
        Manager manager=new Manager();
        manager.setFactory("Baza");
        System.out.println("Pomyslnie uruchomiono serwer.");
        while(true)
        {
            Socket socket=serverSocket.accept();
            Watek watek=new Watek(socket);
            try {
                watek.run();
            }
            catch (RuntimeException e)
            {
                e.printStackTrace();
                System.err.println("Nie udalo sie wykonac operacji!");
            }
            finally {
                socket.close();
            }
        }

    }
}