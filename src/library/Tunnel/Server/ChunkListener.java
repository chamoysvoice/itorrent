package library.Tunnel.Server;

import library.Tunnel.Chunk;
import library.Tunnel.ChunkModel;

/**
 * Created by Leind on 04/06/2015.
 */
public interface ChunkListener {
    public void onChunkReceived(ChunkModel chunk);
}
