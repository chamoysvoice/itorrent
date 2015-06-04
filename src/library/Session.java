package library;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class Session extends Thread{

	public static String session_id;
	private Random randomGenerator;
	private int choose;

	public Session() {
		GlobalVariables.servers.add("http://45.55.151.143");
		GlobalVariables.servers.add("http://45.55.232.142");
		GlobalVariables.servers.add("http://46.101.38.180");

	}

	public static void main(String[] args){
		Session s = new Session();
		s.changeServer();
		s.keepSession();
	}

	public void run() {
		while (true) {
			keepSession();
			try {
				sleep(5 * 60 * 1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public  boolean keepSession(){
		if(testServer()){
			return true;
		} else {

			changeServer();

			return keepSession();
		}
	}

	private void changeSessionId() throws Exception {

		String url = GlobalVariables.current_server + "/session/init.php";
		System.out.println(GlobalVariables.current_server + "/session/init.php");
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		String urlParameters = "";

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		this.session_id = response.toString();

	}

	public void changeServer() {
		randomGenerator = new Random();
		choose = randomGenerator.nextInt(GlobalVariables.servers.size());
		while(!testServer()){
			System.out.println("Server is offline");
			choose = randomGenerator.nextInt(GlobalVariables.servers.size());
		}
		GlobalVariables.current_server = GlobalVariables.servers.get(choose);
		try {
			changeSessionId();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
