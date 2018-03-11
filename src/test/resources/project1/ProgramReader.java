package simulator.utils;

import simulator.aco.Algorithm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ProgramReader {

    //<PROBLEM TYPE>;<USE_SIMULATOR>;<ALGORITHM>;<MAGNITUDE>;<FREQUENCY>;<CYCLE>;<PERIOD>;<N_VERTICES>;<SEED>
    public static List<ProgramInstance> getProgram() {
        try {
            List<ProgramInstance> instances = new ArrayList<>();
            File file = new File("program.txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if(line != null && !line.trim().isEmpty() && !line.startsWith("#")) {
                    ProgramInstance instance = new ProgramInstance();
                    String[] values = line.split(";");
                    instance.problemType = values[0];
                    instance.isSimulated = Boolean.valueOf(values[1]);
                    instance.algorithm = Algorithm.valueOf(values[2]);
                    instance.magnitude = Double.valueOf(values[3]);
                    instance.frequency = Integer.valueOf(values[4]);
                    instance.cycle = Boolean.valueOf(values[5]);
                    instance.period = Integer.valueOf(values[6]);
                    instance.nVertices = Integer.valueOf(values[7]);
                    instance.seed = Integer.valueOf(values[8]);
                    instance.fileName = "" +
                        instance.problemType + "_" + instance.isSimulated + "_" + instance.algorithm + "_" +
                        instance.magnitude + "_" + instance.frequency + "_" + instance.cycle + "_" +
                        instance.period + "_" + instance.nVertices;
                    instances.add(instance);
                }
            }
            fileReader.close();
            return instances;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
