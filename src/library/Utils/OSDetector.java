package library.Utils;

import library.GlobalVariables;

/**
 * Created by leind on 14/05/15.
 */
public class OSDetector {
    private static String OS = System.getProperty("os.name").toLowerCase();
    public enum OperativeSytem { WINDOWS, UNIX, OSX, UNKNOWN }

    public static OperativeSytem getOS() {
        if (isWindows())    { return OperativeSytem.WINDOWS; }
        else if (isMac())   { return OperativeSytem.OSX; }
        else if (isUnix())  { return OperativeSytem.UNIX; }
        else { return OperativeSytem.UNKNOWN; }
    }

    public static boolean isWindows() { return (OS.indexOf("win") >= 0); }

    public static boolean isMac() { return (OS.indexOf("mac") >= 0); }

    public static boolean isUnix() { return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 ); }
}
