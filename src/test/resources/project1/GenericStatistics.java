package simulator.utils;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GenericStatistics {

    private int trialSize;

    private int iterationSize;

    private Map<Integer, Map<Integer, Double>> mean = new HashMap<>();

    private Map<Integer, Map<Integer, Double>> worst = new HashMap<>();

    private Map<Integer, Map<Integer, Double>> best = new HashMap<>();

    private Map<Integer, Map<Integer, Double>> bsf = new HashMap<>();

    private Map<Integer, Map<Integer, Double>> div = new HashMap<>();

    private Map<Integer, Map<Integer, Double>> branch = new HashMap<>();

    private String fileName;

    public GenericStatistics(int iterationSize, int trialSize, String fileName) {
        this.iterationSize = iterationSize;
        this.trialSize = trialSize;
        this.fileName = fileName;
        startStructure(mean);
        startStructure(worst);
        startStructure(best);
        startStructure(bsf);
        startStructure(div);
        startStructure(branch);
    }

    private void startStructure(Map<Integer, Map<Integer, Double>> structure) {
        for(int i = 0; i < iterationSize; i++) {
            structure.put(i, new HashMap<>());
        }
    }

    public void addMean(Integer iteration, Integer trial, double value) {
        this.mean.get(iteration).put(trial, value);
    }

    public void addWorst(Integer iteration, Integer trial, double value) {
        this.worst.get(iteration).put(trial, value);
    }

    public void addBest(Integer iteration, Integer trial, double value) {
        this.best.get(iteration).put(trial, value);
    }

    public void addBestSoFar(Integer iteration, Integer trial, double value) {
        this.bsf.get(iteration).put(trial, value);
    }

    public void addDiv(Integer iteration, Integer trial, double value) {
        this.div.get(iteration).put(trial, value);
    }

    public void addBranch(Integer iteration, Integer trial, double value) {
        this.branch.get(iteration).put(trial, value);
    }

    public void dispose() {
        try {
            String file = (new File("output")).getAbsolutePath() + "/" + fileName + ".csv";
            FileWriter fileWriter = new FileWriter(file, true);
            String header = "iteration,mean,best,worst,bsf,div,branch";
            for(int t = 0; t < trialSize; t++) {
                header += ",mean_" + t;
            }
            header += "\n";
            fileWriter.append(header);
            for(int i = 1; i < iterationSize - 1; i++) {
                String message = String.format(Locale.US, "%08d,%08d,%08d,%08d,%08d,%.2f,%.2f",
                        i, (int) mapMean(mean.get(i)), (int) mapMean(best.get(i)), (int) mapMean(worst.get(i)),
                        (int) mapMean(bsf.get(i)), mapMean(div.get(i)), mapMean(branch.get(i)));
                for(int t = 0; t < trialSize; t++) {
                    message += String.format(Locale.US, ",%08d", bsf.get(i).get(t).intValue());
                }
                message += "\n";
                fileWriter.append(message);
            }
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double mapMean(Map<Integer, Double> values) {
        double sum = values.get(0);
        for(int i = 1; i < values.size(); i++) {
            sum += values.get(i);
        }
        return sum / values.size();
    }

}
