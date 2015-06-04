package library.Tunnel.PairsConnect;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Leind on 24/05/2015.
 */
public class PairReceiver implements Runnable{

    protected Socket clientSocket = null;
    private ObjectInputStream ois;
    private String message;

    List<PairListener> listeners = new ArrayList<PairListener>();

    public PairReceiver(Socket clientSocket, PairListener catcher) {
        this.clientSocket = clientSocket;
        addListener(catcher);
    }

    public void addListener(PairListener toAdd) {
        listeners.add(toAdd);
    }

    /**
     * Get string message to [String message], [long fileID], [long chunkID]
     * Notify a pair connected and it's looking for something
     * */
    public void notifyPair(String message) {
        List<String> data = Arrays.asList(message.split(","));
        long fileID = Long.parseLong(data.get(1));
        long chunkID = Long.parseLong(data.get(2));
        for (PairListener pairlistener : listeners)
            pairlistener.onPairConnected(data.get(0), fileID, chunkID);
    }

    public void run() {
        try {
            ois = new ObjectInputStream(clientSocket.getInputStream());

            //convert ObjectInputStream object to String
            message = (String) ois.readObject();
        }
        catch (IOException ex) { System.out.println("Can't get socket input stream. "); }
        catch (ClassNotFoundException e) { e.printStackTrace(); }

        notifyPair(message);

        try { clientSocket.close(); }
        catch (IOException e) { e.printStackTrace(); }

        return;
    }
}
