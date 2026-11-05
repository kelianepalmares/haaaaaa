package structures;

import java.util.ArrayList;

public class RouteGraph {

    private class Edge {
        int to;
        double weight;

        Edge(int to, double weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    private ArrayList<String> nodes = new ArrayList<>();
    private ArrayList<ArrayList<Edge>> adjacency = new ArrayList<>();
    private double lastDistance = 0;

    public int indexOf(String name) {
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

    public void addNode(String name) {
        if (indexOf(name) == -1) {
            nodes.add(name);
            adjacency.add(new ArrayList<Edge>());
        }
    }

    // Two-way road
    public void addEdge(String a, String b, double weight) {
        addNode(a);
        addNode(b);
        int i = indexOf(a);
        int j = indexOf(b);
        adjacency.get(i).add(new Edge(j, weight));
        adjacency.get(j).add(new Edge(i, weight));
    }

    // Dijkstra. Walang laman ang list kung walang route.
    public ArrayList<String> shortestPath(String from, String to) {
        lastDistance = 0;
        ArrayList<String> path = new ArrayList<>();
        int start = indexOf(from);
        int end = indexOf(to);
        if (start == -1 || end == -1) {
            return path;
        }

        int n = nodes.size();
        double[] dist = new double[n];
        int[] previous = new int[n];
        boolean[] done = new boolean[n];
        for (int i = 0; i < n; i++) {
            dist[i] = Double.MAX_VALUE;
            previous[i] = -1;
        }
        dist[start] = 0;

        for (int count = 0; count < n; count++) {
            int u = -1;
            for (int i = 0; i < n; i++) {
                if (!done[i] && dist[i] != Double.MAX_VALUE && (u == -1 || dist[i] < dist[u])) {
                    u = i;
                }
            }
            if (u == -1) {
                break;
            }
            done[u] = true;

            ArrayList<Edge> edges = adjacency.get(u);
            for (int k = 0; k < edges.size(); k++) {
                Edge e = edges.get(k);
                if (!done[e.to] && dist[u] + e.weight < dist[e.to]) {
                    dist[e.to] = dist[u] + e.weight;
                    previous[e.to] = u;
                }
            }
        }

        if (dist[end] == Double.MAX_VALUE) {
            return path;
        }

        lastDistance = dist[end];
        int current = end;
        while (current != -1) {
            path.add(0, nodes.get(current));
            current = previous[current];
        }
        return path;
    }

    public double getLastDistance() {
        return lastDistance;
    }
}