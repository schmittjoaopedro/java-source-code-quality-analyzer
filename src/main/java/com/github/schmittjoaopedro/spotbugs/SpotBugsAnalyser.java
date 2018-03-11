package com.github.schmittjoaopedro.spotbugs;

import com.github.schmittjoaopedro.mcc.engine.MemoryClassCompiler;
import com.github.schmittjoaopedro.mcc.object.SourceClass;
import com.github.schmittjoaopedro.mcc.utils.MccUtils;
import edu.umd.cs.findbugs.BugCollection;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SpotBugsAnalyser {

    private static Logger logger = LogManager.getLogger(SpotBugsAnalyser.class);

    public void analyse(String sourceCode) {
        String packageName = MccUtils.extractPackageFromSourceCode(sourceCode);
        String className = MccUtils.extractClassNameFromSourceCode(sourceCode);
        Path filePath = Paths.get("temp", className + ".class");
        try {
            SourceClass sourceClass = new SourceClass(packageName, className, sourceCode);
            MemoryClassCompiler compiler = new MemoryClassCompiler();
            compiler.compile(sourceClass);
            FileUtils.writeByteArrayToFile(new File(filePath.toString()), sourceClass.getBytecode());
            AnalysisRunnerSource runner = new AnalysisRunnerSource();
            BugCollection bugCollection = runner.run(filePath).getBugCollection();
            System.out.println(bugCollection);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                FileUtils.forceDelete(new File(filePath.toString()));
            } catch (IOException e) {
                logger.error(e);
            }
        }
    }

}
