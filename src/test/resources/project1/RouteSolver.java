package simulator.aco;

import simulator.graph.Graph;
import simulator.graph.Node;
import simulator.utils.DynamicListener;
import simulator.utils.GenericStatistics;
import simulator.utils.Utils;

import java.util.*;

public class RouteSolver implements DynamicListener {

    private Globals _globals;

    private Statistics statistics;

    private boolean execute = true;

    private Memory memory;

    public RouteSolver(Graph graph, Node sourceNode, List<Node> targetNodes, int trial, GenericStatistics genericStatistics, Algorithm algorithm) {
        _globals = new Globals(algorithm);
        _globals.graph = graph;
        _globals.sourceNode = sourceNode;
        _globals.targetNodes = targetNodes;
        Ant.fixed.clear();
        Ant.costMap.clear();
        Ant.getFixed(sourceNode, null);
        if (!targetNodes.contains(sourceNode)) {
            throw new RuntimeException("TargetNodes must contains SourceNode");
        }
        for (int i = 0; i < _globals.targetNodes.size(); i++) {
            for (int j = i + 1; j < _globals.targetNodes.size(); j++) {
                if (_globals.targetNodes.get(i).getId() != _globals.targetNodes.get(j).getId()) {
                    _globals.routeManager.addRoute(_globals.targetNodes.get(i).getId(), _globals.targetNodes.get(j).getId());
                    _globals.routeManager.addRoute(_globals.targetNodes.get(j).getId(), _globals.targetNodes.get(i).getId());
                }
            }
        }
        _globals.routeManager.updateRoutes(false, 0);
        statistics = new Statistics(_globals, this);
        statistics.setTrial(trial);
        statistics.setGenericStatistics(genericStatistics);
    }

    public void setup() {
        _globals.startParameters();
        allocateAnts();
        allocateStructures();
        initTry();
    }

    public void loop(int t) {
        if(_globals.isMMAS_MEM()) {
            if(_globals.envChanged) {
                _globals.rho = 0.02;
            } else {
                _globals.rho = 0.8;
            }
        }
        constructSolutions();
        updateStatistics();
        pheromoneTrailUpdate();
        searchControl();
        statistics.calculateStatistics();
        repairSolutions();
        computeNNList();
        _globals.iteration = t;
    }

    private void allocateAnts() {
        _globals.ants = new Ant[_globals.numberAnts];
        for (int i = 0; i < _globals.numberAnts; i++) {
            _globals.ants[i] = new Ant(_globals);
        }
        _globals.bestSoFar = new Ant(_globals);
        _globals.restartBestAnt = new Ant(_globals);
        if(_globals.isMIACO()) {
            _globals.previousBest = new Ant[2];
            for(int i = 0; i < 2; i++) {
                _globals.previousBest[i] = new Ant(_globals);
            }
            _globals.previousBestSoFarAnt = new Ant(_globals);
        }
    }

    private void allocateStructures() {
        for (Route route : _globals.routeManager.getRoutes()) {
            route.setPheromone(0.0);
            route.setTotal(0.0);
        }
        if (_globals.nnListSize > _globals.targetNodes.size()) {
            _globals.nnListSize = _globals.targetNodes.size();
        }
        _globals.nnList = new HashMap<>();
        if(_globals.isMMAS_MEM() || _globals.isMIACO() || _globals.isEIACO())
            memory = new Memory(_globals);
    }

