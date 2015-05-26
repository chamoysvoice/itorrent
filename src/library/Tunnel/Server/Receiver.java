package library.Tunnel.Server;

import test.Test;

import java.io.*;
import java.net.Socket;
import java.net.URL;

/**
 * Created by Leind on 24/05/2015.
 */
public class Receiver implements Runnable{

    protected Socket clientSocket = null;
    protected String serverText   = null;

    volatile boolean finished = false;

    private InputStream is;
    private int bufferSize;
    FileOutputStream fos = null;
    DataInputStream dIn;

    public Receiver(Socket clientSocket, String serverText) {
        this.clientSocket = clientSocket;
        this.serverText   = serverText;
    }

    public void run() {
        try {
            is = clientSocket.getInputStream();
            dIn = new DataInputStream(is);

            bufferSize = clientSocket.getReceiveBufferSize();
            System.out.println("Buffer size: " + bufferSize);
        }
        catch (IOException ex) { System.out.println("Can't get socket input stream. "); }

        try {
            URL location = Test.class.getProtectionDomain().getCodeSource().getLocation();
            System.out.println(location.getFile());
            fos = new FileOutputStream(location.getFile() + "test2.txt");
        }
        catch (FileNotFoundException ex) { System.out.println("File not found. ");}

        byte[] bytes = new byte[bufferSize];

        int count;

        try {
            is.read(bytes);
            is.close();
            clientSocket.close();
        }
        catch (IOException e) { e.printStackTrace(); }
    }
}
