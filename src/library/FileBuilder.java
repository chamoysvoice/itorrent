package library;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;

public class FileBuilder {

	private ArrayList<String> servers;
	private boolean[] chunk_status;
	private int number_of_chunks;
	private int chunk_size;
	private long id;
	private String temp_file_path;
	
	
	public FileBuilder(String path, long id){
		
		this.id = id;
		this.temp_file_path = GlobalVariables.TORRENT_TEMP_URL + this.id + ".dt";
		
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
		File temp_file = new File(GlobalVariables.TORRENT_TEMP_URL + this.id + ".dt");
		if(!temp_file.exists()){
			try {
				
				file_writer = new PrintWriter((GlobalVariables.TORRENT_TEMP_URL + this.id + ".dt"), "UTF-8");
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
	
	public void buildChunk(){
		
		File f = new File(this.temp_file_path);
		String letter;
		int i = 0, status;
		try(BufferedReader br = new BufferedReader(new FileReader(f))) {
		    for(String line; (line = br.readLine()) != null; ) {
		    	letter = line.substring(0,1);
		    	if(letter.equals("c")){
		    		String[] parts = line.split("=");
		    		String lastDigit = parts[parts.length - 1].trim();
		    		status = Integer.parseInt(lastDigit);
		    		
		    		if(status != GlobalVariables.FOUND){
		    			/*
		    			 * Pending!!!!
		    			 * 
		    			 * Here goes the function that is going to get #n chunk in case is needed
		    			 * 
		    			 */
		    			System.out.println("need to download " + i);
		    		}
		    		i++;
		    	}
		    }
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
