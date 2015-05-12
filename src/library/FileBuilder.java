package library;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class FileBuilder {

	private ArrayList<String> servers;
	//private boolean[] chunk_status;

	
	public FileBuilder(String path){
		
		
		File f = new File(path);
		String letter;
		String placeholder;
		servers = new ArrayList<String>();
		try(BufferedReader br = new BufferedReader(new FileReader(f))) {
		    for(String line; (line = br.readLine()) != null; ) {
		    	
		    	letter = line.substring(0,1);
		    	if(letter.equals("s")){
		    		placeholder = line.substring(1);
		        	this.addServer(placeholder);
		        } else if(line.equals("c")){
		        	System.out.println(Integer.parseInt(line.substring(1)));
		        }
		    }
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private void addServer(String server){
		servers.add(server);
	}
	
	public ArrayList<String> getServers(){
		return this.servers;
	}
}
