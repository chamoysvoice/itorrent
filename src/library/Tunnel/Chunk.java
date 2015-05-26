package library.Tunnel;

import library.GlobalVariables;
import library.Utils.JSON.JSONObject;
import test.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by leind on 14/05/15.
 *
 * JSON usage:
 * JSONObject json = new JSONObject(response.toString());
 * System.out.println("phonetype = " + json.get("phonetype"));
 * System.out.println("cat = " + json.get("cat"));
 */
public class Chunk extends Thread {
    // Who to request or send chunk
    private String who;
    private Socket socket;
    private int serverPort = GlobalVariables.SERVER_PORT;
    private boolean sending;

    // Sending variables
    private byte[] chunk;
    private int fromByte = 0;

    // Receiving variables
    private long fileID;
    private long chunkID;
    private JSONObject friends;

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

    public Chunk fromByte(int fromByte) {
        this.fromByte = fromByte;
        return this;
    }

    // Chunk send constructors
    //=========================================================
    public Chunk request(long fileID, long chunkID) {
        this.sending = false;
        return this;
    }

    // Threading stuff
    //=========================================================
    public void stopMe() {
        finished = true;
    }

    public void run() {
        while (!finished) {
            // If sending data
            if (sending) {
                try { sendBytes(); }
                catch (IOException e) { e.printStackTrace(); stopMe(); }
            }

            // If requesting data
            else {
                try {
                    // If response code: 200
                    if (sendGetRequest()) {
                        stopMe();
                    }
                    else { stopMe(); }
                }
                catch (Exception e) { e.printStackTrace(); stopMe(); }
            }
        }
    }

    // Net methods
    //=========================================================
    /**
     * Sends GET method to server api
     * Sets the response into a JSON member object
     */
    private boolean sendGetRequest() throws Exception {

        String url = "https://api.myjson.com/bins/1364k";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        // If request failed, stop
        if (responseCode != 200) { return false; }

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) { response.append(inputLine); }
        in.close();

        // Set this JSONObject
        this.friends = new JSONObject(response.toString());
        return true;
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
