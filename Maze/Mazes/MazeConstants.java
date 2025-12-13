// ============================================================================
// FILE: MazeConstants.java
// Menyimpan semua konstanta yang digunakan dalam aplikasi
// ============================================================================
package Maze.Mazes;

import java.awt.*;

public class MazeConstants {
    // Dimensi maze dan cell
    public static final int CELL_SIZE = 25;
    public static final int ROWS = 25;
    public static final int COLS = 25;
    public static final int ANIMATION_DELAY = 15;
    
    // Tipe cell dalam maze
    public static final int WALL = 1;
    public static final int PATH = 0;
    public static final int START = 2;
    public static final int END = 3;
    
    // Palet warna modern (dark mode)
    public static final Color BG_COLOR = new Color(18, 18, 18);
    public static final Color PANEL_BG = new Color(28, 28, 30);
    public static final Color WALL_COLOR = new Color(45, 45, 48);
    public static final Color PATH_COLOR = new Color(28, 28, 30);
    public static final Color START_COLOR = new Color(52, 199, 89);
    public static final Color END_COLOR = new Color(255, 69, 58);
    public static final Color EXPLORED_COLOR = new Color(94, 92, 230, 120);
    public static final Color SOLUTION_COLOR = new Color(255, 214, 10);
    public static final Color BUTTON_BG = new Color(48, 48, 51);
    public static final Color BUTTON_HOVER = new Color(58, 58, 61);
    public static final Color TEXT_COLOR = new Color(242, 242, 247);
    public static final Color STATUS_TEXT_COLOR = new Color(142, 142, 147);
}

