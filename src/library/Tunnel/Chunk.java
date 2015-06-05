package library.Tunnel;

import library.FileSearcher;
import library.GlobalVariables;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by leind on 14/05/15.
 */
public class Chunk extends Thread {
    // Who to request or send chunk
    private String who;
    private Socket socket;
    private int serverPort = GlobalVariables.SERVER_PORT;
    private int serverPairPort = GlobalVariables.SERVER_PAIR_PORT;
    private boolean sending;

    // Sending variables
    private byte[] chunk;
    private int fromByte = 0;

    // Receiving variables
    private long fileID;
    private long chunkID;
    private ArrayList<String> ipPairs;

    // Thread variables
    volatile boolean finished = false;

    public Chunk() {}

    public Chunk to(String who) {
        this.who = who;
        return this;
    }

    // Chunk send constructors
    //=========================================================
    public Chunk send(byte[] chunk) {
        this.chunk = chunk;
        this.sending = true;
        return this;
    }

    public Chunk fileID(long fileID) {
        this.fileID = fileID;
        return this;
    }

    public Chunk chunkID(long chunkID) {
        this.chunkID = chunkID;
        return this;
    }

    public Chunk fromByte(int fromByte) {
        this.fromByte = fromByte;
        return this;
    }

    // Chunk send constructors
    //=========================================================
    public Chunk request(long fileID, long chunkID) {
        this.sending = false;
        this.fileID = fileID;
        this. chunkID = chunkID;
        return this;
    }

    // Threading stuff
    //=========================================================
    /**
     * Call this to stop the current thread
     */
    private void stopMe() {
        finished = true;
    }

    /**
     * Thread main method .start()
     */
    public void run() {
        while (!finished) {
            // If sending data
            if (sending) {
                try { sendChunk(); }
                catch (IOException e) { e.printStackTrace(); stopMe(); }
            }

            // If requesting data
            else {
                if (getPairsFromServer()) {
                    giveMeChunk();
                    stopMe();
                }
                else { System.out.println("Failed to get the pairs IPs"); stopMe(); }
            }
        }
    }

    // Net methods
    //=========================================================
    private void giveMeChunk() {
        pokePairs();
        return;
    }

    /**
     * Tells to the ips of the array "Hey send me this chunk please"
     * When an IP responds with true [askForChunk()] will break
     * If an IP response with false [askForChunk()] will try with the next ip
     */
    private void pokePairs() {
        for (String pair : this.ipPairs) {
            try {
                if (askForChunk(pair))
                    break;
            }
            catch (IOException e) { e.printStackTrace(); }
        }
    }

    private boolean askForChunk(String pair) throws IOException {
        if (pair.equals(""))
            socket = new Socket("127.0.0.1", this.serverPairPort);
        else
            socket = new Socket(pair, this.serverPairPort);

        URL whatismyip = new URL("http://checkip.amazonaws.com");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                whatismyip.openStream()));
        String ip = in.readLine(); //you get the IP as a String

        /// The string contains the "[poke],[fileID],[chunkID]"
        String toSend = ip
                        + ","
                        + String.valueOf(this.fileID)
                        + ","
                        + String.valueOf(this.chunkID);

        ObjectOutputStream oos = null;

        oos = new ObjectOutputStream(socket.getOutputStream());
        System.out.println("Sending request to Socket Server");

        oos.writeObject(toSend);
        oos.close();

        return true;
    }

    /**
     * Sends GET method to server api
     * Sets the response into a member pairs IPs ArrayList<>
     */
    private boolean getPairsFromServer() {
        //String url = GlobalVariables.current_server;
        try { ipPairs = FileSearcher.searchChunk(this.fileID, this.chunkID); }
        catch (IOException e) { e.printStackTrace(); return false; }

        return true;
    }

    private void sendChunk() throws IOException {
        socket = new Socket(this.who, this.serverPort);
        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
        ChunkModel chunk = new ChunkModel(this.chunk, this.fileID, this.chunkID);
        outputStream.writeObject(chunk);

        outputStream.flush();
        outputStream.close();
        socket.close();

        stopMe();
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

        socket = new Socket(this.who, this.serverPort);

        OutputStream out = socket.getOutputStream();
        DataOutputStream dos = new DataOutputStream(out);

        dos.writeInt(length);
        if (length > 0) {
            dos.write(byteArray, start, length);
        }

        out.flush();
        out.close();
        socket.close();

        stopMe();
    }
}
