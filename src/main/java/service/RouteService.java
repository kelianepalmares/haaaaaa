package service;

import java.sql.SQLException;
import java.util.ArrayList;
import structures.RouteGraph;

public class RouteService {

    // Binubuo ang graph mula sa locations at routes ng database
    public static RouteGraph buildGraph() throws SQLException {
        RouteGraph graph = new RouteGraph();
        LocationDAO dao = new LocationDAO();

        ArrayList<String> locations = dao.getAllLocations();
        for (int i = 0; i < locations.size(); i++) {
            graph.addNode(locations.get(i));
        }

        ArrayList<String[]> routes = dao.getAllRoutes();
        for (int i = 0; i < routes.size(); i++) {
            String[] r = routes.get(i);
            graph.addEdge(r[1], r[2], Double.parseDouble(r[3]));
        }
        return graph;
    }

    public static String pathToString(ArrayList<String> path) {
        String text = "";
        for (int i = 0; i < path.size(); i++) {
            text += path.get(i);
            if (i < path.size() - 1) {
                text += " -> ";
            }
        }
        return text;
    }
}