package library;

import library.Utils.OSDetector;
import library.Utils.PathBuilder;
import library.Utils.UndefinedPathException;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


public class FormatManager {

	public static int createFormatFile(FileManager file) throws UndefinedPathException {
		long id = file.id;
		PathBuilder itorrPath = new PathBuilder(OSDetector.getOS());
		PrintWriter file_writer;
		long countChunks = file.count_chunks();

		String itorFileName = id + ".itor";
        List<String> currentServers = new ArrayList<String>() {
            { add("45.55.151.143"); add("45.55.232.142"); add("46.101.38.180");}
        };
		
		try {
			file_writer = new PrintWriter(itorrPath.getTorrentsPath() + itorFileName, "UTF-8");
			file_writer.write("n" + file.getName()+"\n");
			file_writer.write("z" + (file.getChunkSize() / GlobalVariables.KILOBYTE) + "\n");
			for (String server: currentServers){
				file_writer.write("s" + server + "\n");
			}

			for (int i = 0; i < countChunks; i++) {
				file_writer.write("c"+ i + " " + file.getChunkMD5(i) + "\n");
			}
			file_writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	
}
