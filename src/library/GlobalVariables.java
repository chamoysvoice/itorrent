package library;

import java.util.ArrayList;
import java.util.List;

public class GlobalVariables {
	public static final String BASE_URL = "C:/itorr/";
	public static final String TORRENT_URL = GlobalVariables.BASE_URL + "Itorr/";
	public static final String TORRENT_TEMP_URL = GlobalVariables.TORRENT_URL + "Temp/";
	public static final String DOWNLOADS_URL = GlobalVariables.BASE_URL + "Downloads/";
	public static final String TEMP_URL = GlobalVariables.BASE_URL + "Temp/";

	public static final int KILOBYTE = 1024;
	public static final int MEGABYTE = 1048576;
	public static final int FOUND = 1;
	
	public static ArrayList<String> servers = new ArrayList<String>();
	
	public static String current_server = "";

	// UPnP
	//======================== ======================================================
	public static final String UPNP_PROTOCOL_TCP = "TCP";
	public static final String UPNP_PROTOCOL_UDP = "UDP";

	// Ports
	//===============================================================================
	public static final int SERVER_PORT = 6992;
	public static final int SERVER_PAIR_PORT = 6999;

	// Handshake with pairs
	//===============================================================================
	public static final String poke = "hi";
}
