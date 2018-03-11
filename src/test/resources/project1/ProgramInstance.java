package simulator.utils;

import simulator.aco.Algorithm;

public class ProgramInstance {

    //<PROBLEM TYPE>;<USE_SIMULATOR>;<ALGORITHM>;<MAGNITUDE>;<FREQUENCY>;<CYCLE>;<PERIOD>;<N_VERTICES>;<SEED>

    public String fileName;

    public String problemType;

    public Boolean isSimulated;

    public Algorithm algorithm;

    public Double magnitude;

    public Integer frequency;

    public Boolean cycle;

    public Integer period;

    public Integer nVertices;

    public Integer seed;

}
