/*
 * https://github.com/bitletorg/weupnp
 */

package test;

import library.FileBuilder;
import library.FileManager;
import library.FormatManager;
import library.GlobalVariables;
import library.UPnP.GatewayDevice;
import library.UPnP.GatewayDiscover;
import library.UPnP.PortMappingEntry;
import library.Utils.OSDetector;
import library.Utils.PathBuilder;
import library.Utils.UndefinedPathException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;


public class Test {

	// UPnP Test variables
	//==============================================================================
	private static int SAMPLE_PORT = 6991;
	private static short WAIT_TIME = 5;
	private static boolean LIST_ALL_MAPPINGS = false;
    
	// Core test variables
	//==============================================================================
    private static PathBuilder itorrPath;
    private static boolean isCreated;
    private static int testItorId = 18;

	public static void main(String[] args) throws InterruptedException, SAXException, ParserConfigurationException, IOException, UndefinedPathException {
        //CheckFoldersTest();
        UPnPTest();
        //CoreTest();
    }

	// Check / Create directories test
	//==============================================================================
    private static void CheckFoldersTest() throws UndefinedPathException {
        itorrPath = new PathBuilder(OSDetector.getOS());
        isCreated = false;
        checkBasePath();
        checkTorrentsPath();
        checkTempPath();
        checkDownloadsPath();
    }

    // Base directory
    private static boolean checkBasePath() throws UndefinedPathException {
        File fBasePath = new File(itorrPath.getBasePath());
        if (!fBasePath.exists()) {
            addLogLine("Creating base directory: " + itorrPath.getBasePath());

            try     { fBasePath.mkdir(); isCreated = true; }
            catch   (SecurityException se) { }

            if (isCreated) 	{ addLogLine("Base directory created ✓"); return true; }
            else 			{ addLogLine("Failed creating base directory"); return false; }
        }
        else { addLogLine("Base directory exists ✓");  return true; }
    }

    // Torrents directory
    private static boolean checkTorrentsPath() throws UndefinedPathException {
        File fBasePath = new File(itorrPath.getTorrentsPath());
        if (!fBasePath.exists()) {
            addLogLine("Creating torrents directory: " + itorrPath.getTorrentsPath());

            try     { fBasePath.mkdir(); isCreated = true; }
            catch   (SecurityException se) { }

            if (isCreated) 	{ addLogLine("Torrents directory created ✓"); return true; }
            else 			{ addLogLine("Failed creating torrents directory"); return false; }
        }
        else { addLogLine("Torrents directory exists ✓"); return true; }
    }

    // Temp directory
    private static boolean checkTempPath() throws UndefinedPathException {
        File fBasePath = new File(itorrPath.getTempPath());
        if (!fBasePath.exists()) {
            addLogLine("Creating temp directory: " + itorrPath.getTempPath());

            try     { fBasePath.mkdir(); isCreated = true; }
            catch   (SecurityException se) { }

            if (isCreated) 	{ addLogLine("Temp directory created ✓"); return true; }
            else 			{ addLogLine("Failed creating temp directory"); return false; }
        }
        else { addLogLine("Temp directory exists ✓"); return true; }
    }

    // Downloads directory
    private static boolean checkDownloadsPath() throws UndefinedPathException {
        File fBasePath = new File(itorrPath.getDownloadsPath());
        if (!fBasePath.exists()) {
            addLogLine("Creating downloads directory: " + itorrPath.getDownloadsPath());

            try     { fBasePath.mkdir(); isCreated = true; }
            catch   (SecurityException se) { }

            if (isCreated) 	{ addLogLine("Downloads directory created ✓"); return true; }
            else 			{ addLogLine("Failed creating downloads directory"); return false; }
        }
        else { addLogLine("Downloads directory exists ✓"); return true; }
    }

    // Core test function
    //==============================================================================
    private static void CoreTest() throws UndefinedPathException {
        itorrPath = new PathBuilder(OSDetector.getOS());

        URL location = Test.class.getProtectionDomain().getCodeSource().getLocation();
        System.out.println(location.getFile());

        FileManager f = new FileManager(location.getFile() + "TidePool.mp3");
        if(f.checkFile()){
            FormatManager.createFormatFile(testItorId, f);
        } else {
            System.out.println("Could not create .itor file\nCheck that the giving file exists on path\nAborting...");
            return;
        }

        FileBuilder fb = new FileBuilder(itorrPath.getTorrentsPath() + testItorId + ".itor", testItorId);
        fb.getServers().forEach(System.out::println);
        fb.addChunk(45, new byte[]{34,35,34,3});
    }

