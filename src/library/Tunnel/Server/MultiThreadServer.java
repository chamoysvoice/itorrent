package library.Tunnel.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/**
 * Created by Leind on 24/05/2015.
 */
public class MultiThreadServer implements Runnable {
    protected int           serverPort    = 8080;
    protected ServerSocket  serverSocket  = null;
    protected boolean       isStopped     = false;
    protected Thread        runningThread = null;
    protected List<ChunkListener> chunkCatcher  = null;

    public MultiThreadServer(int port, List<ChunkListener> chunkCatcher){
        this.serverPort = port;
        this.chunkCatcher = chunkCatcher;
    }

    public void run(){
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();
        while(! isStopped()){
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if(isStopped()) {
                    System.out.println("Server Stopped.") ;
                    return;
                }
                throw new RuntimeException(
                        "Error accepting client connection", e);
            }
            new Thread(new Receiver(clientSocket, chunkCatcher)).start();
        }
        System.out.println("Server Stopped.") ;
    }

    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port 8080", e);
        }
    }
}
