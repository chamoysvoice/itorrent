package test;

import com.alee.laf.WebLookAndFeel;
import library.FileBuilder;
import library.FileManager;
import library.FileSearcher;
import library.Session;
import library.Tunnel.Chunk;
import library.Tunnel.ChunkModel;
import library.Tunnel.PairsConnect.PairListener;
import library.Tunnel.PairsConnect.PairServer;
import library.Tunnel.Server.ChunkListener;
import library.Tunnel.Server.Server;
import library.Utils.OSDetector;
import library.Utils.PathBuilder;
import library.Utils.UndefinedPathException;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Leind on 26/05/2015.
 */
public class Start {

    private static Session session;
    static byte[] aa = {(byte)0x03, (byte)0x00, (byte)0x00, (byte)0x00};
    private static List<ChunkListener> list = new ArrayList<>();
    private static List<PairListener> pairlist = new ArrayList<>();
    static ArrayList<FileBuilder> filebuilders = new ArrayList<FileBuilder>();
    private static PathBuilder itorrPath;

    public static void main(String[] args) throws InterruptedException, SAXException, ParserConfigurationException, IOException, UndefinedPathException {
        // You should work with UI (including installing L&F) inside Event Dispatch Thread (EDT)
        //SwingUtilities.invokeLater(() -> {
        //    WebLookAndFeel.install();
        //   new GUI.Client().setVisible(true);
        //});
        startSession();
        loadItorFiles();

        //Test.checkFoldersTest();

        // Can only be called once per computer
        //addListeners();
        //startServers();

        requestSomething();
        sendSomething();
    }

    private static void loadItorFiles() throws UndefinedPathException {
        int dot;
        List<String> files;
        itorrPath = new PathBuilder(OSDetector.getOS());
        files = FileManager.listFilesForFolder(new File(itorrPath.getTorrentsPath()));
        try {
            for (String s: files){
                if (s.charAt(s.length()-1) == 'r'){
                    dot = s.indexOf(".");
                    filebuilders.add(new FileBuilder(itorrPath.getTorrentsPath()+ s.substring(0,dot) + ".itor", Long.parseLong(s.substring(0,dot))));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addListeners() throws UndefinedPathException {
        list.addAll(filebuilders.stream().collect(Collectors.toList()));
        pairlist.addAll(filebuilders.stream().collect(Collectors.toList()));
    }

    // Starts core server and PairServer to communicate with other clients
    //==============================================================================
    public static void startServers() {
        Server.startServer(list);
        Runtime.getRuntime().addShutdownHook(new Thread(Server::stopServer));

        PairServer.startServer(pairlist);
        Runtime.getRuntime().addShutdownHook(new Thread(PairServer::stopServer));
    }

    public static void sendSomething() {
        System.out.println(aa.length);
        Chunk chunk = new Chunk();
        chunk.send(aa).to("127.0.0.1").fileID(123).chunkID(24).start();
    }

    public static void requestSomething() {
        System.out.print("Sending");
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
            // Check if the chunk requested can be sent and send it via
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