package Maze;


// Cell (Representasi Node/Grid)
class Cell implements Comparable<Cell> {
    int r, c;           // Koordinat Baris dan Kolom
    int type;           // 0=Wall, 1=Grass, 2=Mud, 3=Water
    boolean visited;    // Status kunjungan visualisasi
    Cell parent;        // Untuk backtracking path
    double gCost;       // Biaya dari start (Dijkstra/A*)
    double hCost;       // Heuristik ke end (A*)
    double fCost;       // Total cost (g + h)

    // Konstanta Berat
    public static final int COST_GRASS = 1;
    public static final int COST_MUD = 5;
    public static final int COST_WATER = 10;

    public Cell(int r, int c, int type) {
        this.r = r;
        this.c = c;
        this.type = type;
        this.gCost = Double.MAX_VALUE;
        this.fCost = Double.MAX_VALUE;
    }

    // Reset data pathfinding tanpa mengubah tipe terrain
    public void reset() {
        this.visited = false;
        this.parent = null;
        this.gCost = Double.MAX_VALUE;
        this.hCost = 0;
        this.fCost = Double.MAX_VALUE;
    }

    // Mendapatkan berat (weight) berdasarkan tipe
    public int getWeight() {
        if (type == 1) return COST_GRASS;
        if (type == 2) return COST_MUD;
        if (type == 3) return COST_WATER;
        return 999; // Wall
    }

    // Diperlukan untuk PriorityQueue (Dijkstra/A*)
    @Override
    public int compareTo(Cell other) {
        return Double.compare(this.fCost, other.fCost);
    }
}

