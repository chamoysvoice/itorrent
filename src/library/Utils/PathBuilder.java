package library.Utils;

/**
 * Created by leind on 14/05/15.
 */
public class PathBuilder {
    OSDetector.OperativeSytem os;
    private String base_path;
    private String torrents_path;
    private String downloads_path;
    private String temp_path;

    /**
     * Ctor
     */
    public PathBuilder (OSDetector.OperativeSytem os) throws UndefinedPathException {
        this.os = os;
        this.base_path = this.setBasePath();
    }

    /**
     * Retrieves the base path according to the user OS
     * and sets it to base_path member variable
     *
     * @return base itorr path
     * @throws UndefinedPathException
     */
    private String setBasePath() throws UndefinedPathException {
        switch (os) {
            case WINDOWS:
                this.base_path = "C:/itorr/";
                break;
            case UNIX:
                this.base_path = System.getProperty("user.home") + "/itorr/";
                break;
            case OSX:
                this.base_path = System.getProperty("user.home") + "/itorr/";
                break;
            case UNKNOWN:
                throw new UndefinedPathException("Cannot define a directory. Undefined OS");
        }
        return this.base_path;
    }

    public String getBasePath() {
        return this.base_path;
    }

    public String getTorrentsPath() {
        return this.torrents_path = base_path + "Itorr/";
    }

    public String getTempPath() {
        return this.temp_path = base_path + "Temp/";
    }
    
    public String getTorrentTempPath() {
        return this.temp_path = base_path + "Itorr/Temp/";
    }

    public String getDownloadsPath() {
        return this.downloads_path = base_path + "Downloads/";
    }
}
