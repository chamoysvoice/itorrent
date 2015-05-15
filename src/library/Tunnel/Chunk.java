package library.Tunnel;

import java.io.*;
import java.net.Socket;

/**
 * Created by leind on 14/05/15.
 */
public class Chunk {
    private byte[] chunk;
    private String ip;
    private Socket socket;

    Chunk(byte[] chunk, String ip) {
        this.chunk = chunk;
        this.ip = ip;
    }

    /**
     * Sends the current chunk over TCP
     */
    public void sendBytes() throws IOException {
        sendBytes(chunk, 0, chunk.length);
    }

    /**
     * Sends the current chunk over TCP.
     * Allows controlling which bytes in the byte array to send
     *
     * @param byteArray bytes to send
     * @param start     starting from byte in this position
     * @param len       how many bytes starting from position in start
     */
    public void sendBytes(byte[] byteArray, int start, int len) throws IOException {
        if (len < 0)
            throw new IllegalArgumentException("Negative length not allowed");
        if (start < 0 || start >= byteArray.length)
            throw new IndexOutOfBoundsException("Out of bounds: " + start);
        // Other checks if needed.

        OutputStream out = socket.getOutputStream();
        DataOutputStream dos = new DataOutputStream(out);

        dos.writeInt(len);
        if (len > 0) {
            dos.write(byteArray, start, len);
        }
    }

    /**
     * Receive data over TCP.
     *
     * @return  byte array of the data received
     */
    public byte[] readBytes() throws IOException {
        InputStream in = socket.getInputStream();
        DataInputStream dis = new DataInputStream(in);

        int len = dis.readInt();
        byte[] data = new byte[len];
        if (len > 0) {
            dis.readFully(data);
        }
        return data;
    }

    private int getAvailablePort() {
        //TODO return an open port
        throw new UnsupportedOperationException("Uninmplemented");
    }
}
