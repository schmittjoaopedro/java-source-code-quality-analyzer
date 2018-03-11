package simulator.utils;


import simulator.aco.Ant;
import simulator.graph.Node;

import java.util.*;

public class Utils {

    public static LinkedHashMap<Integer, Double> sortHashMapByValues(Map<Integer, Double> passedMap) {
        List<Integer> mapKeys = new ArrayList<>(passedMap.keySet());
        List<Double> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);
        LinkedHashMap<Integer, Double> sortedMap = new LinkedHashMap<>();
        Iterator<Double> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Double val = valueIt.next();
            Iterator<Integer> keyIt = mapKeys.iterator();
            while (keyIt.hasNext()) {
                Integer key = keyIt.next();
                Double comp1 = passedMap.get(key);
                Double comp2 = val;
                if (comp1.equals(comp2)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }

    public static void sortAntArray(Ant[] ants) {
        Arrays.sort(ants, new Comparator<Ant>() {
            @Override
            public int compare(Ant ant1, Ant ant2) {
                return Double.compare(ant1.getCost(), ant2.getCost());
            }
        });
    }

    public static double getEuclideanDistance(Node n1, Node n2) {
        double x1 = n1.getX() - n2.getX();
        double y1 = n1.getY() - n2.getY();
        return Math.sqrt(x1 * x1 + y1 * y1);
    }
}
