package org.pliki;

import org.python.core.PyException;
import org.python.util.PythonInterpreter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class Test extends Thread{
    private Long start,end;
    private String kod;
    private BufferedReader reader;
    private BufferedWriter writer;
    private Socket socket;
    private AtomicBoolean limitCzasu;
    private AtomicBoolean licz;

    public Test(String kod,BufferedReader reader, BufferedWriter writer, Socket socket,AtomicBoolean limitCzasu,AtomicBoolean licz) {
        this.kod=kod;
        this.reader=reader;
        this.writer=writer;
        this.socket=socket;
        this.limitCzasu=limitCzasu;
        this.licz=licz;
    }

    @Override
    public void run() {
        StringWriter output = new StringWriter();
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.setOut(output);
        start = System.nanoTime();
        try {
            licz.set(true);
            interpreter.exec(kod);
            limitCzasu.set(false);
            writer.write(String.valueOf(output));
            writer.flush();
        } catch (PyException e) {
            try {
                writer.write(e.toString());
                writer.flush();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            end=System.nanoTime();
            System.out.println((double)(end-start)/ 1_000_000_000);
            try {
                writer.close();
                reader.close();
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
