package library;

import java.io.File;
import java.io.FileInputStream;

public class FileManager {
	private File file;
	private String path;
	private int chunkSize;
	private boolean chunk_status[];

	public int getChunkSize(){
		return this.chunkSize;
	}
	
	public FileManager(String path) {
		this.file = new File(path);
		this.path = path;
		long size = this.file.length() / 1024 / 1024;
		
		if(size < 100){
	        this.chunkSize = 1024 * 256;
		} else if (size < 500){
			this.chunkSize = 1024 * 512;
		} else {
			this.chunkSize = 1024 * 1024;
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
	
	public byte[] get_chunk(int id){
		FileInputStream fi;
		byte[] chunk = new byte[this.chunkSize];
		int i = 0;
		if(this.chunk_status[id] || true){
			try {
				fi = new FileInputStream(this.file);
				while (fi.read(chunk) != -1) {
					if(i == id){
						fi.close();
						return chunk;
					} i++;
				}
				fi.close();
			} catch (Exception e) {
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
