import java.io.File;

public class PathUtil {

    public static String projectDir;

    public static String buildDir;

    public static String getSelfPath() {
        String selfPath = buildDir + File.separator + "self";
        File self = new File(selfPath);
        if (!self.exists()) {
            self.mkdirs();
        }
        return selfPath;
    }

    public static String getUnZipPath() {
        String unzipPath = buildDir + File.separator + "self" + File.separator + "unzip";
        File unzip = new File(unzipPath);
        if (!unzip.exists()) {
            unzip.mkdirs();
        }
        return unzipPath;
    }
}
