package simulator.reader;

import org.apache.commons.io.IOUtils;
import simulator.graph.Graph;
import simulator.graph.Node;
import simulator.utils.Utils;

import java.io.File;
import java.io.FileInputStream;

public class TSPConverter {

    public static Graph readGraph(String tspFile) {
        try {
            Graph graph = new Graph();
            FileInputStream fisTargetFile = new FileInputStream(new File(tspFile));
            String fileContent[] = IOUtils.toString(fisTargetFile, "UTF-8").split("\n");
            boolean started = false;
            for(int l = 0; l < fileContent.length; l++) {
                if(fileContent[l].equals("EOF")) {
                    break;
                }
                if(started) {
                    String[] data = fileContent[l].split(" ");
                    graph.addNode(Integer.valueOf(data[0]) - 1, Double.valueOf(data[1]), Double.valueOf(data[2]));
                }
                if(fileContent[l].equals("NODE_COORD_SECTION")) {
                    started = true;
                }
            }
            for(Node n1 : graph.getNodes()) {
                for(Node n2 : graph.getNodes()) {
                    if(n1 != n2) {
                        graph.addEdge(n1.getId(), n2.getId(), Utils.getEuclideanDistance(n1, n2));
                    }
                }
            }
            return graph;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
