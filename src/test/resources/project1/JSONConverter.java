package simulator.reader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import simulator.graph.Graph;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

public class JSONConverter {

    public static Graph readGraph(String jsonFile) {
        try {
            Graph graph = new Graph();
            FileInputStream fisTargetFile = new FileInputStream(new File(jsonFile));
            String fileContent = IOUtils.toString(fisTargetFile, "UTF-8");
            JsonParser parser = new JsonParser();
            JsonArray rootArray = parser.parse(fileContent).getAsJsonArray();

            Iterator<JsonElement> nodesIterator = rootArray.iterator();
            while(nodesIterator.hasNext()) {
                JsonObject element = nodesIterator.next().getAsJsonObject();
                graph.addNode(
                    element.get("id").getAsInt(),
                    element.get("lng").getAsDouble(),
                    element.get("lat").getAsDouble()
                );
            }

            nodesIterator = rootArray.iterator();
            while(nodesIterator.hasNext()) {
                JsonObject element = nodesIterator.next().getAsJsonObject();
                JsonObject edges = element.getAsJsonObject("nodes");
                if(edges != null) {
                    for (String key : edges.keySet()) {
                        if (element.get("id").getAsInt() != Integer.parseInt(key)) {
                            graph.addEdge(
                                    element.get("id").getAsInt(),
                                    Integer.parseInt(key),
                                    edges.getAsJsonObject(key).get("distance").getAsDouble()
                            );
                        }
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
