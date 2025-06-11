import java.io.*;
import java.util.*;

public class GraphAnalysis {
    // --- Attribute ---
    private int[][] adjMatrix; // Adjazenzmatrix zur Speicherung des Graphen
    private int nodeCount;     // Anzahl der Knoten im Graph

    // --- Hauptmethode ---
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Gib den Pfad zur CSV-Datei ein: ");
        String filePath = scanner.nextLine();

        GraphAnalysis ga = new GraphAnalysis();
        ga.loadMatrix(filePath);
        ga.analyzeGraph();
    }

    // --- 1. Einlesen der CSV-Datei ---
    public void loadMatrix(String fileName) throws IOException {
        List<int[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                int[] row = Arrays.stream(parts).mapToInt(Integer::parseInt).toArray();
                rows.add(row);
            }
        }
        nodeCount = rows.size();
        adjMatrix = new int[nodeCount][nodeCount];
        for (int i = 0; i < nodeCount; i++) {
            adjMatrix[i] = rows.get(i);
        }
    }

    // --- 2. Hauptanalyse ---
    public void analyzeGraph() {
        int[][] distances = new int[nodeCount][nodeCount];
        int[] eccentricities = new int[nodeCount];

        for (int i = 0; i < nodeCount; i++) {
            distances[i] = bfsDistances(i);
            eccentricities[i] = Arrays.stream(distances[i]).max().getAsInt();
        }

        int radius = Arrays.stream(eccentricities).min().getAsInt();
        int diameter = Arrays.stream(eccentricities).max().getAsInt();
        List<Integer> center = new ArrayList<>();
        for (int i = 0; i < nodeCount; i++) {
            if (eccentricities[i] == radius) {
                center.add(i);
            }
        }

        System.out.println("\n--- Analyse ---");
        System.out.println("Exzentrizitäten:");
        for (int i = 0; i < nodeCount; i++) {
            System.out.println("Knoten " + i + ": " + eccentricities[i]);
        }
        System.out.println("\nRadius: " + radius);
        System.out.println("Durchmesser: " + diameter);
        System.out.println("Zentrum: " + center);

        System.out.println("\n--- Suche ---");
        System.out.print("DFS ab Knoten 0: ");
        boolean[] visitedDFS = new boolean[nodeCount];
        dfs(0, visitedDFS);
        System.out.println();

        System.out.print("BFS ab Knoten 0: ");
        bfs(0);
        System.out.println();

        System.out.println("\n--- Dijkstra (kürzeste Wege von Knoten 0) ---");
        dijkstra(0);
    }

    // --- 3. BFS-Distanzermittlung ---
    private int[] bfsDistances(int start) {
        int[] dist = new int[nodeCount];
        Arrays.fill(dist, -1);
        Queue<Integer> queue = new LinkedList<>();
        dist[start] = 0;
        queue.add(start);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            for (int neighbor = 0; neighbor < nodeCount; neighbor++) {
                if (adjMatrix[current][neighbor] != 0 && dist[neighbor] == -1) {
                    dist[neighbor] = dist[current] + 1;
                    queue.add(neighbor);
                }
            }
        }
        return dist;
    }

    // --- 4. DFS ---
    public void dfs(int start, boolean[] visited) {
        visited[start] = true;
        System.out.print(start + " ");
        for (int neighbor = 0; neighbor < nodeCount; neighbor++) {
            if (adjMatrix[start][neighbor] != 0 && !visited[neighbor]) {
                dfs(neighbor, visited);
            }
        }
    }

    // --- 5. BFS ---
    public void bfs(int start) {
        boolean[] visited = new boolean[nodeCount];
        Queue<Integer> queue = new LinkedList<>();
        visited[start] = true;
        queue.add(start);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            System.out.print(current + " ");
            for (int neighbor = 0; neighbor < nodeCount; neighbor++) {
                if (adjMatrix[current][neighbor] != 0 && !visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.add(neighbor);
                }
            }
        }
    }

    // --- 6. Dijkstra-Algorithmus ---
    public void dijkstra(int start) {
        int[] dist = new int[nodeCount];
        boolean[] visited = new boolean[nodeCount];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[start] = 0;

        for (int i = 0; i < nodeCount - 1; i++) {
            int u = getMinDistanceNode(dist, visited);
            if (u == -1) break;
            visited[u] = true;

            for (int v = 0; v < nodeCount; v++) {
                if (!visited[v] && adjMatrix[u][v] > 0 && dist[u] != Integer.MAX_VALUE &&
                        dist[u] + adjMatrix[u][v] < dist[v]) {
                    dist[v] = dist[u] + adjMatrix[u][v];
                }
            }
        }

        for (int i = 0; i < nodeCount; i++) {
            if (dist[i] == Integer.MAX_VALUE) {
                System.out.println("Knoten " + i + ": nicht erreichbar");
            } else {
                System.out.println("Knoten " + i + ": Distanz = " + dist[i]);
            }
        }
    }

    private int getMinDistanceNode(int[] dist, boolean[] visited) {
        int minDist = Integer.MAX_VALUE;
        int minIndex = -1;
        for (int i = 0; i < nodeCount; i++) {
            if (!visited[i] && dist[i] < minDist) {
                minDist = dist[i];
                minIndex = i;
            }
        }
        return minIndex;
    }
}