    private void initTry() {
        computeNNList();
        _globals.iteration = 1;
        _globals.restartFoundBestIteration = 1;
        _globals.foundBestIteration = 1;
        _globals.ants[0].nnTour();
        if(_globals.isMMAS() || _globals.isMMAS_MEM()) {
            _globals.bestSoFar = new Ant(_globals);
            _globals.trailMax = 1.0 / (_globals.rho * _globals.ants[0].getCost());
            _globals.trailMin = _globals.trailMax / (2.0 * _globals.targetNodes.size());
            initPheromoneTrails(_globals.trailMax);
        }
        if(_globals.isMIACO()) {
            _globals.bestSoFar = new Ant(_globals);
            _globals.previousBestSoFarAnt = new Ant(_globals);
            _globals.trail0 = 1.0 / _globals.ants[0].getCost();
            initPheromoneTrails(_globals.trail0);
        }
        if(_globals.isEIACO()) {
            _globals.trail0 = 1.0 / (double) (_globals.targetNodes.size() - 1);
            _globals.previousBestSoFarAnt = _globals.ants[0].clone();
            initPheromoneTrails(_globals.trail0);
        }
        computeTotalInformation();
        if(_globals.isMMAS_MEM() || _globals.isMIACO() || _globals.isEIACO())
            memory.initMemoryRandomly();
    }

    private void initPheromoneTrails(double trail) {
        for (Route route : _globals.routeManager.getRoutes()) {
            route.setPheromone(trail);
            route.setTotal(trail);
        }
    }

    private void computeTotalInformation() {
        for (Route route : _globals.routeManager.getRoutes()) {
            double value = Math.pow(route.getPheromone(), _globals.alpha) * Math.pow(_globals.HEURISTIC(route), _globals.beta);
            route.setTotal(value);
        }
    }

    private void constructSolutions() {
        for (Ant ant : _globals.ants) {
            ant.heuristicTour();
        }
    }

    public void updateStatistics() {
        if(_globals.isMMAS_MEM() || _globals.isMMAS()) {
            Ant iterationBestAnt = findBestAnt();
            if (iterationBestAnt.getCost() < _globals.bestSoFar.getCost()) {
                //iterationBestAnt = LocalSearch.executeLocalSearch(iterationBestAnt, _globals);
                _globals.bestSoFar = iterationBestAnt.clone();
                _globals.restartBestAnt = iterationBestAnt.clone();
                _globals.foundBestIteration = _globals.iteration;
                _globals.restartFoundBestIteration = _globals.iteration;
                //Type 1
                double pX = Math.exp(Math.log(0.05) / _globals.targetNodes.size());
                _globals.trailMin = 1. * (1. - pX) / (pX * (double) ((_globals.nnListSize + 1) / 2));
                _globals.trailMax = 1. / ((_globals.rho) * _globals.bestSoFar.getCost());
                _globals.trail0 = _globals.trailMax;
                _globals.trailMin = _globals.trailMax * _globals.trailMin;
                //Type 2
                //_globals.trailMax = 1.0 / (_globals.rho * _globals.bestSoFar.getCost());
                //_globals.trailMin = _globals.trailMax / (2.0 * _globals.targetNodes.size());
                printBestSoFar();
            }
            if (iterationBestAnt.getCost() < _globals.restartBestAnt.getCost()) {
                //iterationBestAnt = LocalSearch.executeLocalSearch(iterationBestAnt, _globals);
                _globals.restartBestAnt = iterationBestAnt.clone();
                _globals.restartFoundBestIteration = _globals.iteration;
            }
        }
        if(_globals.isMIACO()) {
            Ant iterationBestAnt = findBestAnt();
            if (iterationBestAnt.getCost() < _globals.bestSoFar.getCost()) {
                _globals.bestSoFar = iterationBestAnt.clone();
                printBestSoFar();
            }
            _globals.previousBestSoFarAnt = _globals.bestSoFar.clone();
            _globals.previousBest[0] = _globals.previousBest[1].clone();
            _globals.previousBest[1] = _globals.previousBestSoFarAnt.clone();
            _globals.previousBestSoFarAnt = _globals.previousBest[0].clone();
            if(_globals.iteration == 1) _globals.previousBestSoFarAnt = _globals.bestSoFar.clone();
        }
        if(_globals.isEIACO()) {
            Ant iterationBestAnt = findBestAnt();
            if (iterationBestAnt.getCost() < _globals.bestSoFar.getCost()) {
                _globals.bestSoFar = iterationBestAnt.clone();
                printBestSoFar();
            }
            _globals.previousBestSoFarAnt = _globals.bestSoFar.clone();
        }
    }

