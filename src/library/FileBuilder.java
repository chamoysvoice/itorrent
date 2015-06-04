package library;

import library.Utils.OSDetector;
import library.Utils.PathBuilder;
import library.Utils.UndefinedPathException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

public class FileBuilder {

	private ArrayList<String> servers;
	private boolean[] chunk_status;
	private int number_of_chunks;
	private int chunk_size;
	private long id;
	private String temp_file_path;
	private PathBuilder itorrPath;
	private String name;
	private boolean complete;

	public FileBuilder(String path, long id) throws UndefinedPathException {
		this.complete = false;
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
		        	this.chunk_size = Integer.parseInt(line.substring(1)) * GlobalVariables.KILOBYTE;
		        } else if(letter.equals("n")){
		        	this.name = line.substring(1);
		        }
		    }
		} catch (Exception e){
			e.printStackTrace();
		}
		this.number_of_chunks = count_chunks;
		//make a temporary "itor" registry file, to keep record from downloads success
		PrintWriter file_writer;
		File temp_file = new File(itorrPath.getTempPath() + this.id + ".dt");
		if(!temp_file.exists()){
			try {

				file_writer = new PrintWriter((itorrPath.getTempPath() + this.id + ".dt"), "UTF-8");
				file_writer.write("s"+count_chunks+"\n");
				file_writer.write("b=\n");

				for (int i = 0; i < count_chunks; i++) {
					file_writer.write("c"+ i + "="+ 0+"\n");
				}
				file_writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			notifyStatus();
		}
	}

	public boolean isComplete() {
		return this.complete;
	}

	public void notifyStatus() {
		File f = new File(this.temp_file_path);
		String letter;
		boolean full = true;
		int i = 0, status;
		try (BufferedReader br = new BufferedReader(new FileReader(f))) {
			for (int j = 0; j < 2; j++) {
				for (String line; (line = br.readLine()) != null; ) {
					letter = line.substring(0, 1);
					if (i == this.number_of_chunks) break; // leave the last chunk pending so we can glue it
					if (letter.equals("c")) {
						String[] parts = line.split("=");
						String lastDigit = parts[parts.length - 1].trim();
						status = Integer.parseInt(lastDigit);

						if (status != GlobalVariables.FOUND) {
							full = false;
						}
						if (j == 1 && status == GlobalVariables.FOUND) {
							notifyChunkToServer((long) i);
						}
						i++;
					}
				}
				if (full) {
					notifySeeder();
					this.complete = true;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void notifySeeder() throws Exception {

		String url = GlobalVariables.current_server + "/session/seeder.php";

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		String urlParameters = "id=" + this.id + "&sid=" + Session.session_id;

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();

		BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
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
				if(i == this.number_of_chunks) break; // leave the last chunk pending so we can glue it 
				if(letter.equals("c")){
					String [] parts = line.split("=");
					String lastDigit = parts[parts.length - 1].trim();
					status = Integer.parseInt(lastDigit);
					
					if(status != GlobalVariables.FOUND && i != this.number_of_chunks - 1){
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
	
	private void notifyChunkToServer(long chunk) throws Exception {

		String url = GlobalVariables.current_server + "/session/peer.php";

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		String urlParameters = "id=" + this.id + "&chunk=" + chunk + "&sid=" + Session.session_id;

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();

		BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
	}
	
	public boolean isLastChunk(){
		String last = "", line;
		File temp_file = new File(itorrPath.getTempPath() + this.id + ".dt");
		int status;
		try {
			BufferedReader in = new BufferedReader(new FileReader(temp_file));
			line = in.readLine();
			while (line != null){
				last = line;
				line = in.readLine();
			}
			in.close();
			
			String [] parts = last.split("=");
			String lastDigit = parts[parts.length - 1].trim();
			status = Integer.parseInt(lastDigit);
			
			if(status != GlobalVariables.FOUND){
				return false;
			} else {
				return true;
			}
			
		} catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean addChunk(long id, byte[] chunk){

		File temp_file = new File(itorrPath.getTempPath() + this.id + ".dt");
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
		
		try{
			FileOutputStream file_out = new FileOutputStream(new File(itorrPath.getTempPath() + this.id + ".tmp"), true);
			
			file_out.write(chunk);
			file_out.close();
			notifyChunkToServer(id);
		} catch(Exception e){
			e.printStackTrace();
		}
		
		
		return true;
	}
	
	public byte[] getChunk(long id){
		File dt_file = new File(itorrPath.getTempPath() + this.id + ".dt");
		File src_file = new File(itorrPath.getTempPath() + this.id + ".tmp");
		String line, t[], order[];
		byte[] chunk = new byte[this.chunk_size];
		int j = 0;
		try{
			RandomAccessFile raf = new RandomAccessFile(src_file, "r");

			BufferedReader dt_in = new BufferedReader(new FileReader(dt_file));
			dt_in.readLine();
			line = dt_in.readLine();
			t = line.split("=");
			order = t[1].split(",");
			for(int i = 0; i < order.length; i++){
				
				
				
				if(i == id){
					if(i == this.number_of_chunks - 1){ // this handles the last chunk (because is not fully sized)
						raf.seek(i*  this.chunk_size);
						while(raf.read() != -1 && j < this.chunk_size){
							j++;
						}
						raf.seek(i * this.chunk_size);
						chunk = new byte[j];
						raf.read(chunk);
						continue;
					}
					raf.seek(i * this.chunk_size);
					raf.read(chunk);
				}
			}
			dt_in.close();
			raf.close();
			
		}	catch (Exception e){
			e.printStackTrace();
		}
	
		
		return chunk;


	}


	public boolean moveToDownloads(){
		if(this.isLastChunk()){
			File dst_file = new File(itorrPath.getDownloadsPath()+this.name);
			File dt_file = new File(itorrPath.getTempPath() + this.id + ".dt");
			File src_file = new File(itorrPath.getTempPath() + this.id + ".tmp");
			String line,t[], order[]; 
			try{
				BufferedReader dt_in = new BufferedReader(new FileReader(dt_file));
				BufferedReader src_in = new BufferedReader(new FileReader(src_file));
				FileOutputStream dst_out = new FileOutputStream(dst_file);
				
				dt_in.readLine();
				line = dt_in.readLine();
				t = line.split("=");
				order = t[1].split(",");
				System.out.println(order.length);
				for(int i = 0; i < order.length; i++){
					for(int j = 0; j < order.length; j++){
						System.out.println(i +" " +  j);
						if(i == Integer.parseInt(order[j])){
							dst_out.write(this.getChunk(j));
							break;
						}
					}
				}
				dt_in.close();
				dst_out.close();
				src_in.close();
				this.complete = true;
				notifySeeder();
			} catch(Exception e){
				e.printStackTrace();
				return false;
			}
			
			return true;
		} else {
			System.out.println("File "+ this.id +":" + this.name +  " is not ready to be moved to Downloads dir");
			return false;
		}
	}
}
