//import org.dom4j.Attribute;
//import org.dom4j.Document;
//import org.dom4j.DocumentHelper;
//import org.dom4j.Element;
//import org.dom4j.io.SAXReader;
//import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.List;

public class XmlUtil {

    public static void process(String filePath) {
//        LogUtil.log(" XmlUtil process " + filePath);
//        try {
//            File srcXml = new File(filePath);
//            SAXReader saxReader = new SAXReader();
//            Document src = saxReader.read(srcXml);
//            Document dest = DocumentHelper.createDocument();
//
//            Element srcRootElement = src.getRootElement();
//            String srcRootName = srcRootElement.getName();
//            if (Mapping.maps.keySet().contains(srcRootName)) {
//                srcRootName = Mapping.maps.get(srcRootName);
//            }
//
//            Element destRootElement = dest.addElement(srcRootName);
//            destRootElement.add(srcRootElement.getNamespaceForPrefix("android"));
//
//            copy(srcRootElement, destRootElement);
//            scan(srcRootElement, destRootElement);
//            Writer fileWriter = new FileWriter(filePath);
//            XMLWriter xmlWriter = new XMLWriter(fileWriter);
//            xmlWriter.write(dest);
//            xmlWriter.close();
//        } catch (Throwable throwable) {
//            LogUtil.log(" XmlUtil process err "+throwable.getMessage());
//        }
    }

//    private static void copy(Element src, Element dest) {
//        List<Attribute> srcAttributes = src.attributes();
//        for (Attribute srcAttribute : srcAttributes) {
//            String value = srcAttribute.getValue();
//            if (Mapping.maps.keySet().contains(value)) {
//                value = Mapping.maps.get(value);
//            }
//            dest.addAttribute(srcAttribute.getQName(), value);
//        }
//
//        dest.setText(src.getText());
//        dest.setData(src.getData());
//    }
//
//    private static void scan(Element src, Element dest) {
//        List<Element> srcElements = src.elements();
//        for (Element srcElement : srcElements) {
//            String srcName = srcElement.getName();
//            if (Mapping.maps.keySet().contains(srcName)) {
//                srcName = Mapping.maps.get(srcName);
//            }
//            Element destElement = dest.addElement(srcName);
//            copy(srcElement, destElement);
//            scan(srcElement, destElement);
//        }
//    }

}
