package org.pliki;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class Licznik extends  Thread{

    private Thread thread;
    private BufferedReader reader;
    private BufferedWriter writer;
    private Socket socket;

    public Licznik(Thread thread, BufferedWriter writer, BufferedReader reader, Socket socket){
        this.thread = thread;
        this.reader = reader;
        this.writer = writer;
        this.socket = socket;
    }


    @Override
    public void run() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                try {
                    writer.write("Przekroczono limit czasu");
                    writer.flush();
                    writer.close();
                    reader.close();
                    socket.close();
                    thread.interrupt();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }, 5000);
    }
}
