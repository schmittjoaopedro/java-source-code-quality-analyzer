package simulator.aco;

import simulator.graph.Graph;
import simulator.graph.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Globals {

    public Graph graph;

    public Node sourceNode;

    public RouteManager routeManager;

    public List<Node> targetNodes;

    public Map<Node, Route[]> nnList;

    public int nnListSize;

    public Ant[] ants;

    public Ant bestSoFar;

    public Ant restartBestAnt;

    public Ant[] previousBest;

    public Ant previousBestSoFarAnt;

    public int numberAnts;

    public int iteration;

    public int restartFoundBestIteration;

    public int foundBestIteration;

    public double branchFactor;

    public double lambda;

    public double trailMax;

    public double trailMin;

    public double trail0;

    public double alpha;

    public double beta;

    public double rho;

    public int uGb;

    public double branchFactorValue;

    public double q0;

    public boolean envChanged = false;

    public Algorithm algorithm;

    public Long fitnessEvalCount = 0L;

    public Globals(Algorithm algorithm) {
        this.algorithm = algorithm;
        routeManager = new RouteManager(this);
        targetNodes = new ArrayList<>();
    }

    public void startParameters() {
        numberAnts = 50;
        alpha = 1.0;
        beta = 5.0;
        rho = 0.02;
        branchFactor = 1.0001;
        lambda = 0.05;
        iteration = 0;
        restartFoundBestIteration = 0;
        foundBestIteration = 0;
        uGb = 25;
        nnListSize = 20;
        if(isMMAS()) {
            rho = 0.8;
        }
        if(isMMAS_MEM()) {
            Memory.shortMemorySize = 4;
            Memory.longMemorySize = 4;
            Memory.immigrantRate = 0.4;
            Memory.pMi = 0.01;
            envChanged = false;
        }
        if(isMIACO()) {
            Memory.shortMemorySize = 10;
            Memory.longMemorySize = 4;
            Memory.immigrantRate = 0.4;
            Memory.pMi = 0.01;
        }
        if(isEIACO()) {
            rho = 0.8;
            Memory.shortMemorySize = 3;
            Memory.longMemorySize = 3;
            Memory.immigrantRate = 0.5;
            Memory.pMi = 0.01;
        }
    }

    public boolean isEIACO() {
        return Algorithm.EIACO == this.algorithm;
    }

    public boolean isMMAS() {
        return Algorithm.MMAS == this.algorithm;
    }

    public boolean isMMAS_MEM() {
        return Algorithm.MMAS_MEM == this.algorithm;
    }

    public boolean isMIACO() {
        return Algorithm.MIACO == this.algorithm;
    }

    public double HEURISTIC(Route route) {
        return 1.0 / (route.getBestCost() + 0.00001);
    }

}
