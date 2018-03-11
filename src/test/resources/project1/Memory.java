package simulator.aco;

import simulator.graph.Node;
import simulator.utils.Utils;

import java.util.*;

public class Memory {

    public static int shortMemorySize;

    public static int longMemorySize;

    public static double immigrantRate;

    public static double pMi;

    public int tM;

    private boolean[] randomPoint;

    public Ant[] shortMemory;

    public Ant[] longMemory;

    private Globals _globals;

    private Random random = new Random();

    public Memory(Globals globals) {
        super();
        _globals = globals;
        shortMemory = new Ant[shortMemorySize];
        longMemory = new Ant[longMemorySize];
        randomPoint = new boolean[longMemorySize];
    }

    public void initMemoryRandomly() {
        for (int i = 0; i < longMemorySize; i++) {
            longMemory[i] = new Ant(_globals);
            longMemory[i].randomWalk();
            randomPoint[i] = true;
        }
        if(_globals.isMMAS_MEM()) {
            for (int i = 0; i < shortMemorySize; i++) {
                shortMemory[i] = new Ant(_globals);
                shortMemory[i].randomWalk();
            }
        }
        tM = 5 + ((int) (random.nextDouble() * 6.0));
    }

    public void updateLongTermMemory() {
        if(_globals.isMIACO()) {
            boolean flag = detectChange();
            if (flag == true) {
                updateMemoryEveryChange();
            }
            if (_globals.iteration == tM && flag == false) {
                updateMemoryDynamically();
                int rnd = 5 + ((int) (random.nextDouble() * 6.0));
                tM = _globals.iteration + rnd;
            }
            if (_globals.iteration == tM && flag == true) {
                int rnd = 5 + ((int) (random.nextDouble() * 6.0));
                tM = _globals.iteration + rnd;
            }
        }
        if(_globals.isMMAS_MEM()) {
            repairLongMemory();
            for(int i = 0; i < longMemorySize; i++) {
                longMemory[i].computeCost();
            }
            updateMemoryDynamically();
        }
    }

    public void updateMemoryDynamically() {
        int index = -1;
        for (int i = 0; i < longMemorySize; i++) {
            if(_globals.bestSoFar.getCost() == longMemory[i].getCost()) return;
            if (randomPoint[i] == true) {
                index = i;
                randomPoint[i] = false;
                break;
            }
        }
        if (index != -1) {
            longMemory[index] = _globals.bestSoFar.clone();
        } else {
            int closestInd = -1;
            double closest = Integer.MAX_VALUE;
            for (int i = 0; i < longMemorySize; i++) {
                double d = distanceBetweenAnts(_globals.bestSoFar, longMemory[i]);
                if (closest > d) {
                    closest = d;
                    closestInd = i;
                }
            }
            if (_globals.bestSoFar.getCost() < longMemory[closestInd].getCost()) {
                longMemory[closestInd] = _globals.bestSoFar.clone();
            }
        }
    }

