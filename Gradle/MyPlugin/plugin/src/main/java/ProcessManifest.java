//import org.dom4j.Document;
//import org.dom4j.DocumentException;
//import org.dom4j.Element;
//import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.List;

public class ProcessManifest {

    static String pkg;

    public static void processSrc(File manifest) {
//        if (!manifest.exists()) {
//            return;
//        }
//        SAXReader saxReader = new SAXReader();
//        try {
//            Document document = saxReader.read(manifest);
//            Element rootElement = document.getRootElement();
//            pkg = rootElement.attributeValue("package");
//            scan(rootElement);
//        } catch (DocumentException e) {
//            LogUtil.log("processManifest DocumentException " + e.getMessage());
//        }
    }

//    static void scan(Element rootElement) {
//        List<Element> elements = rootElement.elements();
//        for (Element element : elements) {
//            if (element.getName().equals("activity") || element.getName().equals("service")
//                    || element.getName().equals("receiver") || element.getName().equals("provider")) {
//                String className = element.attributeValue("name");
//                if (className.startsWith(".")) {
//                    className = pkg + className;
//                }
//
//                String randomClassName;
//                do {
//                    randomClassName = RandomUtil.randomClassName();
//                } while (Mapping.maps.values().contains(randomClassName));
//
//                if (!Mapping.maps.keySet().contains(className)) {
//                    LogUtil.log(" mapping put " + className + "  ->  " + randomClassName);
//                    Mapping.maps.put(className, randomClassName);
//                }
//
//            }
//            if (element.elements().size() > 0) {
//                scan(element);
//            }
//        }
//    }

    public static void processMerge(File file) {
        XmlUtil.process(file.getAbsolutePath());
    }
}
