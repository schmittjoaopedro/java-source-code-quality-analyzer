package simulator;

import org.apache.log4j.Logger;
import simulator.aco.RouteSolver;
import simulator.graph.Graph;
import simulator.graph.Node;
import simulator.reader.JSONConverter;
import simulator.reader.TSPConverter;
import simulator.utils.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Run {

    static {
        System.setProperty("rootPath", (new File("logs")).getAbsolutePath() + "/");
    }

    public static void main(String[] args) throws Exception {

        Logger.getLogger(Run.class).info("Initializing...");
        try {
            for(ProgramInstance programInstance : ProgramReader.getProgram()) {
                runSimulation(programInstance);
            }
        } catch (Exception ex) {
            Logger.getLogger(Run.class).error(ex);
        }

    }

    public static void runSimulation(ProgramInstance programInstance) {

        int trialSize = 30;
        int iterationSize = 100 * programInstance.frequency;
        double lowerBound = 0;
        double upperBound = 2;

        Logger.getLogger(Run.class).info("Analysis = " + programInstance.fileName);

        GenericStatistics genericStatistics = new GenericStatistics(iterationSize, trialSize, programInstance.fileName);
        for(int t = 0; t < trialSize; t++) {

            Logger.getLogger(Run.class).info("Trial = " + t);

            Graph graph;
            int startNode;
            List<Node> targets = new ArrayList<>();
            if(programInstance.problemType.equals("TSP")) {
                startNode = 1;
                String tspFile = (new File("maps")).getAbsolutePath() + "/kroA" + programInstance.nVertices + ".tsp";
                graph = TSPConverter.readGraph(tspFile);
                for(Node node : graph.getNodes()) {
                    targets.add(node);
                }
            } else {
                startNode = 553;
                String jsonFile = (new File("maps")).getAbsolutePath() + "/joinville.json";
                graph = JSONConverter.readGraph(jsonFile);
                if(programInstance.nVertices == 46)
                    tsp46(graph, targets);
                if(programInstance.nVertices == 78)
                    tsp78(graph, targets);
                if(programInstance.nVertices == 125)
                    tsp125(graph, targets);
            }

            RouteSolver routeSolver = new RouteSolver(graph, graph.getNode(startNode), targets, t, genericStatistics, programInstance.algorithm);
            Simulator simulator = null;
            if(programInstance.isSimulated)
                simulator = new Simulator(graph, graph.getNode(startNode), targets, routeSolver, iterationSize / targets.size(), false);
//            DynamicEdgeGenerator dynamicEdgeGenerator = new DynamicEdgeGenerator(graph, programInstance.magnitude, programInstance.frequency, lowerBound, upperBound);
//            dynamicEdgeGenerator.setDynamicListener(routeSolver);
//            dynamicEdgeGenerator.setCycle(programInstance.cycle, programInstance.period);
            DynamicRouteGenerator dynamicRouteGenerator = new DynamicRouteGenerator(programInstance.magnitude, programInstance.frequency, lowerBound, upperBound, routeSolver.getRoutes(), programInstance.seed * t);
            dynamicRouteGenerator.setCycle(programInstance.cycle, programInstance.period);

            if(simulator != null) simulator.setup();
            routeSolver.setup();
            Long time = System.currentTimeMillis();
            for (int i = 1; i < iterationSize; i++) {
//                dynamicEdgeGenerator.loop(i);
                dynamicRouteGenerator.loop(i);
                routeSolver.loop(i);
                if(simulator != null) simulator.loop(i);
                if(i % 100 == 0) {
                    Logger.getLogger(Run.class).info("Iteration = " + i);
                }
            }
            Logger.getLogger(Run.class).info("Time taken = " + (System.currentTimeMillis() - time));
            Stack<Node> result = routeSolver.getResultTour();
            Logger.getLogger(Run.class).info("Fitness evaluation count = " + routeSolver.getFECount());
            String resultRoute = "";
            for(int i = 0; i < result.size(); i++) {
                resultRoute += result.get(i).getId();
                if(i < result.size() - 1) {
                    resultRoute += "->";
                }
            }
            Logger.getLogger(Run.class).info(resultRoute);
            if(simulator != null) simulator.finish();
        }
        genericStatistics.dispose();
        Logger.getLogger(Run.class).info("Finished");
        System.gc();
    }

    private static void tsp46(Graph graph, List<Node> targets) {
        targets.add(graph.getNode(553));
        targets.add(graph.getNode(1201));
        targets.add(graph.getNode(43));
        targets.add(graph.getNode(171));
        targets.add(graph.getNode(103));
        targets.add(graph.getNode(1980));
        targets.add(graph.getNode(155));
        targets.add(graph.getNode(2336));
        targets.add(graph.getNode(420));
        targets.add(graph.getNode(310));
        targets.add(graph.getNode(1500));
        targets.add(graph.getNode(2100));
        targets.add(graph.getNode(16));
        targets.add(graph.getNode(720));
        targets.add(graph.getNode(1700));
        //
        targets.add(graph.getNode(1414));
        targets.add(graph.getNode(500));
        targets.add(graph.getNode(113));
        targets.add(graph.getNode(1800));
        targets.add(graph.getNode(1920));
        targets.add(graph.getNode(250));
        targets.add(graph.getNode(125));
        targets.add(graph.getNode(456));
        targets.add(graph.getNode(1285));
        targets.add(graph.getNode(2232));
        targets.add(graph.getNode(209));
        targets.add(graph.getNode(543));
        targets.add(graph.getNode(876));
        targets.add(graph.getNode(905));
        targets.add(graph.getNode(120));
        targets.add(graph.getNode(341));
        targets.add(graph.getNode(1863));
        targets.add(graph.getNode(468));
        targets.add(graph.getNode(1025));
        targets.add(graph.getNode(1902));
        targets.add(graph.getNode(1374));
        targets.add(graph.getNode(2001));
        targets.add(graph.getNode(1572));
        targets.add(graph.getNode(1932));
        targets.add(graph.getNode(1233));
        targets.add(graph.getNode(1356));
        targets.add(graph.getNode(621));
        targets.add(graph.getNode(217));
        targets.add(graph.getNode(842));
        targets.add(graph.getNode(183));
        targets.add(graph.getNode(474));
        targets.add(graph.getNode(4));
    }

    private static void tsp78(Graph graph, List<Node> targets) {
        targets.add(graph.getNode(553));
        targets.add(graph.getNode(1201));
        targets.add(graph.getNode(43));
        targets.add(graph.getNode(171));
        targets.add(graph.getNode(103));
        targets.add(graph.getNode(1980));
        targets.add(graph.getNode(155));
        targets.add(graph.getNode(2336));
        targets.add(graph.getNode(420));
        targets.add(graph.getNode(310));
        targets.add(graph.getNode(1500));
        targets.add(graph.getNode(2100));
        targets.add(graph.getNode(16));
        targets.add(graph.getNode(720));
        targets.add(graph.getNode(1700));
        //
        targets.add(graph.getNode(1414));
        targets.add(graph.getNode(500));
        targets.add(graph.getNode(113));
        targets.add(graph.getNode(1800));
        targets.add(graph.getNode(1920));
        targets.add(graph.getNode(250));
        targets.add(graph.getNode(125));
        targets.add(graph.getNode(456));
        targets.add(graph.getNode(1285));
        targets.add(graph.getNode(2232));
        targets.add(graph.getNode(209));
        targets.add(graph.getNode(543));
        targets.add(graph.getNode(876));
        targets.add(graph.getNode(905));
        targets.add(graph.getNode(120));
        targets.add(graph.getNode(341));
        targets.add(graph.getNode(1863));
        targets.add(graph.getNode(468));
        targets.add(graph.getNode(1025));
        targets.add(graph.getNode(1902));
        targets.add(graph.getNode(1374));
        targets.add(graph.getNode(2001));
        targets.add(graph.getNode(1572));
        targets.add(graph.getNode(1932));
        targets.add(graph.getNode(1233));
        targets.add(graph.getNode(1356));
        targets.add(graph.getNode(621));
        targets.add(graph.getNode(217));
        targets.add(graph.getNode(842));
        targets.add(graph.getNode(183));
        targets.add(graph.getNode(474));
        targets.add(graph.getNode(4));
        //
        targets.add(graph.getNode(1230));
        targets.add(graph.getNode(520));
        targets.add(graph.getNode(333));
        targets.add(graph.getNode(315));
        targets.add(graph.getNode(610));
        targets.add(graph.getNode(1396));
        targets.add(graph.getNode(900));
        targets.add(graph.getNode(2346));
        targets.add(graph.getNode(1235));
        targets.add(graph.getNode(102));
        targets.add(graph.getNode(752));
        targets.add(graph.getNode(246));
        targets.add(graph.getNode(985));
        targets.add(graph.getNode(2032));
        targets.add(graph.getNode(1203));
        targets.add(graph.getNode(1750));
        targets.add(graph.getNode(302));
        targets.add(graph.getNode(564));
        targets.add(graph.getNode(795));
        targets.add(graph.getNode(1865));
        targets.add(graph.getNode(1132));
        targets.add(graph.getNode(1436));
        targets.add(graph.getNode(935));
        targets.add(graph.getNode(1289));
        targets.add(graph.getNode(678));
        targets.add(graph.getNode(712));
        targets.add(graph.getNode(1956));
        targets.add(graph.getNode(650));
        targets.add(graph.getNode(1550));
        targets.add(graph.getNode(25));
        targets.add(graph.getNode(489));
        targets.add(graph.getNode(368));
    }

    private static void tsp125(Graph graph, List<Node> targets) {
        targets.add(graph.getNode(553));
        targets.add(graph.getNode(1201));
        targets.add(graph.getNode(43));
        targets.add(graph.getNode(171));
        targets.add(graph.getNode(103));
        targets.add(graph.getNode(1980));
        targets.add(graph.getNode(155));
        targets.add(graph.getNode(2336));
        targets.add(graph.getNode(420));
        targets.add(graph.getNode(310));
        targets.add(graph.getNode(1500));
        targets.add(graph.getNode(2100));
        targets.add(graph.getNode(16));
        targets.add(graph.getNode(720));
        targets.add(graph.getNode(1700));
        targets.add(graph.getNode(1414));
        targets.add(graph.getNode(500));
        targets.add(graph.getNode(113));
        targets.add(graph.getNode(1800));
        targets.add(graph.getNode(1920));
        targets.add(graph.getNode(250));
        targets.add(graph.getNode(125));
        targets.add(graph.getNode(456));
        targets.add(graph.getNode(1285));
        targets.add(graph.getNode(2232));
        targets.add(graph.getNode(209));
        targets.add(graph.getNode(543));
        targets.add(graph.getNode(876));
        targets.add(graph.getNode(905));
        targets.add(graph.getNode(120));
        targets.add(graph.getNode(341));
        targets.add(graph.getNode(1863));
        targets.add(graph.getNode(468));
        targets.add(graph.getNode(1025));
        targets.add(graph.getNode(1902));
        targets.add(graph.getNode(1374));
        targets.add(graph.getNode(2001));
        targets.add(graph.getNode(1572));
        targets.add(graph.getNode(1932));
        targets.add(graph.getNode(1233));
        targets.add(graph.getNode(1356));
        targets.add(graph.getNode(621));
        targets.add(graph.getNode(217));
        targets.add(graph.getNode(842));
        targets.add(graph.getNode(183));
        targets.add(graph.getNode(474));
        targets.add(graph.getNode(4));
        targets.add(graph.getNode(1230));
        targets.add(graph.getNode(520));
        targets.add(graph.getNode(333));
        targets.add(graph.getNode(315));
        targets.add(graph.getNode(610));
        targets.add(graph.getNode(1396));
        targets.add(graph.getNode(900));
        targets.add(graph.getNode(2346));
        targets.add(graph.getNode(1235));
        targets.add(graph.getNode(102));
        targets.add(graph.getNode(752));
        targets.add(graph.getNode(246));
        targets.add(graph.getNode(985));
        targets.add(graph.getNode(2032));
        targets.add(graph.getNode(1203));
        targets.add(graph.getNode(1750));
        targets.add(graph.getNode(302));
        targets.add(graph.getNode(564));
        targets.add(graph.getNode(795));
        targets.add(graph.getNode(1865));
        targets.add(graph.getNode(1132));
        targets.add(graph.getNode(1436));
        targets.add(graph.getNode(935));
        targets.add(graph.getNode(1289));
        targets.add(graph.getNode(678));
        targets.add(graph.getNode(712));
        targets.add(graph.getNode(1956));
        targets.add(graph.getNode(650));
        targets.add(graph.getNode(1550));
        targets.add(graph.getNode(25));
        targets.add(graph.getNode(489));
        targets.add(graph.getNode(368));
        targets.add(graph.getNode(498));
        targets.add(graph.getNode(484));
        targets.add(graph.getNode(1262));
        targets.add(graph.getNode(1435));
        targets.add(graph.getNode(634));
        targets.add(graph.getNode(245));
        targets.add(graph.getNode(111));
        targets.add(graph.getNode(150));
        targets.add(graph.getNode(222));
        targets.add(graph.getNode(262));
        targets.add(graph.getNode(444));
        targets.add(graph.getNode(734));
        targets.add(graph.getNode(555));
        targets.add(graph.getNode(517));
        targets.add(graph.getNode(666));
        targets.add(graph.getNode(618));
        targets.add(graph.getNode(777));
        targets.add(graph.getNode(754));
        targets.add(graph.getNode(888));
        targets.add(graph.getNode(1842));
        targets.add(graph.getNode(961));
        targets.add(graph.getNode(1011));
        targets.add(graph.getNode(1187));
        targets.add(graph.getNode(1111));
        targets.add(graph.getNode(1222));
        targets.add(graph.getNode(1077));
        targets.add(graph.getNode(1333));
        targets.add(graph.getNode(1544));
        targets.add(graph.getNode(1444));
        targets.add(graph.getNode(1263));
        targets.add(graph.getNode(1555));
        targets.add(graph.getNode(1388));
        targets.add(graph.getNode(1666));
        targets.add(graph.getNode(1314));
        targets.add(graph.getNode(1777));
        targets.add(graph.getNode(1442));
        targets.add(graph.getNode(1888));
        targets.add(graph.getNode(1718));
        targets.add(graph.getNode(1999));
        targets.add(graph.getNode(1200));
        targets.add(graph.getNode(1300));
        targets.add(graph.getNode(1400));
        targets.add(graph.getNode(1501));
        targets.add(graph.getNode(1600));
        targets.add(graph.getNode(2000));
        targets.add(graph.getNode(2101));
    }

}