    public void computeNNList() {
        _globals.nnList = new HashMap<>();
        for (int i = 0; i < _globals.targetNodes.size(); i++) {
            int sourceId = _globals.targetNodes.get(i).getId();
            Set<Route> routesLink = _globals.routeManager.getRoutes(sourceId);
            TreeMap<Double, Integer> positions = new TreeMap<>();
            for (Route route : routesLink) {
                positions.put(route.getBestCost(), route.getTargetNode().getId());
            }
            Integer[] keys = positions.values().toArray(new Integer[] {});
            Route[] routes = new Route[_globals.nnListSize];
            for (int r = 0; r < _globals.nnListSize; r++) {
                routes[r] = _globals.routeManager.getRoute(_globals.targetNodes.get(i).getId(), keys[r]);
            }
            _globals.nnList.put(_globals.targetNodes.get(i), routes);
        }
    }

    public void printBestSoFar() {
//        statistics.printStatistics();
    }

    public Ant findBestAnt() {
        Ant bestAnt = _globals.ants[0];
        for (int i = 1; i < _globals.numberAnts; i++) {
            if (_globals.ants[i].getCost() < bestAnt.getCost()) {
                bestAnt = _globals.ants[i];
            }
        }
        return bestAnt;
    }

    private void pheromoneTrailUpdate() {
        if(_globals.isMMAS() || _globals.isMMAS_MEM()) {
            pheromoneEvaporation();
            if (_globals.iteration % _globals.uGb == 0) {
                pheromoneUpdate(findBestAnt());
            } else if (_globals.uGb == 1 && (_globals.iteration - _globals.restartFoundBestIteration) > 50) {
                pheromoneUpdate(_globals.bestSoFar);
            } else {
                if (_globals.isMMAS_MEM() && _globals.envChanged) {
                    memory.updateShortTermMemory();
                    for (Ant k : memory.shortMemory) {
                        pheromoneUpdate(k);
                    }
                } else {
                    pheromoneUpdate(_globals.restartBestAnt);
                }
            }
            checkPheromoneTrails();
            computeTotalInformation();
        }
        if(_globals.isMIACO()) {
            memory.updateLongTermMemory();
            memory.updateShortTermMemory();
            memory.generatePheromoneMatrix();
            computeTotalInformation();
        }
        if(_globals.isEIACO()) {
            memory.updateShortTermMemory();
            pheromoneEIACOUpdate();
            computeTotalInformation();
        }
    }

    private void pheromoneEvaporation() {
        for (Route route : _globals.routeManager.getRoutes()) {
            route.setPheromone((1.0 - _globals.rho) * route.getPheromone());
        }
    }

    private void pheromoneEIACOUpdate() {
        double deltaT; //amount of pheromone
        deltaT = (1.0 - _globals.trail0) / (double) 2.0;
        initPheromoneTrails(_globals.trail0);
        for(int m = 0; m < Memory.shortMemorySize; m++){
            Ant ant = memory.shortMemory[m];
            for (int i = 0; i < ant.getTour().size() - 1; i++) {
                int fromId = ant.getTour().get(i).getId();
                int toId = ant.getTour().get(i + 1).getId();
                Route route = _globals.routeManager.getRoute(fromId, toId);
                route.setPheromone(route.getPheromone() + deltaT);
            }
        }
    }

    /**
     * Restrição, a atualização contrária não é verdade
     * @param ant
     */
    private void pheromoneUpdate(Ant ant) {
        double dTau = 1.0 / ant.getCost();
        for (int i = 0; i < ant.getTour().size() - 1; i++) {
            int fromId = ant.getTour().get(i).getId();
            int toId = ant.getTour().get(i + 1).getId();
            Route route = _globals.routeManager.getRoute(fromId, toId);
            route.setPheromone(route.getPheromone() + dTau);
        }
    }

    private void checkPheromoneTrails() {
        for (Route route : _globals.routeManager.getRoutes()) {
            if (route.getPheromone() < _globals.trailMin) {
                route.setPheromone(_globals.trailMin);
            }
            if (route.getPheromone() > _globals.trailMax) {
                route.setPheromone(_globals.trailMax);
            }
        }
    }

