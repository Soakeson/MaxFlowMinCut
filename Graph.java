import java.io.File;
import java.util.Scanner;
import java.util.Vector;
import java.util.ArrayList;
import java.util.LinkedList;

public class Graph {
    int vertexCt;  // Number of vertices in the graph.
    int[][] capacity;  // Adjacency  matrix
    String graphName;  //The file from which the graph was created.

    public Graph() {
        this.vertexCt = 0;
        this.graphName = "";
    }


    public int getVertexCt() {
        return vertexCt;
    }

    public boolean addEdge(int source, int destination, int cap) {
        //System.out.println("addEdge " + source + "->" + destination + "(" + cap + ")");
        if (source < 0 || source >= vertexCt) return false;
        if (destination < 0 || destination >= vertexCt) return false;
        capacity[source][destination] = cap;

        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nThe Graph " + graphName + " \n");

        for (int i = 0; i < vertexCt; i++) {
            for (int j = 0; j < vertexCt; j++) {
                sb.append(String.format("%5d", capacity[i][j]));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public void makeGraph(String filename) {
        try {
            graphName = filename;
            Scanner reader = new Scanner(new File(filename));
            vertexCt = reader.nextInt();
            capacity = new int[vertexCt][vertexCt];
            for (int i = 0; i < vertexCt; i++) {
                for (int j = 0; j < vertexCt; j++) {
                    capacity[i][j] = 0;
                }
            }
            while (reader.hasNextInt()) {
                int v1 = reader.nextInt();
                int v2 = reader.nextInt();
                int cap = reader.nextInt();
                if (!addEdge(v1, v2, cap))
                    throw new Exception();
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


        /**
     * 
     * @param source starting point
     * @param sink end point
     * @return arraylist containing the path from source to sink, the last value is the max flow on that path
     */
    private Vector<Integer> bfs(int source, int sink) {
        LinkedList<Vector<Integer>> queue = new LinkedList<Vector<Integer>>();
        boolean[] visited = new boolean[vertexCt];
        visited[source] = true;
        Vector<Integer> path = new Vector<>();
        path.add(source);

        queue.add(path);

        while(!queue.isEmpty()) {
            path = queue.pop();
            int curr = path.get(path.size() - 1);

            if (curr == sink) {
                return path;
            }


            for (int destination = 0; destination < capacity.length; destination++) {
                if (capacity[curr][destination] != 0 && !visited[destination]) {
                    visited[curr] = true;
                    Vector<Integer> updated = new Vector<>(path);
                    updated.add(destination);
                    queue.add(updated);
                }
            } 
        }
        return null;
    }

    public int maxFlow(int source, int sink, Graph residual) {
        int flow = 0;
        Vector<Integer> augmented = residual.bfs(source, sink);
        
        while (augmented != null) {
            int currFlow = Integer.MAX_VALUE;
            
            // Find the min flow on the augmented path
            int parent = source;
            for (int i=1; i < augmented.size(); i++) {
                int next = augmented.get(i);
                if (capacity[parent][next] < currFlow) currFlow = capacity[parent][next];
                parent = next;
            }
            System.out.printf("Found Flow %d: " + augmented + "\n", currFlow);
            flow += currFlow;
            
            // Work backwards because the parent is set at the end of the path, update flow along the path
            for (int i = augmented.size()-2; i >= 0; i--) {
                int prev = augmented.get(i);
                capacity[prev][parent] -= currFlow;
                capacity[parent][prev] += currFlow;
                parent = prev;
            }

            augmented = residual.bfs(source, sink);
        }

        return flow;
    }

    public void minCut(int source, int sink, Graph residual) {
        boolean[] reachable = dfs(source);

        for (int i = 0; i < vertexCt; i++) {
            for (int j = 0; j < vertexCt; j++) {
                if (capacity[i][j] > 0 && reachable[j] && !reachable[i]) {
                    System.out.printf("Edge (%d, %d) transports cases %d \n", j, i, capacity[i][j]);
                }
            }
        }
    }

    public boolean[] dfs(int source) {
        boolean[] visited = new boolean[vertexCt];
        dfs(source, visited);
        return visited;
    }

    private void dfs(int source, boolean[] visited) {
        visited[source] = true;
        for (int i = 0; i < vertexCt; i++) {
            if (capacity[source][i] > 0 && !visited[i])
                dfs(i, visited);
        }
    }


    public static void main(String[] args) {
        Graph graph0 = new Graph();
        Graph residual = graph0;
        graph0.makeGraph("demands6.txt");
        System.out.print(graph0);
        System.out.printf("PRODUCED %d:\n", graph0.maxFlow(0, graph0.vertexCt - 1, residual));
        graph0.minCut(0, graph0.vertexCt - 1,residual);

    }
}