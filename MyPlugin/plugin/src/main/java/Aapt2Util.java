import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Aapt2Util {
    public static String getAapt2Path() {
        FileInputStream fileInputStream = null;
        try {
            String localProperties = new File(PathUtil.projectDir).getParent() + File.separator + "local.properties";
            LogUtil.log("getAapt2Path  localProperties path " + localProperties);
            fileInputStream = new FileInputStream(new File(localProperties));
            Properties properties = new Properties();
            properties.load(fileInputStream);
            String sdkPath = properties.getProperty("sdk.dir");
            File aapt2ParentFile = new File(sdkPath + File.separator + "build-tools");
            File[] files = aapt2ParentFile.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    File[] files1 = file.listFiles();
                    for (File file1 : files1) {
                        if (file1.getName().equals("aapt2.exe")) {
                            return file1.getAbsolutePath();
                        }
                    }
                }
            }
        } catch (Throwable throwable) {
            LogUtil.log("getAapt2Path err " + throwable.getMessage());
        } finally {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;

    }
}
