package test;

import java.io.*;
import java.net.Socket;
import java.net.URL;

/**
 * Created by Leind on 26/05/2015.
 */
public class Client {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        Socket socket = null;
        String host = "127.0.0.1";

        URL location = Test.class.getProtectionDomain().getCodeSource().getLocation();
        System.out.println(location.getFile());

        socket = new Socket(host, 6992);

        File file = new File(location.getFile() + "test.txt");
        // Get the size of the file
        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            System.out.println("File is too large.");
        }
        byte[] bytes = new byte[(int) length];
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());

        int count;

        while ((count = bis.read(bytes)) > 0) {
            out.write(bytes, 0, count);
        }

        out.flush();
        out.close();
        fis.close();
        bis.close();
        socket.close();

    }
}