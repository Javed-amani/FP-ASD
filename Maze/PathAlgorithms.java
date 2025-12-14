package Maze;

import java.util.*;
import java.util.List;

// PathAlgorithms (Logika Pencarian)

class PathAlgorithms {

    // Objek untuk menyimpan hasil algoritma
    public static class Result {
        List<Cell> visitedOrder = new ArrayList<>();
        List<Cell> path = new ArrayList<>();
    }

    // Helper: Reconstruct path dari parent pointer
    private static List<Cell> reconstructPath(Cell end) {
        List<Cell> path = new ArrayList<>();
        Cell curr = end;
        while (curr != null) {
            path.add(0, curr);
            curr = curr.parent;
        }
        return path;
    }
    
    // 5. Total Cost Calculator
    public static double calculatePathCost(List<Cell> path) {
        double total = 0;
        for(Cell c : path) {
            total += c.getWeight();
        }
        return total;
    }

    // Helper: Ambil tetangga valid (bukan tembok)
    private static List<Cell> getNeighbors(Cell[][] grid, Cell c) {
        List<Cell> list = new ArrayList<>();
        int[] dr = {-1, 1, 0, 0}; // Atas, Bawah, Kiri, Kanan
        int[] dc = {0, 0, -1, 1};
        for (int i = 0; i < 4; i++) {
            int nr = c.r + dr[i];
            int nc = c.c + dc[i];
            if (nr >= 0 && nr < grid.length && nc >= 0 && nc < grid[0].length) {
                if (grid[nr][nc].type != 0) { // Bukan Wall
                    list.add(grid[nr][nc]);
                }
            }
        }
        return list;
    }

    // (A) BFS
    public static Result solveBFS(Cell[][] grid, Cell start, Cell end) {
        Result res = new Result();
        Queue<Cell> queue = new LinkedList<>();
        Set<Cell> visited = new HashSet<>();
        
        queue.add(start);
        visited.add(start);
        
        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            res.visitedOrder.add(current);
            
            if (current == end) {
                res.path = reconstructPath(end);
                return res;
            }
            
            for (Cell neighbor : getNeighbors(grid, current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    neighbor.parent = current;
                    queue.add(neighbor);
                }
            }
        }
        return res; // Tidak ketemu
    }

    // (B) DFS
    public static Result solveDFS(Cell[][] grid, Cell start, Cell end) {
        Result res = new Result();
        Stack<Cell> stack = new Stack<>();
        Set<Cell> visited = new HashSet<>();
        
        stack.push(start);
        visited.add(start);
        
        while (!stack.isEmpty()) {
            Cell current = stack.pop();
            res.visitedOrder.add(current);
            
            if (current == end) {
                res.path = reconstructPath(end);
                return res;
            }
            
            for (Cell neighbor : getNeighbors(grid, current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    neighbor.parent = current;
                    stack.push(neighbor);
                }
            }
        }
        return res;
    }

    // (C) Dijkstra
    public static Result solveDijkstra(Cell[][] grid, Cell start, Cell end) {
        Result res = new Result();
        PriorityQueue<Cell> pq = new PriorityQueue<>();
        
        start.gCost = 0;
        start.fCost = 0;
        pq.add(start);
        
        while (!pq.isEmpty()) {
            Cell current = pq.poll();
            res.visitedOrder.add(current);
            
            if (current == end) {
                res.path = reconstructPath(end);
                return res;
            }
            
            for (Cell neighbor : getNeighbors(grid, current)) {
                double newCost = current.gCost + neighbor.getWeight();
                if (newCost < neighbor.gCost) {
                    neighbor.gCost = newCost;
                    neighbor.fCost = newCost; // Dijkstra f = g
                    neighbor.parent = current;
                    
                    // Remove & Add untuk update prioritas
                    pq.remove(neighbor);
                    pq.add(neighbor);
                }
            }
        }
        return res;
    }

    // (D) A* (A Star)
    public static Result solveAStar(Cell[][] grid, Cell start, Cell end) {
        Result res = new Result();
        PriorityQueue<Cell> pq = new PriorityQueue<>();
        
        start.gCost = 0;
        start.hCost = Math.abs(start.r - end.r) + Math.abs(start.c - end.c); // Manhattan
        start.fCost = start.gCost + start.hCost;
        pq.add(start);
        
        while (!pq.isEmpty()) {
            Cell current = pq.poll();
            res.visitedOrder.add(current);
            
            if (current == end) {
                res.path = reconstructPath(end);
                return res;
            }
            
            for (Cell neighbor : getNeighbors(grid, current)) {
                double newGCost = current.gCost + neighbor.getWeight();
                
                if (newGCost < neighbor.gCost) {
                    neighbor.gCost = newGCost;
                    neighbor.hCost = Math.abs(neighbor.r - end.r) + Math.abs(neighbor.c - end.c);
                    neighbor.fCost = neighbor.gCost + neighbor.hCost;
                    neighbor.parent = current;
                    
                    pq.remove(neighbor);
                    pq.add(neighbor);
                }
            }
        }
        return res;
    }
}

