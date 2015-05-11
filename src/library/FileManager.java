package library;

import java.io.File;
import java.io.FileInputStream;

public class FileManager {
	private File file;
	private int chunkSize;

	public FileManager(String path) {
		this.file = new File(path);
	}

	public boolean checkFile() {
		if (file.exists()) {
			return true;
		} else {
			return false;
		}
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
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
