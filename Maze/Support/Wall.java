// ============================================================================
// FILE: Wall.java
// Representasi dinding dalam algoritma Prim's untuk maze generation
// ============================================================================
package Maze.Support;

public class Wall {
    private final int row;
    private final int col;
    
    public Wall(int row, int col) {
        this.row = row;
        this.col = col;
    }
    
    public int getRow() { return row; }
    public int getCol() { return col; }
    
    // Menentukan arah dinding (horizontal atau vertikal)
    // berdasarkan posisi row (genap = vertikal, ganjil = horizontal)
    public int[] getDirection() {
        if (row % 2 == 0) {
            return new int[]{1, 0}; // Vertikal
        } else {
            return new int[]{0, 1}; // Horizontal
        }
    }
}