    // UPnP test function
    //==============================================================================
	private static void UPnPTest() throws IOException, SAXException, ParserConfigurationException, InterruptedException {
		addLogLine("Starting weupnp");

		GatewayDiscover gatewayDiscover = new GatewayDiscover();
		addLogLine("Looking for Gateway Devices...");

		Map<InetAddress, GatewayDevice> gateways = gatewayDiscover.discover();

		if (gateways.isEmpty()) {
			addLogLine("No gateways found");
			addLogLine("Stopping weupnp");
			return;
		}
		addLogLine(gateways.size() +" gateway(s) found\n");

		int counter=0;
		for (GatewayDevice gw: gateways.values()) {
			counter++;
			addLogLine("Listing gateway details of device #" + counter+
					"\n\tFriendly name: " + gw.getFriendlyName()+
					"\n\tPresentation URL: " + gw.getPresentationURL()+
					"\n\tModel name: " + gw.getModelName()+
					"\n\tModel number: " + gw.getModelNumber()+
					"\n\tLocal interface address: " + gw.getLocalAddress().getHostAddress() + "\n");
		}

		// choose the first active gateway for the tests
		GatewayDevice activeGW = gatewayDiscover.getValidGateway();

		if (null != activeGW) {
			addLogLine("Using gateway: " + activeGW.getFriendlyName());
		} else {
			addLogLine("No active gateway device found");
			addLogLine("Stopping weupnp");
			return;
		}


		// testing PortMappingNumberOfEntries
		Integer portMapCount = activeGW.getPortMappingNumberOfEntries();
		addLogLine("GetPortMappingNumberOfEntries: " + (portMapCount != null? portMapCount.toString() : "(unsupported)"));

		// testing getGenericPortMappingEntry
		PortMappingEntry portMapping = new PortMappingEntry();
		if (LIST_ALL_MAPPINGS) {
			int pmCount = 0;
			do {
				if (activeGW.getGenericPortMappingEntry(pmCount, portMapping))
					addLogLine("Portmapping #" + pmCount + " successfully retrieved (" + portMapping.getPortMappingDescription() + ":" + portMapping.getExternalPort() + ")");
				else{
					addLogLine("Portmapping #" + pmCount + " retrieval failed");
					break;
				}
				pmCount++;
			} while (portMapping != null);
		} else {
			if (activeGW.getGenericPortMappingEntry(0, portMapping))
				addLogLine("Portmapping #0 successfully retrieved (" + portMapping.getPortMappingDescription() + ":" + portMapping.getExternalPort() + ")");
			else
				addLogLine("Portmapping #0 retrival failed");
		}

		InetAddress localAddress = activeGW.getLocalAddress();
		addLogLine("Using local address: " + localAddress.getHostAddress());
		String externalIPAddress = activeGW.getExternalIPAddress();
		addLogLine("External address: " + externalIPAddress);

		addLogLine("Querying device to see if a port mapping already exists for port " + SAMPLE_PORT);

		if (activeGW.getSpecificPortMappingEntry(SAMPLE_PORT, GlobalVariables.UPNP_PROTOCOL_TCP, portMapping)) {
			addLogLine("Port " + SAMPLE_PORT + " is already mapped. Aborting test.");
			return;
		} else {
			addLogLine("Mapping free. Sending port mapping request for ports " + SAMPLE_PORT + "-" + (SAMPLE_PORT + 10));

            for (int i = 0; i <= 10; ++i) {
                // test static lease duration mapping
                if (activeGW.addPortMapping(SAMPLE_PORT + i, SAMPLE_PORT + i, localAddress.getHostAddress(),
                        GlobalVariables.UPNP_PROTOCOL_TCP, "test")) {
                    addLogLine("Mapping SUCCESSFUL on port " + (SAMPLE_PORT + i) + " ✓");
                }
            }

            addLogLine("Waiting " + WAIT_TIME + " seconds before removing mapping...");
            Thread.sleep(1000 * WAIT_TIME);

            for (int i = 0; i <= 10; ++i) {
                if (activeGW.deletePortMapping(SAMPLE_PORT + i, GlobalVariables.UPNP_PROTOCOL_TCP)) {
                    addLogLine("Port " + (SAMPLE_PORT + i) + ": mapping removed, test SUCCESSFUL ✓");
                } else {
                    addLogLine("Port mapping removal FAILED");
                }
            }
		}
		addLogLine("Stopping weupnp");
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
