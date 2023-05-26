package org.pliki;

import org.python.core.PyException;
import org.python.util.PythonInterpreter;
import java.util.Timer;
import java.util.TimerTask;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class Watek extends Thread{
    private Socket socket;
    private String linia;
    private int ID;
    private String czas;
    private int ID_zad;
    private int licznik;
    private String kod="";

    public Watek(Socket socket)
    {
        this.socket=socket;

    }

    @Override
    public void run()
    {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                licznik = 0;
                System.out.println(1);

                while ((linia = reader.readLine()) != null) {
                    if (linia.equals("#END")) {
                        break;
                    }
                    if (licznik == 0) {
                        ID_zad = Integer.parseInt(linia);
                        licznik++;
                    } else if (licznik == 1) {
                        ID = Integer.parseInt(linia);
                        licznik++;
                    } else if (licznik == 2) {
                        czas = linia;
                        licznik++;
                    } else {
                        kod += linia + "\n";
                    }
                }
                AtomicBoolean limitCzasu = new AtomicBoolean(true);
                AtomicBoolean licz= new AtomicBoolean(false);
                Test test = new Test(kod, reader, writer, socket, limitCzasu,licz);
                test.start();
                while(licz.get()==false) {

                }
                Thread.sleep(5000);
                if (limitCzasu.get() == true) {
                    test.interrupt();
                    writer.write("Przekroczono limit czasu");
                    writer.flush();
                    writer.close();
                    reader.close();
                    socket.close();
                }

        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }
}
