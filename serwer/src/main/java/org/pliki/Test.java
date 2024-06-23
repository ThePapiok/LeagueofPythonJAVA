package org.pliki;

import org.json.JSONObject;
import org.python.core.PyException;
import org.python.util.PythonInterpreter;

import javax.persistence.EntityManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Test extends Thread{
    private Long start,end;
    private String kod;
    private AtomicBoolean limitCzasu;
    private Zadania zadania;
    private AtomicBoolean akceptuje;
    private AtomicInteger negatywne;
    private StringBuilder wynik;
    private AtomicInteger itest;
    private float[] czas;

    public Test(String kod, AtomicBoolean limitCzasu, Zadania zadania, AtomicInteger negatywne,AtomicBoolean akceptuje,StringBuilder wynik,AtomicInteger itest,float[] czas) {
        this.kod=kod;
        this.limitCzasu=limitCzasu;
        this.zadania=zadania;
        this.akceptuje=akceptuje;
        this.negatywne=negatywne;
        this.wynik=wynik;
        this.itest=itest;
        this.czas=czas;
    }

    @Override
    public void run() {
        StringWriter output = new StringWriter();
        JSONObject jsonObject=new JSONObject();
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.setOut(output);
        try {
            start = System.nanoTime();
            interpreter.exec(kod);
            limitCzasu.set(false);
            String wynikOutput=String.valueOf(output).replace("\n","");
            if(wynikOutput.equals(zadania.getOutput().get(itest.get())))
            {
                end = System.nanoTime();
                czas[0]+=((float) (end - start) / 1_000_000_000);
                wynik.append("Test "+String.valueOf(itest.get()+1)+" [SUKCES]\n\tTest przeszedl pomyslnie.\n");
            }
            else
            {
                akceptuje.set(false);
                wynik.append("Test "+String.valueOf(itest.get()+1)+" [BLAD]\n\tTwoj wynik: "+wynikOutput+"\n\tSpodziewany: "+zadania.getOutput().get(itest.get())+"\n");
                negatywne.incrementAndGet();
            }
        } catch (PyException e) {

            akceptuje.set(false);
            wynik.append("Test "+String.valueOf(itest.get()+1)+" [BLAD]\n\t"+e.toString()+"\n");
            negatywne.incrementAndGet();
        }
    }
}
