import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProcessAaptRules {

    public static void processAaptRules(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File file1 : files) {
                    if (file1.isDirectory()) {
                        processAaptRules(file1);
                    } else {
                        process(file1);
                    }
                }
            }
        } else {
            process(file);
        }
    }

    private static void process(File file) {
        if (file.getName().equals("aapt_rules.txt")) {
            List<String> lines = new ArrayList<>();
            for (Map.Entry<String, String> entry : Mapping.maps.entrySet()) {
                lines.add(entry.getKey() + " -> " + entry.getValue() + ":");
            }

            try {
                FileUtils.writeLines(new File(file.getParent() + File.separator + "applymapping"), lines);


                List<String> ruleLines = FileUtils.readLines(file);
                ruleLines.add("\n\n\n");
                ruleLines.add("-applymapping applymapping");
                FileUtils.writeLines(file, ruleLines);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
