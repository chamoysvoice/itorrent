package library.Tunnel.PairsConnect;

/**
 * Created by Leind on 04/06/2015.
 */
public interface PairListener {
    public void onPairConnected(String message, long fileID, long chunkID);
}
