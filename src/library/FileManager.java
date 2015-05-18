package library;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;

public class FileManager {
	private File file;
	private String path;
	private int chunkSize;
	private String name;
	private boolean chunk_status[];

	public int getChunkSize(){
		return this.chunkSize;
	}
	
	public FileManager(String path) {
		this.file = new File(path);
		this.path = path;
		long size = this.file.length() / 1024 / 1024; // get it on MB
		String []route = path.split("/");
		this.name = route[route.length-1];
		
		/*
		 * Files below 100 mb are 256 kb each chunk
		 * Files between 100 mb and 500 mb are 512 kb each chunk
		 * Files above 500 mb will have chunks of 1 mb of size.
		 */
		
		if(size < 100){
	        this.chunkSize = 1024 * 256; // make each chunk 256 kb
		} else if (size < 500){ 			
			this.chunkSize = 1024 * 512; // make each chunk 512 kb
		} else {
			this.chunkSize = 1024 * 1024; // make each chunk 1 mb
		}
		
		this.chunk_status = new boolean[this.chunkSize];
		
		
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	
	public boolean checkFile() {
		if (file.exists()) {
			return true;
		} else {
			return false;
		}
	}

	public String getChunkMD5(int id){
		byte[] chunk = this.get_chunk(id);
		return Security.MD5(chunk);
	}
	
	public String getName(){
		return this.name;
	}
	
	public byte[] get_chunk(int id){
		RandomAccessFile raf;
		byte[] chunk = new byte[this.chunkSize];
		if(this.chunk_status[id] || true){ // It should have chunk status on ram someday. when it does please remove the true. 
			try{
				raf = new RandomAccessFile(this.file, "r");
				raf.seek(id * this.chunkSize);
				raf.read(chunk);
				raf.close();
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		return chunk;
		
	}



	public int count_chunks() {
		FileInputStream fi;
		int chunk_count = 0;
		byte[] chunk = new byte[this.chunkSize];

		try {
			fi = new FileInputStream(this.file);
			while (fi.read(chunk) != -1) {
				chunk_count++;
			}
			fi.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return chunk_count;
	}

}
