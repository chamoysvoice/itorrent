package test;

import library.Session;
import library.Tunnel.Chunk;
import library.Tunnel.ChunkModel;
import library.Tunnel.PairsConnect.PairListener;
import library.Tunnel.PairsConnect.PairServer;
import library.Tunnel.Server.ChunkListener;
import library.Tunnel.Server.Server;
import library.Utils.UndefinedPathException;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Leind on 26/05/2015.
 */
public class Start {

    private static Session session;
    static byte[] aa = {(byte)0x03, (byte)0x00, (byte)0x00, (byte)0x00};

    public static void main(String[] args) throws InterruptedException, SAXException, ParserConfigurationException, IOException, UndefinedPathException {
        Test.checkFoldersTest();
        startSession();

        // Can only be called once per computer
        //startServers();

        requestSomething();
        sendSomething();
    }

    // Starts core server and PairServer to communicate with other clients
    //==============================================================================
    public static void startServers() {
        Server.startServer(new ChunkCatcher());
        Runtime.getRuntime().addShutdownHook(new Thread(Server::stopServer));

        PairServer.startServer(new PairCatcher());
        Runtime.getRuntime().addShutdownHook(new Thread(PairServer::stopServer));
    }

    public static void sendSomething() {
        System.out.println(aa.length);
        Chunk chunk = new Chunk();
        chunk.send(aa).to("127.0.0.1").fileID(123).chunkID(24).start();
    }

    public static void requestSomething() {
        Chunk chunk = new Chunk();
        chunk.request(237, 40).start();
    }

    private static void startSession() {
        session = new Session();
        session.changeServer();
        session.start();
        session.yield();
    }

    // Someone asking for chunk
    static class PairCatcher implements PairListener {
        @Override
        public void onPairConnected(String message, long fileID, long chunkID) {
            System.out.println("-------------------");
            System.out.print("Message: " + message + "\nFileID: " + fileID + "\nChunkId: " + chunkID);
            System.out.println("------------------- \n");

            // TODO:
            // Check if the chunk requested can be sended and send it via
            //  Chunk chunk = new Chunk();
            //  chunk.request(237, 40).start();
        }
    }

    // Someone sended me a chunk
    static class ChunkCatcher implements ChunkListener{
        @Override
        public void onChunkReceived(ChunkModel chunk) {
            System.out.println("-------------------");
            System.out.println("File: " + chunk.getFileID());
            System.out.println("Chunk: " + chunk.getChunkID());
            System.out.println("Same byte array: " + Arrays.equals(aa, chunk.getData()));
            System.out.println("\n------------------- \n");

            // TODO:
            // Put this chunk wherever it belongs to
        }
    }
}