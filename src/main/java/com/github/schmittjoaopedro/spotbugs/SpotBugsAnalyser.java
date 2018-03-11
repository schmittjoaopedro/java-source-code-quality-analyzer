package com.github.schmittjoaopedro.spotbugs;

import com.github.schmittjoaopedro.mcc.engine.MemoryClassCompiler;
import com.github.schmittjoaopedro.mcc.object.SourceClass;
import com.github.schmittjoaopedro.mcc.utils.MccUtils;
import com.github.schmittjoaopedro.metrics.SpotBugsMetrics;
import edu.umd.cs.findbugs.BugCollection;
import edu.umd.cs.findbugs.BugInstance;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SpotBugsAnalyser {

    private static Logger logger = LogManager.getLogger(SpotBugsAnalyser.class);

    public List<SpotBugsMetrics> analyse(String sourceCode) {
        String packageName = MccUtils.extractPackageFromSourceCode(sourceCode);
        String className = MccUtils.extractClassNameFromSourceCode(sourceCode);
        Path filePath = Paths.get("temp_spotbugs", className + ".class");
        try {
            List<SpotBugsMetrics> metrics = new ArrayList<>();
            SourceClass sourceClass = new SourceClass(packageName, className, sourceCode);
            MemoryClassCompiler compiler = new MemoryClassCompiler();
            compiler.compile(sourceClass);
            FileUtils.writeByteArrayToFile(new File(filePath.toString()), sourceClass.getBytecode());
            AnalysisRunnerSource runner = new AnalysisRunnerSource();
            BugCollection bugCollection = runner.run(filePath).getBugCollection();
            for (BugInstance bugInstance : bugCollection.getCollection()) {
                metrics.add(new SpotBugsMetrics(
                    bugInstance.getMessageWithoutPrefix(),
                    bugInstance.getBugPattern().getCategory(),
                    bugInstance.getBugRank(),
                    bugInstance.getPriority(),
                    bugInstance.getPriorityString()
                ));
            }
            return metrics;
        } catch (Exception e) {
            logger.error(e);
        } finally {
            try {
                FileUtils.forceDelete(new File(filePath.toString()));
            } catch (IOException e) {
                logger.error(e);
            }
        }
        return null;
    }

}
