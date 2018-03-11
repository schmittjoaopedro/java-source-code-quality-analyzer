package simulator.graph;

import java.util.HashSet;
import java.util.Set;

public class Node implements Comparable<Node> {

    private int id;

    // latitude
    private double x;

    // longitude
    private double y;

    // edges
    private Set<Edge> edges;

    // dijkstra
    private Double minCost = Double.POSITIVE_INFINITY;

    // dijkstra
    private Node previous;

    public Node() {
        super();
        this.edges = new HashSet<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Set<Edge> getEdges() {
        return this.edges;
    }

    public Double getMinCost() {
        return minCost;
    }

    public void setMinCost(Double minCost) {
        this.minCost = minCost;
    }

    public Node getPrevious() {
        return previous;
    }

    public void setPrevious(Node previous) {
        this.previous = previous;
    }

    @Override
    public String toString() {
        return this.getId() + " = " + this.getY() + "," + this.getX();
    }

    @Override
    public int compareTo(Node node) {
        return Double.compare(minCost, node.getMinCost());
    }

}
