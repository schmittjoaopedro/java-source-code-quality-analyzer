package simulator.aco;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RouteManager {

    private Globals _globals;

    private Set<Route> routes;

    private Map<Integer, Set<Route>> routesIndex;

    private Map<String, Route> routesMapIndex;

    public RouteManager(Globals globals) {
        super();
        _globals = globals;
        routes = new HashSet<>();
        routesIndex = new HashMap<>();
        routesMapIndex = new HashMap<>();
    }

    public void addRoute(int from, int to) {
        String key = from + "->" + to;
        if(!routesMapIndex.containsKey(key)) {
            Route route = new Route(_globals.graph, _globals.graph.getNode(from), _globals.graph.getNode(to));
            routes.add(route);
            if(!routesIndex.containsKey(from)) {
                routesIndex.put(from, new HashSet<>());
            }
            routesIndex.get(from).add(route);
            routesMapIndex.put(key, route);
        }
    }

    public Set<Route> getRoutes() {
        return routes;
    }

    public Set<Route> getRoutes(int from) {
        return routesIndex.get(from);
    }

    public Route getRoute(int from, int to) {
        return routesMapIndex.get(from + "->" + to);
    }

    public void updateRoutes(boolean cycled, int phase) {
        for(Route route : routes) {
            route.calculateCost(cycled, phase);
        }
    }

}
