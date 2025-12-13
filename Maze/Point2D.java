// ============================================================================
// FILE: Point2D.java
// Representasi koordinat 2D dalam maze
// ============================================================================
package Maze;

public class Point2D {
    private final int x;
    private final int y;
    
    public Point2D(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point2D)) return false;
        Point2D point = (Point2D) o;
        return x == point.x && y == point.y;
    }
    
    @Override
    public int hashCode() {
        return 31 * x + y;
    }
}

