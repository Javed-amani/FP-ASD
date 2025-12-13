// ============================================================================
// FILE: Node.java
// Representasi node dalam algoritma pencarian (BFS/DFS)
// Menyimpan posisi dan parent untuk backtracking path
// ============================================================================
package Maze.Support;

public class Node {
    private final int x;
    private final int y;
    private final Node parent;
    
    public Node(int x, int y, Node parent) {
        this.x = x;
        this.y = y;
        this.parent = parent;
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
    public Node getParent() { return parent; }
    
    public Point2D toPoint() {
        return new Point2D(x, y);
    }
}

