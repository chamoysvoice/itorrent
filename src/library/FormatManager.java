package library;

import java.io.PrintWriter;


public class FormatManager {

	public static int createFormatFile(String name, FileManager file){
		PrintWriter file_writer;
		long countChunks = file.count_chunks();
		
		try {
			file_writer = new PrintWriter(GlobalVariables.TORRENT_URL + name, "UTF-8");
			file_writer.write("125.25.62.25\n");
			
			for (int i = 0; i < countChunks; i++) {
				file_writer.write(i + " " + file.getChunkMD5(i) + "\n");
			}
			file_writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
