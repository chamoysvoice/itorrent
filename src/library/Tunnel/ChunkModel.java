package library.Tunnel;

import com.sun.istack.internal.Nullable;

import java.io.Serializable;

/**
 * Created by Leind on 04/06/2015.
 */
public class ChunkModel implements Serializable {
    private byte[] data;
    private long fileID;
    private long chunkID;

    public ChunkModel() {}

    public ChunkModel(byte[] data, long fileID, long chunkID) {
        this.data = data.clone();
        this.fileID = fileID;
        this.chunkID = chunkID;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public long getFileID() {
        return fileID;
    }

    public void setFileID(long fileID) {
        this.fileID = fileID;
    }

    public long getChunkID() {
        return chunkID;
    }

    public void setChunkID(long chunkID) {
        this.chunkID = chunkID;
    }
}
