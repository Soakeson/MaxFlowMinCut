import java.io.File;
import java.util.Scanner;
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

    private ArrayList<Integer> bfs(int source, int sink) {
        LinkedList<ArrayList<Integer>> queue = new LinkedList<ArrayList<Integer>>();
        boolean[] visited = new boolean[capacity.length];
        visited[source] = true;
        ArrayList<Integer> pred = new ArrayList<Integer>();
        pred.add(source);
        int flow = -1;

        queue.add(pred);

        while (queue.size() != 0) {
            ArrayList<Integer> path = queue.pop();
            int parent = path.get(path.size()-1);

            for (int destination = 0; destination < capacity.length; destination++) {
                if (capacity[parent][destination] != 0 && !visited[destination]) {
                    if (flow == -1 || capacity[parent][destination] < flow) flow = capacity[parent][destination]; 
                    visited[parent] = true;
                    ArrayList<Integer> updated = new ArrayList<Integer>();
                    for (int num: path) {
                        updated.add(num);
                    }
                    updated.add(destination);
                    queue.add(updated);

                    if (destination == sink) {
                        updated.add(flow);
                        return updated;
                    }
                }
            }
        }
        return null;
    }

    public int maxFlow(int source, int sink) {
        System.out.println("MAX FLOW");
        int flow = 0;
        Graph residual = this;
        ArrayList<Integer> augmented = residual.bfs(source, sink);
        
        while (augmented != null) {
            int currFlow = augmented.remove(augmented.size()-1);
            System.out.printf("Found Flow %d: " + augmented + "\n", currFlow);
            flow += currFlow;
            int parent = source;

            for (int i=0; i < augmented.size()-1; i++) {
                int next = augmented.get(i);
                capacity[parent][next] -= currFlow;
                capacity[next][parent] += currFlow;
                parent = next;
            }
            augmented = residual.bfs(source, sink);
        }

        return flow;

    }


    public static void main(String[] args) {
        Graph graph0 = new Graph();
        graph0.makeGraph("demands1.txt");
        System.out.print(graph0);
        System.out.printf("PRODUCED: %d\n", graph0.maxFlow(0, graph0.vertexCt - 1));
    }
}