    public void updateMemoryEveryChange() {
        int index = -1;
        Ant ant = null;
        if(_globals.isMMAS_MEM()) ant = _globals.restartBestAnt;
        if(_globals.isMIACO()) ant = _globals.previousBestSoFarAnt;
        for (int i = 0; i < longMemorySize; i++) {
            if(ant.getCost() == longMemory[i].getCost()) return;
            if (randomPoint[i] == true) {
                index = i;
                randomPoint[i] = false;
                break;
            }
        }
        ant.computeCost();
        if (index != -1) {
            longMemory[index] = ant.clone();
        } else {
            double closest = Integer.MAX_VALUE;
            int closestInd = -1;
            for (int i = 0; i < longMemorySize; i++) {
                double d = distanceBetweenAnts(ant, longMemory[i]);
                if (closest > d) {
                    closest = d;
                    closestInd = i;
                }
            }
            if (ant.getCost() < longMemory[closestInd].getCost()) {
                longMemory[closestInd] = ant;
            }
        }
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

    public boolean detectChange() {
        double totalBefore = 0.0;
        double totalAfter = 0.0;
        repairLongMemory();
        for (int i = 0; i < longMemorySize; i++) {
            totalBefore += longMemory[i].getCost();
        }
        for (int i = 0; i < longMemorySize; i++) {
            longMemory[i].computeCost();
            totalAfter += longMemory[i].getCost();
        }
        if (totalBefore == totalAfter)
            return false;
        else
            return true;
    }

    public void repairLongMemory() {
        for (int i = 0; i < longMemorySize; i++) {
            boolean valid = isValidAnt(longMemory[i]);
            if(!valid) {
                longMemory[i].randomWalk();
                randomPoint[i] = true;
            }
        }
    }

    public boolean isValidAnt(Ant k) {
        for(int j = 0; j < Ant.getFixed(null, null).size(); j++) {
            if(k.getTour().get(j) != Ant.getFixed(null, null).get(j)) {
                return false;
            }
        }
        return true;
    }


    public void updateShortTermMemory() {

        if(_globals.isMMAS_MEM()) {
            int imSize = (int) (immigrantRate * shortMemorySize);
            Ant[] immigrants = new Ant[imSize];
            for (int i = 0; i < imSize; i++) {
                immigrants[i] = generateMemoryBasedImmigrant();
            }
            Set<Ant> antsPopulation = new HashSet<>();
            Set<Double> antsCosts = new HashSet<>();
            antsPopulation.add(_globals.restartBestAnt);
            antsCosts.add(_globals.restartBestAnt.getCost());
            Utils.sortAntArray(_globals.ants);
            for (Ant ant : _globals.ants) {
                if(!antsCosts.contains(ant.getCost())) {
                    antsPopulation.add(ant);
                    antsCosts.add(ant.getCost());
                }
            }
            if(antsPopulation.size() < shortMemorySize) {
                for (Ant ant : _globals.ants) {
                    if(!antsPopulation.contains(ant) && antsPopulation.size() < shortMemorySize) {
                        antsPopulation.add(ant);
                    }
                }
            }
            Ant[] ants = antsPopulation.toArray(new Ant[0]);
            Utils.sortAntArray(ants);
            for(int i = 0; i < shortMemorySize; ++i) {
                this.shortMemory[i].computeCost();
            }
            Utils.sortAntArray(this.shortMemory);
            for(int i = 0; i < shortMemorySize; ++i) {
                if(!isValidAnt(this.shortMemory[i]) || ants[i].getCost() < this.shortMemory[i].getCost()) {
                    this.shortMemory[i] = ants[i].clone();
                }
            }
            for (int i = shortMemorySize - 1; i > shortMemorySize - imSize - 1; i--) {
                shortMemory[i] = immigrants[shortMemorySize - 1 - i];
            }
            Utils.sortAntArray(shortMemory);
        }

        if(_globals.isMIACO()) {
            int imSize = (int) (immigrantRate * shortMemorySize);
            Ant[] immigrants = new Ant[imSize];
            for (int i = 0; i < imSize; i++) {
                immigrants[i] = generateMemoryBasedImmigrant();
            }
            Utils.sortAntArray(_globals.ants);
            for (int i = 0; i < shortMemorySize; i++) {
                shortMemory[i] = _globals.ants[i].clone();
            }
            for (int i = shortMemorySize - 1; i > shortMemorySize - imSize - 1; i--) {
                shortMemory[i] = immigrants[shortMemorySize - 1 - i];
            }
        }

        if(_globals.isEIACO()) {
            int imSize = (int) Math.ceil(immigrantRate * shortMemorySize);
            Utils.sortAntArray(_globals.ants);
            for(int i = 0; i < shortMemorySize; ++i) {
                this.shortMemory[i] = _globals.ants[i].clone();
            }
            _globals.previousBestSoFarAnt.computeCost();
            Ant[] immigrants = new Ant[imSize];
            for (int i = 0; i < imSize; i++) {
                immigrants[i] = generateMemoryBasedImmigrant();
            }
            for (int i = shortMemorySize - 1; i > shortMemorySize - imSize - 1; i--) {
                shortMemory[i] = immigrants[shortMemorySize - 1 - i];
            }
        }
    }

    public Ant generateMemoryBasedImmigrant() {
        Ant selectedAnt = findAnt().clone();
        for(int i = 1; i < selectedAnt.getTour().size() - 1; i++) {
            if(random.nextDouble() < pMi) {
                int index = (int) ((random.nextDouble() * (selectedAnt.getTour().size() - 2)) + 1);
                Node temp = selectedAnt.getTour().get(i);
                selectedAnt.getTour().set(i, selectedAnt.getTour().get(index));
                selectedAnt.getTour().set(index, temp);
            }
        }
        selectedAnt.computeCost();
        return selectedAnt;
    }

    public Ant findAnt() {
        if(_globals.isMIACO()) {
            int index = 0;
            double cost = longMemory[index].getCost();
            for (int i = 1; i < longMemorySize; i++) {
                if (longMemory[i].getCost() < cost) {
                    cost = longMemory[i].getCost();
                    index = i;
                }
            }
            return longMemory[index];
        }
        if(_globals.isMMAS_MEM()) {
            int index = 0;
            double cost = _globals.ants[index].getCost();
            for (int i = 1; i < _globals.numberAnts; i++) {
                if (_globals.ants[i].getCost() < cost) {
                    cost = _globals.ants[i].getCost();
                    index = i;
                }
            }
            return _globals.ants[index];
        }
        if(_globals.isEIACO()) {
            return _globals.previousBestSoFarAnt;
        }
        return null;
    }

    public void generatePheromoneMatrix() {
        double deltaT = (1.0 - _globals.trail0) / (double) shortMemorySize;
        for(Route route : _globals.routeManager.getRoutes()) {
            route.setPheromone(_globals.trail0);
        }
        for (int i = 0; i < shortMemorySize; i++) {
            constantPheromoneDeposit(shortMemory[i], deltaT);
        }
    }

    private void constantPheromoneDeposit(Ant ant, double deltaT) {
        for (int i = 0; i < ant.getTour().size() - 1; i++) {
            int fromId = ant.getTour().get(i).getId();
            int toId = ant.getTour().get(i + 1).getId();
            Route route = _globals.routeManager.getRoute(fromId, toId);
            route.setPheromone(route.getPheromone() + deltaT);
        }
    }

    int rank(Stack<Node> tour, Node city){
        int i = 0;
        while(tour.get(i) != city)
            i++;
        if(i >= _globals.targetNodes.size())
            return -1;
        else
            return i;
    }

    Ant inverOver(Ant ind){
        double p = 1.0;
        double gain = 0.0;
        int step = 0;
        Ant selected = new Ant(_globals);
        int tempInd;
        Node city1, city2;
        int indexCity1Ind, indexCity2Ind, indexCity1TempInd, indexCity2TempInd;

        Ant tempIndividual = new Ant(_globals);

        Ant random = new Ant(_globals);
        random.randomWalk();

        tempIndividual = ind.clone();
        indexCity1Ind = (int) (Math.random() * (_globals.targetNodes.size() - 1));

        city1 = tempIndividual.getTour().get(indexCity1Ind);
        while (true){
            indexCity1Ind = rank(tempIndividual.getTour(), city1);
            if (Math.random() <= p){
                indexCity2Ind = (int) (Math.random() * (_globals.targetNodes.size() - 1));
                city2 = tempIndividual.getTour().get(indexCity2Ind);
                while(city2 == city1){
                    indexCity2Ind = (int) (Math.random() * (_globals.targetNodes.size() - 1));
                    city2 = tempIndividual.getTour().get(indexCity2Ind);
                }
            } else {
                tempInd = (int) (Math.random() * _globals.numberAnts);
                selected = _globals.ants[tempInd].clone();
                while (selected == ind){
                    tempInd = (int) (Math.random() * _globals.numberAnts);
                    selected = _globals.ants[tempInd].clone();
                }
                indexCity1TempInd = rank(selected.getTour(), city1);
                indexCity2TempInd = (indexCity1TempInd + 1) % _globals.targetNodes.size();
                city2 = selected.getTour().get(indexCity2TempInd);
                indexCity2Ind = rank(tempIndividual.getTour(), city2);
            }
            if (tempIndividual.getTour().get((indexCity1Ind + 1) % _globals.targetNodes.size()) == city2)
                break;
            gain += -_globals.routeManager.getRoute(city1.getId(), tempIndividual.getTour().get((indexCity1Ind + 1) % _globals.targetNodes.size()).getId()).getBestCost()
                    -_globals.routeManager.getRoute(city2.getId(), tempIndividual.getTour().get((indexCity2Ind + 1) % _globals.targetNodes.size()).getId()).getBestCost()
                    +_globals.routeManager.getRoute(city1.getId(), city2.getId()).getBestCost()
                    +_globals.routeManager.getRoute(tempIndividual.getTour().get((indexCity1Ind + 1) % _globals.targetNodes.size()).getId(), tempIndividual.getTour().get((indexCity2Ind + 1) % _globals.targetNodes.size()).getId()).getBestCost();
            step++;
            //Inverse cities between indexCity1_Ind and indexCity2_Ind
            int i, j;
            Node temp;
            if (indexCity1Ind < indexCity2Ind) {
                for (i = indexCity1Ind + 1, j = indexCity2Ind; i < j; i++, j--) {
                    temp = tempIndividual.getTour().get(i);
                    tempIndividual.getTour().set(i, tempIndividual.getTour().get(j));
                    tempIndividual.getTour().set(j, temp);
                }
            } else {
                if (_globals.targetNodes.size() - 1 - indexCity1Ind <= indexCity2Ind + 1) {
                    for (i = indexCity1Ind + 1, j = indexCity2Ind; i < _globals.targetNodes.size(); i++, j--) {
                        temp = tempIndividual.getTour().get(i);
                        tempIndividual.getTour().set(i, tempIndividual.getTour().get(j));
                        tempIndividual.getTour().set(j, temp);
                    }
                    for (i = 0; i < j; i++, j--) {
                        temp = tempIndividual.getTour().get(i);
                        tempIndividual.getTour().set(i, tempIndividual.getTour().get(j));
                        tempIndividual.getTour().set(j, temp);
                    }
                } else {
                    for (i = indexCity1Ind + 1, j = indexCity2Ind; j >= 0; i++, j--) {
                        temp = tempIndividual.getTour().get(i);
                        tempIndividual.getTour().set(i, tempIndividual.getTour().get(j));
                        tempIndividual.getTour().set(j, temp);
                    }
                    for (j = _globals.targetNodes.size() - 1; i < j; i++, j--) {
                        temp = tempIndividual.getTour().get(i);
                        tempIndividual.getTour().set(i, tempIndividual.getTour().get(j));
                        tempIndividual.getTour().set(j, temp);
                    }
                }
            }
            city1 = city2;
            //printTour(temp_individual->tour);
        }
        tempIndividual.getTour().set(_globals.targetNodes.size(), tempIndividual.getTour().get(0));
        tempIndividual.computeCost();
        return tempIndividual;
    }
}
