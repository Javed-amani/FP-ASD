// ============================================================================
// FILE: Maze.java
// Model data maze - menyimpan struktur maze dan state-nya
// ============================================================================
package Maze;

import java.util.*;


public class Maze {
    private final int[][] grid;
    private final boolean[][] visited;
    private Point2D start;
    private Point2D end;
    private final int rows;
    private final int cols;
    
    public Maze(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new int[rows][cols];
        this.visited = new boolean[rows][cols];
    }
    
    // Getter methods
    public int[][] getGrid() { return grid; }
    public boolean[][] getVisited() { return visited; }
    public Point2D getStart() { return start; }
    public Point2D getEnd() { return end; }
    public int getRows() { return rows; }
    public int getCols() { return cols; }
    
    // Setter methods
    public void setStart(Point2D start) { this.start = start; }
    public void setEnd(Point2D end) { this.end = end; }
    
    // Mengecek apakah koordinat valid (tidak keluar batas dan bukan di tepi)
    public boolean isValidPosition(int row, int col) {
        return row > 0 && row < rows - 1 && col > 0 && col < cols - 1;
    }
    
    // Mengecek apakah cell bisa dilalui (PATH atau END)
    public boolean isWalkable(int row, int col) {
        return grid[row][col] == MazeConstants.PATH || grid[row][col] == MazeConstants.END;
    }
    
    // Reset status visited untuk algoritma pencarian baru
    public void resetVisited() {
        for (int i = 0; i < rows; i++) {
            Arrays.fill(visited[i], false);
        }
    }
    
    // Set cell value
    public void setCell(int row, int col, int value) {
        grid[row][col] = value;
    }
    
    // Get cell value
    public int getCell(int row, int col) {
        return grid[row][col];
    }
    
    // Mark cell sebagai visited
    public void markVisited(int row, int col) {
        visited[row][col] = true;
    }
    
    // Check jika cell sudah visited
    public boolean isVisited(int row, int col) {
        return visited[row][col];
    }
}

