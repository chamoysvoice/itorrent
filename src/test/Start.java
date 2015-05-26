package test;

import library.Tunnel.Chunk;
import library.Tunnel.Server.PoolServer;
import library.Tunnel.Server.Server;
import library.UPnP.GatewayDevice;
import library.Utils.UndefinedPathException;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Leind on 26/05/2015.
 */
public class Start {

    public static void main(String[] args) throws InterruptedException, SAXException, ParserConfigurationException, IOException, UndefinedPathException {
        //Server.startServer();
        //Runtime.getRuntime().addShutdownHook(new Thread(() -> { Server.stopServer(); }));

        Chunk chunk = new Chunk();
        chunk.request(1,3).start();
    }
}