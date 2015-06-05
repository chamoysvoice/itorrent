package library.Tunnel.Server;

import library.GlobalVariables;
import library.UPnP.GatewayDevice;
import library.UPnP.GatewayDiscover;
import library.UPnP.PortMappingEntry;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.InetAddress;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Leind on 26/05/2015.
 */
public class Server {

    // StartServer variables
    private static int SERVER_PORT = GlobalVariables.SERVER_PORT;
    private static MultiThreadServer server;
    private static GatewayDevice activeGW;

    // Start server
    // Call this in order to receive data
    //==============================================================================
    public static void startServer(List<ChunkListener> chunkCatcher) {
        try { openPort(); }
        catch (IOException e) { e.printStackTrace(); }
        catch (SAXException e) { e.printStackTrace(); }
        catch (ParserConfigurationException e) { e.printStackTrace(); }

        server = new MultiThreadServer(SERVER_PORT, chunkCatcher);
        new Thread(server).start();
    }

    // Stop server
    // Call this when program finish in order to unmap the ports
    //==============================================================================
    public static void stopServer() {
        System.out.println("Stopping Server");
        server.stop();

        try { closePort(); }
        catch (IOException e) { e.printStackTrace(); }
        catch (SAXException e) { e.printStackTrace(); }
    }

    // Open port assuming UPnPTest() passed correctly
    //==============================================================================
    private static GatewayDevice openPort() throws IOException, SAXException, ParserConfigurationException {
        GatewayDiscover gatewayDiscover = new GatewayDiscover();

        Map<InetAddress, GatewayDevice> gateways = gatewayDiscover.discover();

        if (gateways.isEmpty())
            return null;

        // choose the first active gateway for the tests
        activeGW = gatewayDiscover.getValidGateway();

        if (activeGW == null)
            return null;

        // testing getGenericPortMappingEntry
        PortMappingEntry portMapping = new PortMappingEntry();

        InetAddress localAddress = activeGW.getLocalAddress();

        if (activeGW.getSpecificPortMappingEntry(SERVER_PORT, GlobalVariables.UPNP_PROTOCOL_TCP, portMapping))
            return null;
        else {
            if (activeGW.addPortMapping(SERVER_PORT, SERVER_PORT, localAddress.getHostAddress(), GlobalVariables.UPNP_PROTOCOL_TCP, "test"))
                return activeGW;

            return null;
        }
    }

    // Close port
    //==============================================================================
    private static void closePort() throws IOException, SAXException {
        if (activeGW.deletePortMapping(SERVER_PORT, GlobalVariables.UPNP_PROTOCOL_TCP)) {
            addLogLine("Port " + (SERVER_PORT) + ": mapping removed SUCCESSFUL ?");
        } else {
            addLogLine("Port mapping removal FAILED");
        }
    }

    /**
     * UPnP test function
     *
     * @param line  line to log
     */
    private static void addLogLine(String line) {

        String timeStamp = DateFormat.getTimeInstance().format(new Date());
        String logline = timeStamp+": "+line+"\n";
        System.out.print(logline);
    }
}