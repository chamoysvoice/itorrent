package test;

import library.FileManager;
import library.FormatManager;

public class Test {
	public static void main(String[] args) {
		
		FileManager f = new FileManager("C:/Users/Jesus/Downloads/AtomSetup.exe");
		if(f.checkFile()){
			FormatManager.createFormatFile("test.itor", f);
		} else {
			System.out.println("File Not Found");
		}
	}
}
