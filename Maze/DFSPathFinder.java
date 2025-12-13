// ============================================================================
// FILE: DFSPathFinder.java
// Implementasi algoritma Depth-First Search untuk mencari path
// ============================================================================
package Maze;

import java.util.*;

public class DFSPathFinder implements PathFinder {
    // Arah pergerakan (atas, bawah, kiri, kanan)
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    
    @Override
    public SearchResult findPath(Maze maze) {
        maze.resetVisited();
        
        // Stack untuk DFS
        Stack<Node> stack = new Stack<>();
        List<Point2D> exploredNodes = new ArrayList<>();
        
        Point2D start = maze.getStart();
        Point2D end = maze.getEnd();
        
        // Mulai dari start position
        Node startNode = new Node(start.getX(), start.getY(), null);
        stack.push(startNode);
        maze.markVisited(start.getX(), start.getY());
        
        Node endNode = null;
        
        // DFS traversal
        while (!stack.isEmpty()) {
            Node current = stack.pop();
            exploredNodes.add(current.toPoint());
            
            // Cek jika sudah sampai end
            if (current.getX() == end.getX() && current.getY() == end.getY()) {
                endNode = current;
                break;
            }
            
            // Eksplorasi tetangga
            exploreNeighbors(current, stack, maze);
        }
        
        // Build final path jika ditemukan
        List<Point2D> finalPath = buildPath(endNode);
        
        return new SearchResult(exploredNodes, finalPath, endNode != null);
    }
    
    // Eksplorasi cell-cell tetangga
    private void exploreNeighbors(Node current, Stack<Node> stack, Maze maze) {
        for (int[] dir : DIRECTIONS) {
            int nextX = current.getX() + dir[0];
            int nextY = current.getY() + dir[1];
            
            if (canMoveTo(nextX, nextY, maze)) {
                maze.markVisited(nextX, nextY);
                stack.push(new Node(nextX, nextY, current));
            }
        }
    }
    
    // Cek apakah bisa pindah ke posisi tertentu
    private boolean canMoveTo(int x, int y, Maze maze) {
        return maze.isValidPosition(x, y) && 
               !maze.isVisited(x, y) && 
               maze.isWalkable(x, y);
    }
    
    // Build path dari end ke start dengan backtracking parent
    private List<Point2D> buildPath(Node endNode) {
        List<Point2D> path = new ArrayList<>();
        
        if (endNode == null) {
            return path;
        }
        
        Node current = endNode;
        while (current != null) {
            path.add(0, current.toPoint());
            current = current.getParent();
        }
        
        return path;
    }
}

