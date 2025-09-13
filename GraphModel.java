package com.simulator;

import java.util.*;

/**
 * Enhanced Graph Model with weighted edges support and advanced algorithms
 * Supports both directed/undirected and weighted/unweighted graphs
 */
public class GraphModel {
    // Proper generics for type safety
    private Map<Integer, List<Integer>> adjacencyList;
    private Map<String, Double> edgeWeights; // For weighted edges
    private boolean isDirected;
    private boolean isWeighted;

    // ==================== CONSTRUCTORS ====================

    public GraphModel(boolean isDirected, boolean isWeighted) {
        this.adjacencyList = new HashMap<>();
        this.edgeWeights = new HashMap<>();
        this.isDirected = isDirected;
        this.isWeighted = isWeighted;
    }

    public GraphModel(boolean isDirected) {
        this(isDirected, false); // Default to unweighted
    }

    public GraphModel() {
        this(false, false); // Default to undirected, unweighted
    }

    // ==================== BASIC OPERATIONS ====================

    /**
     * Add a vertex to the graph
     */
    public void addVertex(int vertex) {
        adjacencyList.putIfAbsent(vertex, new ArrayList<>());
        System.out.println("Added vertex: " + vertex);
    }

    /**
     * Add an edge with default weight of 1.0
     */
    public void addEdge(int source, int destination) {
        addEdge(source, destination, 1.0); // Default weight = 1.0
    }

    /**
     * Add an edge with specified weight
     */
    public void addEdge(int source, int destination, double weight) {
        addVertex(source);
        addVertex(destination);

        adjacencyList.get(source).add(destination);
        if (!isDirected) {
            adjacencyList.get(destination).add(source);
        }

        if (isWeighted) {
            String edgeKey = source + "-" + destination;
            edgeWeights.put(edgeKey, weight);
            if (!isDirected) {
                edgeWeights.put(destination + "-" + source, weight);
            }
        }

        System.out.println("Added edge: " + source + " -> " + destination +
                (isWeighted ? " (weight: " + weight + ")" : ""));
    }

    /**
     * Remove a vertex and all its edges
     */
    public void removeVertex(int vertex) {
        if (!adjacencyList.containsKey(vertex)) {
            return;
        }

        // Remove all edges to this vertex
        for (List<Integer> edges : adjacencyList.values()) {
            edges.remove(Integer.valueOf(vertex));
        }

        // Remove edge weights involving this vertex
        if (isWeighted) {
            edgeWeights.entrySet().removeIf(entry ->
                    entry.getKey().startsWith(vertex + "-") ||
                            entry.getKey().endsWith("-" + vertex));
        }

        // Remove the vertex itself
        adjacencyList.remove(vertex);
        System.out.println("Removed vertex: " + vertex);
    }

    /**
     * Remove an edge between two vertices
     */
    public void removeEdge(int source, int destination) {
        if (adjacencyList.containsKey(source)) {
            adjacencyList.get(source).remove(Integer.valueOf(destination));
        }

        if (!isDirected && adjacencyList.containsKey(destination)) {
            adjacencyList.get(destination).remove(Integer.valueOf(source));
        }

        // Remove edge weights
        if (isWeighted) {
            edgeWeights.remove(source + "-" + destination);
            if (!isDirected) {
                edgeWeights.remove(destination + "-" + source);
            }
        }

        System.out.println("Removed edge: " + source + " -> " + destination);
    }

    // ==================== TRAVERSAL ALGORITHMS ====================

