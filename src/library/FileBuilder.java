package library;

import library.Utils.OSDetector;
import library.Utils.PathBuilder;
import library.Utils.UndefinedPathException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;

public class FileBuilder {

	private ArrayList<String> servers;
	private boolean[] chunk_status;
	private int number_of_chunks;
	private int chunk_size;
	private long id;
	private String temp_file_path;
	private PathBuilder itorrPath;
	
	public FileBuilder(String path, long id) throws UndefinedPathException {
		this.itorrPath = new PathBuilder(OSDetector.getOS());
		this.id = id;
		this.temp_file_path = itorrPath.getTempPath() + this.id + ".dt";
		
		// Get data "servers and chunks" from itor file
		File f = new File(path);
		String letter;
		String placeholder;
		int count_chunks = 0;
		servers = new ArrayList<String>();

		try(BufferedReader br = new BufferedReader(new FileReader(f))) {
		    for(String line; (line = br.readLine()) != null; ) {
		    	letter = line.substring(0,1);
		    	if(letter.equals("s")){
		    		placeholder = line.substring(1);
		        	this.addServer(placeholder);
		        } else if(letter.equals("c")){
		        	count_chunks++;
		        } else if(letter.equals("z")){
		        	this.chunk_size = Integer.parseInt(line.substring(1));
		        }
		    }
		} catch (Exception e){
			e.printStackTrace();
		}
		
		//make a temporary "itor" registry file, to keep record from downloads success
		PrintWriter file_writer;
		File temp_file = new File(itorrPath.getTempPath() + this.id + ".dt");
		if(!temp_file.exists()){
			try {
				
				file_writer = new PrintWriter((itorrPath.getTempPath() + this.id + ".dt"), "UTF-8");
				file_writer.write("s"+count_chunks+"\n");
				
				for (int i = 0; i < count_chunks; i++) {
					file_writer.write("c"+ i + "="+ 0+"\n");
				}
				file_writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void addServer(String server){
		servers.add(server);
	}
	
	public ArrayList<String> getServers(){
		return this.servers;
	}
	
	public int searchMissingChunk(){
		File f = new File(this.temp_file_path);
		String letter;
		int i = 0, status;
		try(BufferedReader br = new BufferedReader(new FileReader(f))){
			for(String line; (line = br.readLine()) != null;){
				letter = line.substring(0,1);
				if(letter.equals("c")){
					String [] parts = line.split("=");
					String lastDigit = parts[parts.length - 1].trim();
					status = Integer.parseInt(lastDigit);
					
					if(status != GlobalVariables.FOUND){
						return i;
					}
					i++;
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return -1;
	}
	
	public boolean addChunk(long id, byte[] chunk){
		File temp_file = new File(itorrPath.getTorrentTempPath() + this.id + ".dt");
		String size, status = "", order = "", line, order_temp;
		int i = 0;
		try{
			BufferedReader in = new BufferedReader(new FileReader(temp_file));
			size = in.readLine();
			order_temp = in.readLine();
			if(order_temp.length() < 3){
				order = new String(order_temp.substring(0,2) + id);
			} else {
				order = new String(order_temp + "," +id);
			}
			line = in.readLine();
			while(line != null){
				if(i == id){
					status = new String(status + "c" + id + "=1\n");
				} else {
					status = new String(status + line +"\n");
				}
				i++;
				line = in.readLine();
			}
			
			in.close();
			BufferedWriter out = new BufferedWriter(new FileWriter(temp_file));
			out.write(size + "\n" +
					order + "\n" + status);
			out.close();
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
