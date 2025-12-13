// ============================================================================
// FILE: MazeGenerator.java
// Bertanggung jawab untuk generate maze menggunakan Prim's algorithm
// ============================================================================
package Maze;

import java.util.*;

public class MazeGenerator {
    private final Maze maze;
    private final Random random;
    
    // Arah pergerakan (atas, bawah, kiri, kanan) dengan step 2 cells
    private static final int[][] DIRECTIONS = {{-2, 0}, {2, 0}, {0, -2}, {0, 2}};
    
    public MazeGenerator(Maze maze) {
        this.maze = maze;
        this.random = new Random();
    }
    
    // Generate maze baru menggunakan Prim's algorithm
    public void generate() {
        // Inisialisasi semua cell sebagai WALL
        initializeWalls();
        
        // Pilih starting point random (harus di posisi ganjil)
        Point2D startPoint = selectRandomOddPosition();
        maze.setCell(startPoint.getX(), startPoint.getY(), MazeConstants.PATH);
        
        // Kumpulkan dinding-dinding yang bersebelahan dengan path
        List<Wall> walls = new ArrayList<>();
        addAdjacentWalls(startPoint.getX(), startPoint.getY(), walls);
        
        // Prim's algorithm: ambil random wall dan coba buat path
        processWalls(walls);
        
        // Set posisi start dan end
        setStartAndEndPoints();
    }
    
    // Set semua cell menjadi WALL
    private void initializeWalls() {
        for (int i = 0; i < maze.getRows(); i++) {
            Arrays.fill(maze.getGrid()[i], MazeConstants.WALL);
        }
    }
    
    // Pilih posisi random dengan koordinat ganjil (untuk memastikan maze terstruktur)
    private Point2D selectRandomOddPosition() {
        int row = 1 + 2 * random.nextInt(maze.getRows() / 2);
        int col = 1 + 2 * random.nextInt(maze.getCols() / 2);
        return new Point2D(row, col);
    }
    
    // Tambahkan dinding-dinding yang bersebelahan dengan cell saat ini
    private void addAdjacentWalls(int row, int col, List<Wall> walls) {
        for (int[] dir : DIRECTIONS) {
            int wallRow = row + dir[0] / 2;
            int wallCol = col + dir[1] / 2;
            
            if (maze.isValidPosition(wallRow, wallCol) && 
                maze.getCell(wallRow, wallCol) == MazeConstants.WALL) {
                walls.add(new Wall(wallRow, wallCol));
            }
        }
    }
    
    // Proses semua dinding untuk membuat path
    private void processWalls(List<Wall> walls) {
        while (!walls.isEmpty()) {
            // Ambil random wall
            Wall wall = walls.remove(random.nextInt(walls.size()));
            
            // Coba buat path melalui wall ini
            tryCreatePath(wall, walls);
        }
    }
    
    // Coba untuk membuat path melalui wall
    private void tryCreatePath(Wall wall, List<Wall> walls) {
        int row = wall.getRow();
        int col = wall.getCol();
        int[] direction = wall.getDirection();
        
        // Hitung posisi cell di kedua sisi wall
        int nextRow = row + direction[0];
        int nextCol = col + direction[1];
        int prevRow = row - direction[0];
        int prevCol = col - direction[1];
        
        if (!maze.isValidPosition(nextRow, nextCol) || !maze.isValidPosition(prevRow, prevCol)) {
            return;
        }
        
        // Jika salah satu sisi PATH dan sisi lain WALL, buat connection
        if (maze.getCell(nextRow, nextCol) == MazeConstants.WALL && 
            maze.getCell(prevRow, prevCol) == MazeConstants.PATH) {
            createConnection(row, col, nextRow, nextCol, walls);
        } else if (maze.getCell(prevRow, prevCol) == MazeConstants.WALL && 
                   maze.getCell(nextRow, nextCol) == MazeConstants.PATH) {
            createConnection(row, col, prevRow, prevCol, walls);
        }
    }
    
    // Buat connection antara dua cell melalui wall
    private void createConnection(int wallRow, int wallCol, int targetRow, int targetCol, List<Wall> walls) {
        maze.setCell(wallRow, wallCol, MazeConstants.PATH);
        maze.setCell(targetRow, targetCol, MazeConstants.PATH);
        addAdjacentWalls(targetRow, targetCol, walls);
    }
    
    // Set posisi start (top-left) dan end (bottom-right)
    private void setStartAndEndPoints() {
        Point2D start = new Point2D(1, 1);
        Point2D end = new Point2D(maze.getRows() - 2, maze.getCols() - 2);
        
        maze.setStart(start);
        maze.setEnd(end);
        maze.setCell(start.getX(), start.getY(), MazeConstants.START);
        maze.setCell(end.getX(), end.getY(), MazeConstants.END);
    }
}