    /**
     * Breadth-First Search traversal
     */
    public List<Integer> bfs(int startVertex) {
        List<Integer> result = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();

        queue.offer(startVertex);
        visited.add(startVertex);

        System.out.println("Starting BFS from vertex: " + startVertex);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            result.add(current);
            System.out.println("Visiting: " + current);

            if (adjacencyList.containsKey(current)) {
                for (int neighbor : adjacencyList.get(current)) {
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        queue.offer(neighbor);
                    }
                }
            }
        }

        System.out.println("BFS completed. Visited order: " + result);
        return result;
    }

    /**
     * Depth-First Search traversal
     */
    public List<Integer> dfs(int startVertex) {
        List<Integer> result = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();

        System.out.println("Starting DFS from vertex: " + startVertex);
        dfsHelper(startVertex, visited, result);
        System.out.println("DFS completed. Visited order: " + result);

        return result;
    }

    /**
     * Helper method for DFS recursion
     */
    private void dfsHelper(int vertex, Set<Integer> visited, List<Integer> result) {
        visited.add(vertex);
        result.add(vertex);
        System.out.println("Visiting: " + vertex);

        if (adjacencyList.containsKey(vertex)) {
            for (int neighbor : adjacencyList.get(vertex)) {
                if (!visited.contains(neighbor)) {
                    dfsHelper(neighbor, visited, result);
                }
            }
        }
    }

    // ==================== ADVANCED ALGORITHMS ====================

    /**
     * Dijkstra's shortest path algorithm for weighted graphs
     */
    public DijkstraResult dijkstra(int startVertex, int endVertex) {
        if (!isWeighted) {
            throw new UnsupportedOperationException("Dijkstra requires weighted graph");
        }

        Map<Integer, Double> distances = new HashMap<>();
        Map<Integer, Integer> previous = new HashMap<>();
        Set<Integer> unvisited = new HashSet<>();

        // Initialize distances
        for (int vertex : adjacencyList.keySet()) {
            distances.put(vertex, vertex == startVertex ? 0.0 : Double.POSITIVE_INFINITY);
            unvisited.add(vertex);
        }

        while (!unvisited.isEmpty()) {
            // Find vertex with minimum distance
            int current = unvisited.stream()
                    .min(Comparator.comparingDouble(distances::get))
                    .orElse(-1);

            if (current == -1 || distances.get(current) == Double.POSITIVE_INFINITY) {
                break;
            }

            unvisited.remove(current);

            if (current == endVertex) {
                break;
            }

            // Update distances to neighbors
            if (adjacencyList.containsKey(current)) {
                for (int neighbor : adjacencyList.get(current)) {
                    if (unvisited.contains(neighbor)) {
                        double weight = getEdgeWeight(current, neighbor);
                        double newDistance = distances.get(current) + weight;

                        if (newDistance < distances.get(neighbor)) {
                            distances.put(neighbor, newDistance);
                            previous.put(neighbor, current);
                        }
                    }
                }
            }
        }

        List<Integer> path = reconstructDijkstraPath(previous, startVertex, endVertex);
        return new DijkstraResult(path, distances.get(endVertex), distances, previous);
    }

    /**
     * A* pathfinding algorithm (simplified version)
     */
    public List<Integer> aStar(int startVertex, int endVertex) {
        Map<Integer, Double> gScore = new HashMap<>();
        Map<Integer, Double> fScore = new HashMap<>();
        Map<Integer, Integer> previous = new HashMap<>();
        Set<Integer> openSet = new HashSet<>();
        Set<Integer> closedSet = new HashSet<>();

        gScore.put(startVertex, 0.0);
        fScore.put(startVertex, heuristic(startVertex, endVertex));
        openSet.add(startVertex);

        while (!openSet.isEmpty()) {
            int current = openSet.stream()
                    .min(Comparator.comparingDouble(v -> fScore.getOrDefault(v, Double.POSITIVE_INFINITY)))
                    .orElse(-1);

            if (current == endVertex) {
                return reconstructDijkstraPath(previous, startVertex, endVertex);
            }

            openSet.remove(current);
            closedSet.add(current);

            if (adjacencyList.containsKey(current)) {
                for (int neighbor : adjacencyList.get(current)) {
                    if (closedSet.contains(neighbor)) {
                        continue;
                    }

                    double tentativeGScore = gScore.get(current) + getEdgeWeight(current, neighbor);

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    } else if (tentativeGScore >= gScore.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {
                        continue;
                    }

                    previous.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);
                    fScore.put(neighbor, tentativeGScore + heuristic(neighbor, endVertex));
                }
            }
        }

        return new ArrayList<>(); // No path found
    }

    /**
     * Topological Sort (only for directed acyclic graphs)
     */
    public List<Integer> topologicalSort() {
        if (!isDirected) {
            throw new UnsupportedOperationException("Topological sort requires directed graph");
        }

        Map<Integer, Integer> inDegree = new HashMap<>();

        // Initialize in-degrees
        for (int vertex : adjacencyList.keySet()) {
            inDegree.put(vertex, 0);
        }

        // Calculate in-degrees
        for (List<Integer> neighbors : adjacencyList.values()) {
            for (int neighbor : neighbors) {
                inDegree.put(neighbor, inDegree.get(neighbor) + 1);
            }
        }

        Queue<Integer> queue = new LinkedList<>();
        for (Map.Entry<Integer, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.offer(entry.getKey());
            }
        }

        List<Integer> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            int current = queue.poll();
            result.add(current);

            if (adjacencyList.containsKey(current)) {
                for (int neighbor : adjacencyList.get(current)) {
                    inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                    if (inDegree.get(neighbor) == 0) {
                        queue.offer(neighbor);
                    }
                }
            }
        }

        if (result.size() != adjacencyList.size()) {
            throw new IllegalStateException("Graph contains cycle - topological sort not possible");
        }

        return result;
    }

    // ==================== PATH OPERATIONS ====================

    /**
     * Check if there's a path between two vertices
     */
    public boolean hasPath(int source, int destination) {
        if (!adjacencyList.containsKey(source) || !adjacencyList.containsKey(destination)) {
            return false;
        }

        Set<Integer> visited = new HashSet<>();
        return hasPathHelper(source, destination, visited);
    }

    /**
     * Helper method for path checking using DFS
     */
    private boolean hasPathHelper(int current, int destination, Set<Integer> visited) {
        if (current == destination) {
            return true;
        }

        visited.add(current);

        if (adjacencyList.containsKey(current)) {
            for (int neighbor : adjacencyList.get(current)) {
                if (!visited.contains(neighbor) && hasPathHelper(neighbor, destination, visited)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Find shortest path between two vertices (unweighted)
     */
    public List<Integer> getShortestPath(int source, int destination) {
        if (!adjacencyList.containsKey(source) || !adjacencyList.containsKey(destination)) {
            return new ArrayList<>();
        }

        Map<Integer, Integer> parent = new HashMap<>();
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();

        queue.offer(source);
        visited.add(source);
        parent.put(source, -1);

        while (!queue.isEmpty()) {
            int current = queue.poll();

            if (current == destination) {
                return reconstructPath(parent, source, destination);
            }

            if (adjacencyList.containsKey(current)) {
                for (int neighbor : adjacencyList.get(current)) {
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        parent.put(neighbor, current);
                        queue.offer(neighbor);
                    }
                }
            }
        }

        return new ArrayList<>(); // No path found
    }

    /**
     * Reconstruct path from parent map (for BFS-based shortest path)
     */
    private List<Integer> reconstructPath(Map<Integer, Integer> parent, int source, int destination) {
        List<Integer> path = new ArrayList<>();
        int current = destination;

        while (current != -1) {
            path.add(current);
            current = parent.get(current);
        }

        Collections.reverse(path);
        System.out.println("Shortest path from " + source + " to " + destination + ": " + path);
        return path;
    }

    /**
     * Reconstruct path from previous map (for Dijkstra's algorithm)
     */
    private List<Integer> reconstructDijkstraPath(Map<Integer, Integer> previous, int source, int destination) {
        List<Integer> path = new ArrayList<>();
        Integer current = destination;

        while (current != null) {
            path.add(current);
            current = previous.get(current);
            if (current != null && current == source) {
                path.add(source);
                break;
            }
        }

        if (path.isEmpty() || path.get(path.size() - 1) != source) {
            return new ArrayList<>(); // No path found
        }

        Collections.reverse(path);
        return path;
    }

    // ==================== ADVANCED PATH OPERATIONS ====================

    /**
     * Find all paths between two vertices
     */
    public List<List<Integer>> findAllPaths(int source, int destination) {
        List<List<Integer>> allPaths = new ArrayList<>();
        List<Integer> currentPath = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();

        findAllPathsHelper(source, destination, visited, currentPath, allPaths);
        return allPaths;
    }

    /**
     * Helper method for finding all paths
     */
    private void findAllPathsHelper(int current, int destination, Set<Integer> visited,
                                    List<Integer> currentPath, List<List<Integer>> allPaths) {
        visited.add(current);
        currentPath.add(current);

        if (current == destination) {
            allPaths.add(new ArrayList<>(currentPath));
        } else if (adjacencyList.containsKey(current)) {
            for (int neighbor : adjacencyList.get(current)) {
                if (!visited.contains(neighbor)) {
                    findAllPathsHelper(neighbor, destination, visited, currentPath, allPaths);
                }
            }
        }

        // Backtrack
        currentPath.remove(currentPath.size() - 1);
        visited.remove(current);
    }

    /**
     * Detect if the graph has cycles
     */
    public boolean hasCycle() {
        if (isDirected) {
            return hasDirectedCycle();
        } else {
            return hasUndirectedCycle();
        }
    }

    /**
     * Detect cycles in directed graph using DFS
     */
    private boolean hasDirectedCycle() {
        Set<Integer> visited = new HashSet<>();
        Set<Integer> recStack = new HashSet<>();

        for (int vertex : adjacencyList.keySet()) {
            if (!visited.contains(vertex)) {
                if (hasDirectedCycleUtil(vertex, visited, recStack)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Utility method for directed cycle detection
     */
    private boolean hasDirectedCycleUtil(int vertex, Set<Integer> visited, Set<Integer> recStack) {
        visited.add(vertex);
        recStack.add(vertex);

        if (adjacencyList.containsKey(vertex)) {
            for (int neighbor : adjacencyList.get(vertex)) {
                if (!visited.contains(neighbor)) {
                    if (hasDirectedCycleUtil(neighbor, visited, recStack)) {
                        return true;
                    }
                } else if (recStack.contains(neighbor)) {
                    return true;
                }
            }
        }

        recStack.remove(vertex);
        return false;
    }

    /**
     * Detect cycles in undirected graph using DFS
     */
    private boolean hasUndirectedCycle() {
        Set<Integer> visited = new HashSet<>();

        for (int vertex : adjacencyList.keySet()) {
            if (!visited.contains(vertex)) {
                if (hasUndirectedCycleUtil(vertex, -1, visited)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Utility method for undirected cycle detection
     */
    private boolean hasUndirectedCycleUtil(int vertex, int parent, Set<Integer> visited) {
        visited.add(vertex);

        if (adjacencyList.containsKey(vertex)) {
            for (int neighbor : adjacencyList.get(vertex)) {
                if (!visited.contains(neighbor)) {
                    if (hasUndirectedCycleUtil(neighbor, vertex, visited)) {
                        return true;
                    }
                } else if (neighbor != parent) {
                    return true;
                }
            }
        }

        return false;
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Simple heuristic function for A* (can be improved with actual coordinates)
     */
    private double heuristic(int vertex1, int vertex2) {
        return Math.abs(vertex1 - vertex2);
    }

    /**
     * Get the weight of an edge
     */
    public double getEdgeWeight(int source, int destination) {
        if (!isWeighted) {
            return 1.0;
        }

        return edgeWeights.getOrDefault(source + "-" + destination, Double.POSITIVE_INFINITY);
    }

    /**
     * Get all vertices in the graph
     */
    public Set<Integer> getVertices() {
        return adjacencyList.keySet();
    }

    /**
     * Get neighbors of a vertex
     */
    public List<Integer> getNeighbors(int vertex) {
        return adjacencyList.getOrDefault(vertex, new ArrayList<>());
    }

    /**
     * Get total number of vertices
     */
    public int getVertexCount() {
        return adjacencyList.size();
    }

    /**
     * Get total number of edges
     */
    public int getEdgeCount() {
        int count = 0;
        for (List<Integer> edges : adjacencyList.values()) {
            count += edges.size();
        }

        return isDirected ? count : count / 2;
    }

    /**
     * Get degree of a vertex
     */
    public int getDegree(int vertex) {
        return adjacencyList.getOrDefault(vertex, new ArrayList<>()).size();
    }

    /**
     * Check if the graph is empty
     */
    public boolean isEmpty() {
        return adjacencyList.isEmpty();
    }

    /**
     * Clear all vertices and edges
     */
    public void clear() {
        adjacencyList.clear();
        edgeWeights.clear();
        System.out.println("Graph cleared");
    }

    /**
     * Check if a vertex exists
     */
    public boolean containsVertex(int vertex) {
        return adjacencyList.containsKey(vertex);
    }

    /**
     * Check if an edge exists
     */
    public boolean containsEdge(int source, int destination) {
        return adjacencyList.containsKey(source) &&
                adjacencyList.get(source).contains(destination);
    }

    /**
     * Check if the graph is directed
     */
    public boolean isDirected() {
        return isDirected;
    }

    /**
     * Check if the graph is weighted
     */
    public boolean isWeighted() {
        return isWeighted;
    }

    /**
     * Get graph density (percentage of possible edges that exist)
     */
    public double getDensity() {
        if (adjacencyList.isEmpty()) return 0.0;

        int maxEdges = isDirected ?
                adjacencyList.size() * (adjacencyList.size() - 1) :
                adjacencyList.size() * (adjacencyList.size() - 1) / 2;

        return maxEdges > 0 ? (double) getEdgeCount() / maxEdges : 0.0;
    }

    /**
     * Get connected components (for undirected graphs)
     */
    public List<List<Integer>> getConnectedComponents() {
        if (isDirected) {
            throw new UnsupportedOperationException("Connected components are for undirected graphs");
        }

        List<List<Integer>> components = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();

        for (int vertex : adjacencyList.keySet()) {
            if (!visited.contains(vertex)) {
                List<Integer> component = new ArrayList<>();
                dfsForComponent(vertex, visited, component);
                components.add(component);
            }
        }

        return components;
    }

    /**
     * DFS helper for finding connected components
     */
    private void dfsForComponent(int vertex, Set<Integer> visited, List<Integer> component) {
        visited.add(vertex);
        component.add(vertex);

        if (adjacencyList.containsKey(vertex)) {
            for (int neighbor : adjacencyList.get(vertex)) {
                if (!visited.contains(neighbor)) {
                    dfsForComponent(neighbor, visited, component);
                }
            }
        }
    }

    /**
     * String representation of the graph
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Graph (").append(isDirected ? "Directed" : "Undirected")
                .append(", ").append(isWeighted ? "Weighted" : "Unweighted").append("):\n");

        for (Map.Entry<Integer, List<Integer>> entry : adjacencyList.entrySet()) {
            sb.append("Vertex ").append(entry.getKey()).append(": ");
            sb.append(entry.getValue()).append("\n");
        }

        if (isWeighted && !edgeWeights.isEmpty()) {
            sb.append("\nEdge Weights:\n");
            for (Map.Entry<String, Double> entry : edgeWeights.entrySet()) {
                sb.append(entry.getKey()).append(" -> ").append(entry.getValue()).append("\n");
            }
        }

        return sb.toString();
    }

    // ==================== INNER CLASSES ====================

    /**
     * Result class for Dijkstra's algorithm
     */
    public static class DijkstraResult {
        private final List<Integer> path;
        private final double distance;
        private final Map<Integer, Double> allDistances;
        private final Map<Integer, Integer> previous;

        public DijkstraResult(List<Integer> path, double distance,
                              Map<Integer, Double> allDistances, Map<Integer, Integer> previous) {
            this.path = path;
            this.distance = distance;
            this.allDistances = allDistances;
            this.previous = previous;
        }

        public List<Integer> getPath() { return path; }
        public double getDistance() { return distance; }
        public Map<Integer, Double> getAllDistances() { return allDistances; }
        public Map<Integer, Integer> getPrevious() { return previous; }

        @Override
        public String toString() {
            return "DijkstraResult{" +
                    "path=" + path +
                    ", distance=" + distance +
                    '}';
        }
    }
}
