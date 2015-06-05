package library.Tunnel.Server;

import library.Tunnel.Chunk;
import library.Tunnel.ChunkModel;
import library.Tunnel.PairsConnect.PairListener;
import test.Test;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Leind on 24/05/2015.
 */
public class Receiver implements Runnable{

    protected Socket clientSocket = null;
    List<ChunkListener> listeners = new ArrayList<ChunkListener>();

    private ObjectInputStream inStream;

    /**
     * Add this listener to listeners list
     * */
    public Receiver(Socket clientSocket, List<ChunkListener> chunkListener) {
        this.clientSocket = clientSocket;
        addListener(chunkListener);
    }

    public void addListener(List<ChunkListener> chunkListeners) {
        this.listeners.addAll(chunkListeners.stream().collect(Collectors.toList()));
    }

    /**
     * Notify when a chunk has been received
     * */
    public void notifyReceived(ChunkModel chunk) {
        for (ChunkListener pairlistener : listeners)
            pairlistener.onChunkReceived(chunk);
    }

    public void run() {
        ChunkModel chunk = new ChunkModel();

        try {
            inStream = new ObjectInputStream(clientSocket.getInputStream());
            chunk = (ChunkModel) inStream.readObject();
        }
        catch (IOException e) { e.printStackTrace(); }
        catch (ClassNotFoundException e) { e.printStackTrace(); }

        notifyReceived(chunk);

        try {
            inStream.close();
            clientSocket.close();
        }
        catch (IOException e) { e.printStackTrace(); }

        return;
    }
}
