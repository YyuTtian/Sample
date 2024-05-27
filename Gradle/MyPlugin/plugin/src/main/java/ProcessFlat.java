import net.lingala.zip4j.ZipFile;

import org.apache.commons.io.FileUtils;
//import org.dom4j.Document;
//import org.dom4j.DocumentException;
//import org.dom4j.Element;
//import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ProcessFlat {

    public static void processRes(File file) throws Throwable {
        FileUtils.copyDirectoryToDirectory(file, new File(PathUtil.getSelfPath()));
        processLayout(PathUtil.getSelfPath() + File.separator + "res" + File.separator + "layout");
        // 手动编译资源文件
        String aapt2Path = Aapt2Util.getAapt2Path();
        LogUtil.log("aapt2Path  " + aapt2Path);
        Process exec = Runtime.getRuntime().exec(aapt2Path + " compile --dir " + PathUtil.getSelfPath() + File.separator + "res -o " + PathUtil.getSelfPath() + File.separator + "res.zip");
        exec.waitFor();
        // 把编译好的文件解压到unzip文件夹下
        ZipFile zipFile = new ZipFile(PathUtil.getSelfPath() + File.separator + "res.zip");
        zipFile.extractAll(PathUtil.getUnZipPath());
    }

    // 用自己编译好的资源文件覆盖系统编译的资源文件
    public static void copyFlat(File dest) {
        isCopy = false;
        if (dest.isDirectory()) {
            File[] files = dest.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        copyFlat(file);
                    } else {
                        startCopy(file);
                    }
                }
            }
        } else {
            startCopy(dest);
        }
    }

    private static boolean isCopy;

    private static void startCopy(File flatFile) {
        if (isCopy) {
            return;
        }
        if (flatFile.getAbsolutePath().endsWith(".flat")) {
            LogUtil.log("copy flat");
            File src = new File(PathUtil.getUnZipPath());
            File[] files = src.listFiles();
            for (File file : files) {
                try {
                    FileUtils.copyFile(file, new File(flatFile.getParent() + File.separator + file.getName()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            isCopy = true;
        }
    }

    private static void processLayout(String layoutPath) {
        File layout = new File(layoutPath);
        File[] files = layout.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                parserXml(file);
                XmlUtil.process(file.getAbsolutePath());
            }
        }
    }

    private static void parserXml(File inputXml) {
//        SAXReader saxReader = new SAXReader();
//        try {
//            Document document = saxReader.read(inputXml);
//            Element rootElement = document.getRootElement();
//            if (rootElement.getName().contains(".")) {
//                String randomClassName;
//                do {
//                    randomClassName = RandomUtil.randomClassName();
//                } while (Mapping.maps.values().contains(randomClassName));
//
//                if (!Mapping.maps.keySet().contains(rootElement.getName())) {
//                    LogUtil.log(" mapping put " + rootElement.getName() + "  ->  " + randomClassName);
//                    Mapping.maps.put(rootElement.getName(), randomClassName);
//                }
//            }
//            scan(rootElement);
//        } catch (DocumentException e) {
//            LogUtil.log("parserXml err " + e.getMessage());
//        }
    }

//    public static void scan(Element rootElement) {
//        List<Element> elements = rootElement.elements();
//        for (Element element : elements) {
//            if (element.getName().contains(".") && !Mapping.maps.keySet().contains(element.getName())) {
//                String randomClassName;
//                do {
//                    randomClassName = RandomUtil.randomClassName();
//                } while (Mapping.maps.values().contains(randomClassName));
//
//                if (!Mapping.maps.keySet().contains(element.getName())) {
//                    LogUtil.log(" mapping put " + element.getName() + "  ->  " + randomClassName);
//                    Mapping.maps.put(element.getName(), randomClassName);
//                }
//            }
//            if (element.elements().size() > 0) {
//                scan(element);
//            }
//        }
//    }
}
