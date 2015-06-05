package library.Tunnel;

import library.FileBuilder;
import test.Start;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jesus on 05/06/2015.
 */
public class PeneErecto implements Runnable {
    private volatile boolean isStop = false;
    private List<FileBuilder> fileBuildersList = new ArrayList<>();
    private long fileID;
    private long chunkID;

    public PeneErecto() { this.fileBuildersList.addAll(Start.getFileBuilders()); }
    @Override
    public void run() {
        while (!isStop) {
            System.out.println("Checking");
            // If not complete
            fileBuildersList.stream().filter(fileBuilder -> !fileBuilder.isComplete()).forEach(fileBuilder -> {
                // If not complete
                fileID = fileBuilder.getFileID();
                chunkID = fileBuilder.searchMissingChunk();

                Chunk chunk = new Chunk();
                chunk.request(fileID, chunkID).start();

                System.out.println("Chunk id: " + chunkID);
                System.out.println("File id: " + fileID);
            });

            try {
                System.out.println("Waiting");
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopMe() {
        this.isStop = true;
    }
}
