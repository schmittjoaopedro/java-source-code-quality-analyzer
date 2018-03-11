package simulator.utils;

import simulator.graph.Edge;
import simulator.graph.Graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DynamicEdgeGenerator {

    private double magnitude;

    private long frequency;

    private double upperBound;

    private double lowerBound;

    private Graph graph;

    private Random random = new Random();

    private DynamicListener dynamicListener;

    long nextTime;

    private boolean cycle = false;

    private int periodLimit;

    private int period;

    private Map<Integer, Map<Edge, Double>> cycles;

    public DynamicEdgeGenerator(Graph graph, double magnitude, long frequency, double lowerBound, double upperBound) {
        this.graph = graph;
        this.magnitude = magnitude;
        this.frequency = frequency;
        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
    }

    public void loop(int t) {
        if(t >= nextTime) {
            if(cycle) {
                for(Map.Entry<Edge, Double> edgeCost : cycles.get(period).entrySet()) {
                    edgeCost.getKey().setCost(edgeCost.getValue());
                }
                if(this.getDynamicListener() != null) {
                    this.getDynamicListener().updatedWeights(cycle, period);
                }
                period++;
                if(period >= periodLimit) {
                    period = 0;
                }
            } else {
                for (Edge edge : graph.getEdges()) {
                    if (random.nextDouble() < magnitude) {
                        double prop = lowerBound + (random.nextDouble() * (upperBound - lowerBound));
                        edge.setCost(edge.getOriginalCost() + (edge.getOriginalCost() * prop));
                    } else {
                        edge.setCost(edge.getOriginalCost());
                    }
                }
                if(this.getDynamicListener() != null) {
                    this.getDynamicListener().updatedWeights(cycle, period);
                }
            }
            nextTime = t + frequency;
        }
    }

    public DynamicListener getDynamicListener() {
        return dynamicListener;
    }

    public void setDynamicListener(DynamicListener dynamicListener) {
        this.dynamicListener = dynamicListener;
    }

    public void setCycle(boolean cycle, int periodLimit) {
        this.cycle = cycle;
        this.cycles = new HashMap<>();
        this.period = 0;
        this.periodLimit = periodLimit;
        for(int i = 0; i < periodLimit; i++) {
            cycles.put(i, new HashMap<>());
            for(Edge edge : graph.getEdges()) {
                if(random.nextDouble() < magnitude) {
                    double prop = lowerBound + (random.nextDouble() * (upperBound - lowerBound));
                    cycles.get(i).put(edge, edge.getOriginalCost() + (edge.getOriginalCost() * prop));
                } else {
                    cycles.get(i).put(edge, edge.getOriginalCost());
                }
            }
        }
        for(int i = 0; i < periodLimit; i++) {
            for(Map.Entry<Edge, Double> edgeCost : cycles.get(i).entrySet()) {
                edgeCost.getKey().setCost(edgeCost.getValue());
            }
            if(this.getDynamicListener() != null) {
                this.getDynamicListener().updatedWeights(cycle, i);
            }
        }
    }
}
