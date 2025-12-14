package Maze;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.Timer;


// MazePanel (High Resolution & Dense Maze)
class MazePanel extends JPanel {
    // TIPS: Gunakan angka GANJIL untuk ROWS dan COLS agar maze sempurna (tembok 1 blok)
    private final int ROWS = 35; // Harus Ganjil
    private final int COLS = 45; // Harus Ganjil
    private final int CELL_SIZE = 20; // Ukuran disesuaikan
    
    private Cell[][] grid;
    private Cell startNode, endNode;
    
    // Variabel animasi
    private Timer timer;
    private List<Cell> visualizationQueue;
    private List<Cell> finalPath;
    private int animIndex = 0;
    private boolean isAnimating = false;

    public MazePanel() {
        this.setBackground(Color.BLACK);
        // Set ukuran panel agar pas dengan grid
        this.setPreferredSize(new Dimension(COLS * CELL_SIZE, ROWS * CELL_SIZE));
        
        grid = new Cell[ROWS][COLS];
        // Inisialisasi awal
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                grid[r][c] = new Cell(r, c, 0); 
            }
        }
        generateMaze(); 
    }

    // ---------------------------------------------
    // 1. Generate Maze (Dense Prim's + Braiding)
    // ---------------------------------------------
    public void generateMaze() {
        if (isAnimating) return;

        // 1. Reset: Jadikan semua TEMBOK (0)
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                grid[r][c] = new Cell(r, c, 0);
            }
        }

        // 2. Prim's Algorithm (Hasilkan Maze Sempurna dulu)
        // Mulai dari koordinat ganjil (1,1)
        int startR = 1, startC = 1;
        grid[startR][startC].type = 1; 

        ArrayList<Cell> walls = new ArrayList<>();
        addWalls(startR, startC, walls);

        Random rand = new Random();

        while (!walls.isEmpty()) {
            // Ambil tembok acak
            int idx = rand.nextInt(walls.size());
            Cell wall = walls.remove(idx);
            
            // Cek tetangga (jarak 2)
            List<Cell> neighbors = getNeighbors(wall, 2);
            List<Cell> visitedNeighbors = new ArrayList<>();
            for(Cell n : neighbors) {
                if(n.type != 0) visitedNeighbors.add(n);
            }

            // Jika tembok ini memisahkan area visited dan unvisited
            if (visitedNeighbors.size() == 1) {
                wall.type = 1; // Jadi Jalan
                
                Cell n = visitedNeighbors.get(0);
                // Buka tembok di antaranya
                int midR = (wall.r + n.r) / 2;
                int midC = (wall.c + n.c) / 2;
                grid[midR][midC].type = 1;

                addWalls(wall.r, wall.c, walls);
            }
        }

        // 3. Set Start & End (Pojok ke Pojok)
        startNode = grid[1][1];
        endNode = grid[ROWS - 2][COLS - 2];
        startNode.type = 1; 
        endNode.type = 1;

        // 4. Create Multiple Paths (Braiding)
        // Sekarang kita lubangi maze yang sudah jadi agar ada banyak jalan
        
        // Buat jalan pintas Horizontal & Vertikal acak (looping)
        // Ini mencegah maze hanya punya 1 solusi
        int loops = (ROWS * COLS) / 10; // Jumlah loop
        for(int i=0; i < loops; i++) {
            int r = rand.nextInt(ROWS-2) + 1;
            int c = rand.nextInt(COLS-2) + 1;
            if(grid[r][c].type == 0) {
                // Pastikan tidak membuka border luar
                if(r > 0 && r < ROWS-1 && c > 0 && c < COLS-1) {
                   // Cek agar tidak membuat area terbuka terlalu besar (opsional)
                   grid[r][c].type = 1; 
                }
            }
        }

        // Paksa Jalur Alternatif Panjang (Variabel Path 1 & 2)
        // Kita hancurkan tembok sepanjang garis tertentu
        carvePath(ROWS/4);       // Jalur Atas
        carvePath(ROWS*3/4);     // Jalur Bawah

        // 5. Assign Weights (Grass, Mud, Water)
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (grid[r][c].type != 0) {
                    int chance = rand.nextInt(100);
                    if (chance < 65) grid[r][c].type = 1;      // Grass
                    else if (chance < 85) grid[r][c].type = 2; // Mud
                    else grid[r][c].type = 3;                  // Water
                }
            }
        }
        
        // Pastikan Start/End bersih
        startNode.type = 1;
        endNode.type = 1;

        resetPath();
        repaint();
    }

    // Fungsi bikin jalan tembus horizontal
    private void carvePath(int row) {
        // Cari baris terdekat yang ganjil (biar align sama grid)
        if(row % 2 == 0) row++; 
        Random rand = new Random();
        
        // Buat jalan dari kiri ke kanan dengan sedikit acak naik turun
        for(int c = 2; c < COLS-2; c++) {
            grid[row][c].type = 1;
            // Kadang-kadang buka atas/bawahnya biar lebar dikit atau zigzag
            if(rand.nextInt(10) < 2) {
               if(row + 1 < ROWS-1) grid[row+1][c].type = 1;
            }
        }
    }

    private void addWalls(int r, int c, ArrayList<Cell> walls) {
        int[] dr = {-2, 2, 0, 0};
        int[] dc = {0, 0, -2, 2};
        for (int i = 0; i < 4; i++) {
            int nr = r + dr[i];
            int nc = c + dc[i];
            // Pastikan tembok valid dan belum jadi jalan
            if (isValid(nr, nc) && grid[nr][nc].type == 0) {
                walls.add(grid[nr][nc]);
            }
        }
    }

    // ---------------------------------------------
    // Visualisasi & Pathfinding Control (DENGAN FIX GARIS KUNING)
    // ---------------------------------------------
    public void startSolving(String algo) {
        if (isAnimating) return;
        resetPath();
        
        PathAlgorithms.Result result = null;

        switch (algo) {
            case "BFS": result = PathAlgorithms.solveBFS(grid, startNode, endNode); break;
            case "DFS": result = PathAlgorithms.solveDFS(grid, startNode, endNode); break;
            case "Dijkstra": result = PathAlgorithms.solveDijkstra(grid, startNode, endNode); break;
            case "A*": result = PathAlgorithms.solveAStar(grid, startNode, endNode); break;
        }

        if (result != null) {
            this.visualizationQueue = result.visitedOrder;
            // SIMPAN HASIL, JANGAN DIGAMBAR DULU
            List<Cell> calculatedPath = result.path; 
            
            animIndex = 0;
            isAnimating = true;
            
            // Timer sedikit dipercepat (delay 3ms) karena grid makin banyak
            timer = new Timer(3, new ActionListener() { 
                @Override
                public void actionPerformed(ActionEvent e) {
                    // LOOP untuk speed up (gambar 3 sel per frame)
                    for(int k=0; k<3; k++) {
                        if (animIndex < visualizationQueue.size()) {
                            Cell c = visualizationQueue.get(animIndex);
                            c.visited = true; 
                            animIndex++;
                        } else {
                            break;
                        }
                    }
                    
                    if(animIndex % 10 == 0) SoundUtils.playStepSound();
                    repaint();

                    if (animIndex >= visualizationQueue.size()) {
                        ((Timer)e.getSource()).stop();
                        isAnimating = false;
                        
                        // BARU MUNCULKAN GARIS KUNING
                        finalPath = calculatedPath; 
                        
                        repaint(); 
                        SoundUtils.playFinishSound();
                        
                        double cost = PathAlgorithms.calculatePathCost(finalPath);
                        JOptionPane.showMessageDialog(null, 
                            algo + " Finished!\nPath Nodes: " + finalPath.size() + 
                            "\nTotal Weight Cost: " + cost);
                    }
                }
            });
            timer.start();
        } else {
            JOptionPane.showMessageDialog(this, "No Path Found!");
        }
    }

    public void compareAlgorithms() {
        if(isAnimating) return;
        
        StringBuilder sb = new StringBuilder("Algorithm Comparison:\n\n");
        String[] algos = {"BFS", "DFS", "Dijkstra", "A*"};
        
        for(String algo : algos) {
            for(int r=0; r<ROWS; r++) for(int c=0; c<COLS; c++) grid[r][c].reset();
            
            long startTime = System.nanoTime();
            PathAlgorithms.Result res = null;
            
            if(algo.equals("BFS")) res = PathAlgorithms.solveBFS(grid, startNode, endNode);
            else if(algo.equals("DFS")) res = PathAlgorithms.solveDFS(grid, startNode, endNode);
            else if(algo.equals("Dijkstra")) res = PathAlgorithms.solveDijkstra(grid, startNode, endNode);
            else if(algo.equals("A*")) res = PathAlgorithms.solveAStar(grid, startNode, endNode);
            
            long endTime = System.nanoTime();
            double duration = (endTime - startTime) / 1000000.0;
            
            if(res != null && !res.path.isEmpty()) {
                double totalCost = PathAlgorithms.calculatePathCost(res.path);
                sb.append(String.format("%-10s | Nodes: %4d | Cost: %6.1f | Time: %4.2f ms\n", 
                    algo, res.visitedOrder.size(), totalCost, duration));
            } else {
                sb.append(algo + ": No Path Found\n");
            }
        }
        
        resetPath(); 
        JOptionPane.showMessageDialog(this, new JTextArea(sb.toString()));
    }

    public void resetPath() {
        if(timer != null) timer.stop();
        isAnimating = false;
        finalPath = null;
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                grid[r][c].reset();
            }
        }
        repaint();
    }

    private List<Cell> getNeighbors(Cell c, int dist) {
        List<Cell> list = new ArrayList<>();
        int[] dr = {-dist, dist, 0, 0};
        int[] dc = {0, 0, -dist, dist};
        for (int i = 0; i < 4; i++) {
            int nr = c.r + dr[i];
            int nc = c.c + dc[i];
            if (isValid(nr, nc)) list.add(grid[nr][nc]);
        }
        return list;
    }

    private boolean isValid(int r, int c) {
        return r >= 0 && r < ROWS && c >= 0 && c < COLS;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Anti-aliasing text
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Cell cell = grid[r][c];
                int x = c * CELL_SIZE;
                int y = r * CELL_SIZE;

                if (cell.type == 0) {
                    g.setColor(new Color(30, 30, 30)); // Tembok (Dark)
                    g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                } else {
                    // Warna Terrain
                    if (cell.type == 1) g.setColor(new Color(34, 139, 34)); // Grass
                    else if (cell.type == 2) g.setColor(new Color(139, 69, 19)); // Mud
                    else if (cell.type == 3) g.setColor(new Color(30, 144, 255)); // Water

                    // Jika visited (Animasi)
                    if (cell.visited) {
                         // Campur warna terrain dengan highlight
                        g.setColor(blend(g.getColor(), Color.CYAN, 0.3f));
                    }
                    g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                    
                    // Garis grid sangat tipis
                    g.setColor(new Color(0, 0, 0, 50)); 
                    g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
                }
            }
        }

        // Gambar Path Kuning (Solusi)
        if (finalPath != null && finalPath.size() > 1) {
            g2d.setColor(Color.YELLOW);
            g2d.setStroke(new BasicStroke(3)); // Garis tebal
            
            // Gambar garis menyambung antar titik tengah cell
            int[] xPoints = new int[finalPath.size()];
            int[] yPoints = new int[finalPath.size()];
            
            for(int i=0; i<finalPath.size(); i++) {
                Cell c = finalPath.get(i);
                xPoints[i] = c.c * CELL_SIZE + CELL_SIZE/2;
                yPoints[i] = c.r * CELL_SIZE + CELL_SIZE/2;
            }
            g2d.drawPolyline(xPoints, yPoints, finalPath.size());
        }

        // Marker Start/End
        drawMarker(g, startNode, Color.GREEN, "S");
        drawMarker(g, endNode, Color.RED, "E");
    }
    
    // Helper untuk mencampur warna (visualisasi visited lebih cantik)
    private Color blend(Color c1, Color c2, float ratio) {
        int r = (int) (c1.getRed() * (1 - ratio) + c2.getRed() * ratio);
        int g = (int) (c1.getGreen() * (1 - ratio) + c2.getGreen() * ratio);
        int b = (int) (c1.getBlue() * (1 - ratio) + c2.getBlue() * ratio);
        return new Color(r, g, b);
    }

    private void drawMarker(Graphics g, Cell node, Color color, String text) {
        if (node != null) {
            g.setColor(color);
            g.fillOval(node.c * CELL_SIZE + 2, node.r * CELL_SIZE + 2, CELL_SIZE-4, CELL_SIZE-4);
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 10));
            FontMetrics fm = g.getFontMetrics();
            int w = fm.stringWidth(text);
            int h = fm.getAscent();
            g.drawString(text, node.c * CELL_SIZE + (CELL_SIZE-w)/2, node.r * CELL_SIZE + (CELL_SIZE+h)/2 - 2);
        }
    }
}