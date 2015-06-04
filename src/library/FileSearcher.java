package library;

import jdk.nashorn.internal.parser.JSONParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class FileSearcher {
    public static ArrayList<String> searchChunk(long file_id, long chunk_id) throws IOException {
        ArrayList<String> ip_addresses = new ArrayList<String>();
        String url = GlobalVariables.current_server + "/torrent/getChunk.php?id=" + file_id + "&chunk=" + chunk_id;

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        String t = response.toString();
        t = t.substring(1, t.length() - 1);
        String[] parts = t.split(",");
        for (String a : parts) {
            //System.out.println(a);
            ip_addresses.add(a);
        }
        return ip_addresses;
    }
}
