import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
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

    public ArrayList<Integer> bfs(int s, int t) {
        LinkedList<ArrayList<Integer>> queue = new LinkedList<ArrayList<Integer>>();
        boolean[] visited = new boolean[capacity.length];
        visited[s] = true;
        ArrayList<Integer> p = new ArrayList<Integer>(1);
        p.add(s);

        queue.add(p);

        while (queue.size() != 0) {
            ArrayList<Integer> path = queue.pop();
            int n = path.get(path.size()-1);

            for (int i = 0; i < capacity[s].length; i++) {
                if (capacity[n][i] != 0 && !visited[i]) {
                    visited[n] = true;
                    ArrayList<Integer> updated = new ArrayList<Integer>();
                    for (int num: path) {
                        updated.add(num);
                    }
                    updated.add(i);
                    queue.add(updated);

                    if (i == t) {
                        return updated;
                    }
                }
            }
        }
        return null;
        
    }

    public static void main(String[] args) {
        Graph graph0 = new Graph();
        graph0.makeGraph("demands5.txt");
        System.out.print(graph0);
        System.out.println(graph0.bfs(1, 7));
    }
}