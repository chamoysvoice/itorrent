package test;

import library.FileBuilder;
import library.FileManager;
import library.FormatManager;
import library.GlobalVariables;


public class Test {
	public static void main(String[] args) {
		
		FileManager f = new FileManager("C:/Users/Jesus/Downloads/AtomSetup.exe");
		if(f.checkFile()){
			FormatManager.createFormatFile("18.itor", f);
		} else {
			System.out.println("File Not Found");
		}
		FileBuilder fb = new FileBuilder(GlobalVariables.TORRENT_URL+"18.itor", 18);
		for (String server: fb.getServers()){
			System.out.println(server);
		}
		fb.buildChunk();
	}
}