    private void searchControl() {
        if(_globals.isMMAS() || _globals.isMMAS_MEM()) {
            _globals.branchFactorValue = calculateBranchingFactor();
            if (_globals.iteration % 100 == 0) {
                //System.out.println("Branch factor = " + _globals.branchFactorValue + " at iteration " + _globals.iteration);
                if (_globals.branchFactorValue < _globals.branchFactor && (_globals.iteration - _globals.restartFoundBestIteration) > 250) {
                    //System.out.println(" ================== Restarting System! ================");
                    _globals.restartBestAnt = new Ant(_globals);
                    _globals.restartBestAnt.randomWalk();
                    initPheromoneTrails(_globals.trailMax);
                    computeTotalInformation();
                    _globals.restartFoundBestIteration = _globals.iteration;
                }
            }
        }
    }

    public double calculateBranchingFactor() {
        double min, max, cutoff, avg = 0.0;
        List<Double> numBranches = new ArrayList<>();
        for (Node node : _globals.targetNodes) {
            if (Ant.fixed.contains(node)) continue;
            Route[] routes = _globals.nnList.get(node);
            if (routes.length > 0) {
                max = Double.MAX_VALUE * -1.0;
                min = Double.MAX_VALUE;
                for (Route route : routes) {
                    if (route.getPheromone() > max) {
                        max = route.getPheromone();
                    }
                    if (route.getPheromone() < min) {
                        min = route.getPheromone();
                    }
                }
                cutoff = min + _globals.lambda * (max - min);
                double count = 0.0;
                for (Route route : routes) {
                    if (route.getPheromone() >= cutoff) {
                        count += 1.0;
                    }
                }
                numBranches.add(count);
            }
        }
        for (Double branch : numBranches) {
            avg += branch;
        }
        if(_globals.targetNodes.size() - Ant.fixed.size() == 0) return 0.0;
        return (avg / ((_globals.targetNodes.size() - Ant.fixed.size()) * 2.0));
    }

    public void repairSolutions() {
        double cost = _globals.bestSoFar.getCost();
        _globals.bestSoFar.computeCost();
        if (cost != _globals.bestSoFar.getCost()) {
            printBestSoFar();
            _globals.envChanged = true;
        }
        _globals.restartBestAnt.computeCost();
    }

    public Stack<Node> getResultTour() {
        Stack<Node> tour = new Stack<>();
        for (Node node : _globals.bestSoFar.getTour()) {
            tour.add(node);
        }
        return tour;
    }

    public double getCost() {
        return _globals.bestSoFar.getCost();
    }

    public Route getRoute(Node source, Node target) {
        return _globals.routeManager.getRoute(source.getId(), target.getId());
    }

    public void finish() {
        execute = false;
    }

    public void addVisited(Node node) {
        if(!Ant.getFixed(null, null).isEmpty()) {
            Node last = Ant.getFixed(null, null).lastElement();
            String key = last.getId() + "->" + node.getId();
            Ant.costMap.put(key, _globals.routeManager.getRoute(last.getId(), node.getId()).getBestCost());
        }
        Ant.getFixed(node, null);
    }

    @Override
    public void updatedWeights(boolean cycled, int phase) {
        //System.out.println("Dynamic environment changed, starting updating routes....");
        this._globals.routeManager.updateRoutes(cycled, phase);
        //System.out.println("Dynamic environment changed, ending updating routes....");
    }

    public Integer getIteration() {
        return _globals.iteration;
    }

    public Set<Route> getRoutes() {
        return _globals.routeManager.getRoutes();
    }

    public void adaptPheromone() {
        if(_globals.isMMAS_MEM() && _globals.iteration % 10 == 0)
            _globals.rho = 0.02 + (1.0 - statistics.getDiversity()) * (0.8 - 0.02);
    }

    public Long getFECount() {
        return _globals.fitnessEvalCount;
    }
}
