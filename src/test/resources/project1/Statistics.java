package simulator.aco;


import simulator.graph.Node;
import simulator.utils.GenericStatistics;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;

public class Statistics {

    private Globals _globals;

    private RouteSolver routeSolver;

    private int trial;

    private GenericStatistics genericStatistics;

    public Statistics(Globals globals, RouteSolver routeSolver) {
        this._globals = globals;
        this.routeSolver = routeSolver;
    }

    public void printStatistics() {
        double[] costs = new double[_globals.numberAnts];
        for (int a = 0; a < _globals.numberAnts; a++) {
            costs[a] = _globals.ants[a].getCost();
        }
        int iter = _globals.iteration;
        int mean = (int) mean(costs);
        int best = (int) getBest(costs);
        int worst = (int) getWorst(costs);
        int bsf = (int) _globals.bestSoFar.getCost();
        double div = getDiversity();
        String message = String.format(Locale.US, "%08d,%08d,%08d,%08d,%08d,%.2f,%.2f",
                iter, mean, best, worst, bsf, div, routeSolver.calculateBranchingFactor());
        System.out.println(message);
    }

    public void calculateStatistics() {
        double[] costs = new double[_globals.numberAnts];
        for (int a = 0; a < _globals.numberAnts; a++) {
            costs[a] = _globals.ants[a].getCost();
        }
        double mean = 0;
        double best = 0;
        double worst = 0;
        if(costs.length != 0) {
            best = Integer.MAX_VALUE;
            worst = Integer.MIN_VALUE;
            double sum = costs[0];
            for(int i = 1; i < costs.length; i++) {
                sum += costs[i];
                best = Math.min(best, costs[i]);
                worst = Math.max(worst, costs[i]);
            }
            mean = sum / costs.length;
        }
        int iter = _globals.iteration;
        int bsf = (int) _globals.bestSoFar.getCost();
        double div = getDiversity();
        genericStatistics.addMean(iter, trial, mean);
        genericStatistics.addBest(iter, trial, best);
        genericStatistics.addWorst(iter, trial, worst);
        genericStatistics.addBestSoFar(iter, trial, bsf);
        genericStatistics.addDiv(iter, trial, div);
        genericStatistics.addBranch(iter, trial, _globals.branchFactorValue);
        if(_globals.isMIACO()) {
            _globals.previousBestSoFarAnt = _globals.bestSoFar.clone();
        }
    }

    public double mean(double[] values) {
        if(values.length == 0) return 0;
        double sum = values[0];
        for(int i = 1; i < values.length; i++) {
            sum += values[i];
        }
        return sum / values.length;
    }

    public double getDiversity() {
        double div = 0.0;
        for (int i = 0; i < _globals.numberAnts; i++) {
            for (int j = 0; j < _globals.numberAnts; j++) {
                if (i != j) {
                    div += distanceBetweenAnts(_globals.ants[i], _globals.ants[j]);
                }
            }
        }
        return (1.0 / (_globals.numberAnts * (_globals.numberAnts - 1.0))) * div;
    }

    public double distanceBetweenAnts(Ant a1, Ant a2) {
        int pos, n = _globals.targetNodes.size();
        double distance = 0.0;
        Map<Integer, Integer> edges = new HashMap<>();
        for(int i = 0; i < a2.getTour().size() - 1; i++) {
            edges.put(a2.getTour().get(i).getId(), a1.getTour().get(i + 1).getId());
        }
        for(int i = 0; i < a1.getTour().size() - 1; i++) {
            int j = a1.getTour().get(i).getId();
            int h = a1.getTour().get(i + 1).getId();
            pos = edges.get(j);
            if(h == pos) {
                distance++;
            }
        }
        return 1.0 - (distance / (double) n);
    }

    public double getBest(double[] values) {
        if(values.length == 0) return 0;
        double min = values[0];
        for(int i = 0; i < values.length; i++) {
            if(values[i] < min) {
                min = values[i];
            }
        }
        return min;
    }

    public double getWorst(double[] values) {
        if(values.length == 0) return 0;
        double max = values[0];
        for(int i = 0; i < values.length; i++) {
            if(values[i] > max) {
                max = values[i];
            }
        }
        return max;
    }

    public void setTrial(int trial) {
        this.trial = trial;
    }

    public void setGenericStatistics(GenericStatistics genericStatistics) {
        this.genericStatistics = genericStatistics;
    }
}
