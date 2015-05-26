package library.Tunnel;

import library.GlobalVariables;
import test.Test;

import java.io.*;
import java.net.Socket;
import java.net.URL;

/**
 * Created by leind on 14/05/15.
 */
public class Chunk extends Thread {
    private byte[] chunk;
    private String ip;
    private Socket socket;
    private int serverPort = GlobalVariables.SERVER_PORT;
    private int fromByte = 0;

    // Thread variables
    volatile boolean finished = false;

    public Chunk() {}

    public Chunk(byte[] chunk, String ip) {
        this.chunk = chunk;
        this.ip = ip;
    }

    public Chunk send(byte[] chunk) {
        this.chunk = chunk;
        return this;
    }

    public Chunk to(String ip) {
        this.ip = ip;
        return this;
    }

    public Chunk fromByte(int fromByte) {
        this.fromByte = fromByte;
        return this;
    }

    // Threading stuff
    //=========================================================
    public void stopMe() {
        finished = true;
    }

    public void run() {
        //Code
        try { sendBytes(); }
        catch (IOException e) { e.printStackTrace(); }
    }

    /**
     * Sends the current chunk over TCP
     */
    private void sendBytes() throws IOException {
        sendBytes(chunk, this.fromByte, chunk.length);
    }

    /**
     * Sends the current chunk over TCP.
     * Allows controlling which bytes in the byte array to send
     *
     * @param byteArray bytes to send
     * @param start     starting from byte in this position
     * @param length       how many bytes starting from position in start
     */
    private void sendBytes(byte[] byteArray, int start, int length) throws IOException {
        if (length < 0)
            throw new IllegalArgumentException("Negative length not allowed");
        if (start < 0 || start >= byteArray.length)
            throw new IndexOutOfBoundsException("Out of bounds: " + start);

        socket = new Socket(this.ip, this.serverPort);

        OutputStream out = socket.getOutputStream();
        DataOutputStream dos = new DataOutputStream(out);

        dos.writeInt(length);
        if (length > 0) {
            dos.write(byteArray, start, length);
        }

        out.flush();
        out.close();
        socket.close();

    }

    /**
     * Receive data over TCP.
     *
     * @return  byte array of the data received
     */
    private byte[] readBytes() throws IOException {
        InputStream in = socket.getInputStream();
        DataInputStream dis = new DataInputStream(in);

        int len = dis.readInt();
        byte[] data = new byte[len];
        if (len > 0) {
            dis.readFully(data);
        }
        return data;
    }
}
