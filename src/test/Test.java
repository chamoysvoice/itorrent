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
import java.io.IOException;
import java.net.InetAddress;
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

	public static void main(String[] args) throws InterruptedException, SAXException, ParserConfigurationException, IOException, UndefinedPathException {
        CheckFoldersTest();
        CoreTest();
        //UPnPTest();
    }

    private static void CheckFoldersTest() throws UndefinedPathException {
        itorrPath = new PathBuilder(OSDetector.getOS());

    }

    // Core test function
    //==============================================================================
    private static void CoreTest() throws UndefinedPathException {
        itorrPath = new PathBuilder(OSDetector.getOS());

        FileManager f = new FileManager("TidePool.mp3");
        if(f.checkFile()){
            FormatManager.createFormatFile("18.itor", f);
        } else {
            System.out.println("File Not Found");
        }
        FileBuilder fb = new FileBuilder(itorrPath.getBasePath() + "18.itor", 18);
        for (String server: fb.getServers()){
            System.out.println(server);
        }
        fb.buildChunk();
    }

    // UPnP test function
    //==============================================================================
    private static void UPnPTest() {
        try {
            StartTestUPnP();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * UPnP test function
     *
     * @returns Just prints out if everything worked fine or not
     */
	private static void StartTestUPnP() throws IOException, SAXException, ParserConfigurationException, InterruptedException {
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
			} while (portMapping!=null);
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
