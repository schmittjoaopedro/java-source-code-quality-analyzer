package simulator;

import org.apache.log4j.Logger;
import simulator.graph.Graph;
import simulator.graph.Node;
import simulator.aco.Route;
import simulator.aco.RouteSolver;
import simulator.visualizer.Visualizer;

import java.util.*;
import java.util.stream.Collectors;

public class Simulator {

    private Graph graph;

    private Node currentNode;

    private Node lastNode;

    private Set<Node> notVisitedPoints;

    private Set<Node> visitedPoints;

    private Visualizer visualizer;

    private RouteSolver routeSolver;

    private Map<String, List<Node>> routesVisited;

    long timePass = 10000;

    int stepSize;

    boolean printGraph;

    private double lastCost = 0;

    public Simulator(Graph graph, Node sourceNode, List<Node> targetNodes, RouteSolver routeSolver, int stepSize, boolean print) {
        this.stepSize = stepSize;
        this.notVisitedPoints = new HashSet<>();
        this.notVisitedPoints.addAll(targetNodes);
        this.notVisitedPoints.remove(sourceNode);
        this.visitedPoints = new HashSet<>();
        currentNode = sourceNode;
        this.graph = graph;
        this.routeSolver = routeSolver;
        Set<Integer> ids = targetNodes.stream().map(Node::getId).collect(Collectors.toSet());
        this.routesVisited = new HashMap<>();
        this.printGraph = print;
        if(print) {
            this.visualizer = new Visualizer(graph, ids);
        }
    }

    public void setup() {
        visitedPoints.add(currentNode);
        notVisitedPoints.remove(currentNode);
        timePass = stepSize;
        if(printGraph) {
            visualizer.setStat("Starting in 4s");
            try { Thread.sleep(4000); } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public void loop(int t) {
        if(!notVisitedPoints.isEmpty()) {
            if(t >= timePass) {
                for(Node node : routeSolver.getResultTour()) {
                    if(!visitedPoints.contains(node)) {
                        routeSolver.addVisited(node);
                        notVisitedPoints.remove(node);
                        visitedPoints.add(node);
                        lastNode = currentNode;
                        currentNode = node;
                        break;
                    }
                }
                timePass = t + stepSize;
                //System.out.println(" ---> Going to: " + currentNode.getId());
                if(printGraph) {
                drawFull();
//                    drawSimple();
                }
            }
        } else {
            routeSolver.finish();
        }
        if(printGraph && lastCost != routeSolver.getCost()) {
            lastCost = routeSolver.getCost();
            drawFull();
//            drawSimple();
        }
    }

    private void drawSimple() {
        visualizer.setStat("Cost = " + routeSolver.getCost() + " iteration " + routeSolver.getIteration());
        visualizer.draw(
            routeSolver.getResultTour().stream().map(Node::getId).collect(Collectors.toList()).toArray(new Integer[] {}),
            visitedPoints.stream().map(Node::getId).collect(Collectors.toSet()), new HashSet<>());
    }

    public void finish() {
        if(printGraph)
            this.visualizer.dispose();
    }

    private void drawFull() {
        List<Integer> routeTour = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        Stack<Node> tour = routeSolver.getResultTour();
        visualizer.setStat("Cost = " + routeSolver.getCost() + " iteration " + routeSolver.getIteration());
        Set<Integer> traversed = new HashSet<>();
        for(int i = 1; i < tour.size(); i++) {
            Route route = routeSolver.getRoute(tour.get(i - 1), tour.get(i));
            if(visitedPoints.contains(tour.get(i)) && visitedPoints.contains(tour.get(i - 1))) {
                String key = tour.get(i - 1).getId() + "->" + tour.get(i).getId();
                if(routesVisited.containsKey(key)) {
                    for (Node path : routesVisited.get(key)) {
                        visited.add(path.getId());
                        routeTour.add(path.getId());
                        traversed.add(path.getId());
                    }
                } else {
                    if(tour.get(i) != currentNode) {
                        routesVisited.put(key, new ArrayList<>());
                    }
                    for (Node path : route.getBestRoute()) {
                        routeTour.add(path.getId());
                        visited.add(path.getId());
                        if(tour.get(i) != currentNode) {
                            routesVisited.get(key).add(path);
                        }
                    }
                }
            } else {
                for (Node path : route.getBestRoute()) {
                    routeTour.add(path.getId());
                }
            }
        }
        visualizer.draw(routeTour.toArray(new Integer[] {}), visited, traversed);
    }

}
