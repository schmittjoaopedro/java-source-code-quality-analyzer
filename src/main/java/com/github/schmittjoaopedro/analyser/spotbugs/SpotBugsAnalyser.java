package com.github.schmittjoaopedro.analyser.spotbugs;

public class SpotBugsAnalyser {

    /*
    private static Logger logger = LogManager.getLogger(SpotBugsAnalyser.class);

    public List<SpotBugsMetric> analyse(String sourceCode) throws Exception {
        String packageName = MccUtils.extractPackageFromSourceCode(sourceCode);
        String className = MccUtils.extractClassNameFromSourceCode(sourceCode);
        Path filePath = Paths.get("temp_spotbugs", className + ".class");
        try {
            List<SpotBugsMetric> metrics = new ArrayList<>();
            SourceClass sourceClass = new SourceClass(packageName, className, sourceCode);
            MemoryClassCompiler compiler = new MemoryClassCompiler();
            compiler.compile(sourceClass);
            FileUtils.writeByteArrayToFile(new File(filePath.toString()), sourceClass.getBytecode());
            AnalysisRunnerSource runner = new AnalysisRunnerSource();
            BugCollection bugCollection = runner.run(filePath).getBugCollection();
            for (BugInstance bugInstance : bugCollection.getCollection()) {
                metrics.add(new SpotBugsMetric(
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
            throw e;
        } finally {
            try {
                FileUtils.forceDelete(new File(filePath.toString()));
            } catch (IOException e) {
                logger.error(e);
            }
        }
    }
    */

}
