package library.Tunnel.PairsConnect;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/**
 * Created by Leind on 24/05/2015.
 */
public class MultiThreadPairServer implements Runnable {
    protected int          serverPort    = 8081;
    protected ServerSocket serverSocket  = null;
    protected boolean      isStopped     = false;
    protected Thread       runningThread = null;
    protected List<PairListener> catcher       = null;

    public MultiThreadPairServer(int port, List<PairListener> catcher){ this.serverPort = port; this.catcher = catcher;}

    public void run(){
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();
        while(!isStopped()){
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            }
            catch (IOException e) {
                if(isStopped()) {
                    System.out.println("PairServer Stopped.") ;
                    return;
                }
                throw new RuntimeException(
                        "Error accepting client connection", e);
            }

            new Thread(new PairReceiver(clientSocket, catcher)).start();
        }
        System.out.println("PairServer Stopped.") ;
    }


    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing pair server", e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port " + this.serverPort, e);
        }
    }

}
