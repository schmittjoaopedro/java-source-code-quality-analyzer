package com.github.schmittjoaopedro.analyser.checkstyle;

import com.github.schmittjoaopedro.model.CheckstyleMetric;
import com.github.schmittjoaopedro.mcc.utils.MccUtils;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CheckstyleAnalyser {

    private static Logger logger = LogManager.getLogger(CheckstyleAnalyser.class);

    public List<CheckstyleMetric> analyse(String sourceCode) {
        String className = MccUtils.extractClassNameFromSourceCode(sourceCode);
        Path filePath = Paths.get("temp_checkstyle", className + ".java");
        try {
            FileUtils.write(new File(filePath.toString()), sourceCode);
            List<File> files = new ArrayList<>();
            files.add(new File(filePath.toString()));
            Checker checker = new Checker();
            checker.setModuleClassLoader(Checker.class.getClassLoader());
            checker.configure(ConfigurationLoader.loadConfiguration("/weg_checks.xml", new PropertiesExpander(System.getProperties())));
            CheckstyleListener checkstyleListener = new CheckstyleListener();
            checker.addListener(checkstyleListener);
            checker.process(files);
            checker.destroy();
            return checkstyleListener.getCheckstyleMetrics();
        } catch (Exception e) {
            logger.error(e);
            return null;
        } finally {
            try {
                FileUtils.forceDelete(new File(filePath.toString()));
            } catch (IOException e) {
                logger.error(e);
            }
        }
    }

}
