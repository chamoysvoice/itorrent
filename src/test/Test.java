package test;

import library.FileManager;

public class Test {
	public static void main(String[] args) {
		FileManager f = new FileManager("/home/jesus/Downloads/YOOtheme/YOOtheme.zip");
		if(f.checkFile()){
			System.out.println(f.count_chunks());
			System.out.println(f.getFile().length() / (10000));
		} else {
			System.out.println("File Not Found");
		}
	}
}
