package library;

import java.net.*;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class Session extends Thread{
	
	private Random randomGenerator;
	private int choose;
	
	public static void main(String[] args){
		Session s = new Session();
		s.changeServer();
		s.keepSession();
	}
	
	public Session(){
		GlobalVariables.servers.add("http://45.55.151.143");
		GlobalVariables.servers.add("http://45.55.232.142");
		GlobalVariables.servers.add("http://46.101.38.180");
		
	}
	
	public  boolean keepSession(){
		if(testServer()){
			return true;
		} else {
			changeServer();
			return keepSession();
		}
	}
	
	public  void changeServer(){
		randomGenerator = new Random();
		choose = randomGenerator.nextInt(GlobalVariables.servers.size());
		while(!testServer()){
			System.out.println("Server is offline");
			choose = randomGenerator.nextInt(GlobalVariables.servers.size());
		}
		GlobalVariables.current_server = GlobalVariables.servers.get(choose);
	}
	
	public boolean testServer(){

		 try {
			 
				URL obj = new URL(GlobalVariables.servers.get(choose) + "/session/init.php");
				URLConnection conn = obj.openConnection();
				Map<String, List<String>> map = conn.getHeaderFields();			 
				
				String server = conn.getHeaderField("Server");
			 
				if (server == null) {
					return false;
				} else {
					System.out.println(GlobalVariables.servers.get(choose) + "/session/init.php is online");
				}
					
			 
			 
			    } catch (Exception e) {
				e.printStackTrace();
			    }
		 	return true;
		}
	
}
