// ============================================================================
// FILE: MazePanel.java
// JPanel untuk render visualisasi maze
// ============================================================================
package Maze.Mazes;

import javax.swing.*;

import Maze.Support.Point2D;

import java.awt.*;
import java.util.List;


public class MazePanel extends JPanel {
    private final Maze maze;
    private List<Point2D> exploredNodes;
    private List<Point2D> solutionPath;
    
    public MazePanel(Maze maze) {
        this.maze = maze;
        setPreferredSize(new Dimension(
            MazeConstants.COLS * MazeConstants.CELL_SIZE,
            MazeConstants.ROWS * MazeConstants.CELL_SIZE
        ));
        setBackground(MazeConstants.PANEL_BG);
    }
    
    // Update nodes yang sudah diexplore
    public void setExploredNodes(List<Point2D> exploredNodes) {
        this.exploredNodes = exploredNodes;
    }
    
    // Update solution path
    public void setSolutionPath(List<Point2D> solutionPath) {
        this.solutionPath = solutionPath;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Render maze grid
        drawMazeGrid(g2);
        
        // Render explored nodes
        if (exploredNodes != null) {
            drawExploredNodes(g2);
        }
        
        // Render solution path
        if (solutionPath != null) {
            drawSolutionPath(g2);
        }
    }
    
    // Draw maze grid (walls, paths, start, end)
    private void drawMazeGrid(Graphics2D g2) {
        int[][] grid = maze.getGrid();
        
        for (int i = 0; i < maze.getRows(); i++) {
            for (int j = 0; j < maze.getCols(); j++) {
                int cellType = grid[i][j];
                drawCell(g2, i, j, cellType);
            }
        }
    }
    
    // Draw single cell berdasarkan tipenya
    private void drawCell(Graphics2D g2, int row, int col, int cellType) {
        int x = col * MazeConstants.CELL_SIZE;
        int y = row * MazeConstants.CELL_SIZE;
        int size = MazeConstants.CELL_SIZE;
        
        switch (cellType) {
            case MazeConstants.WALL:
                g2.setColor(MazeConstants.WALL_COLOR);
                g2.fillRect(x, y, size, size);
                break;
                
            case MazeConstants.PATH:
                g2.setColor(MazeConstants.PATH_COLOR);
                g2.fillRect(x, y, size, size);
                break;
                
            case MazeConstants.START:
                g2.setColor(MazeConstants.PATH_COLOR);
                g2.fillRect(x, y, size, size);
                g2.setColor(MazeConstants.START_COLOR);
                g2.fillOval(x + 5, y + 5, size - 10, size - 10);
                break;
                
            case MazeConstants.END:
                g2.setColor(MazeConstants.PATH_COLOR);
                g2.fillRect(x, y, size, size);
                g2.setColor(MazeConstants.END_COLOR);
                g2.fillOval(x + 5, y + 5, size - 10, size - 10);
                break;
        }
    }
    
    // Draw explored nodes (dengan transparansi)
    private void drawExploredNodes(Graphics2D g2) {
        g2.setColor(MazeConstants.EXPLORED_COLOR);
        
        for (Point2D point : exploredNodes) {
            int cellType = maze.getCell(point.getX(), point.getY());
            
            // Jangan draw over start/end
            if (cellType != MazeConstants.START && cellType != MazeConstants.END) {
                int x = point.getY() * MazeConstants.CELL_SIZE + 2;
                int y = point.getX() * MazeConstants.CELL_SIZE + 2;
                int size = MazeConstants.CELL_SIZE - 4;
                g2.fillRect(x, y, size, size);
            }
        }
    }
    
    // Draw solution path dengan connecting lines
    private void drawSolutionPath(Graphics2D g2) {
        g2.setColor(MazeConstants.SOLUTION_COLOR);
        
        // Draw path cells
        for (int i = 0; i < solutionPath.size(); i++) {
            Point2D point = solutionPath.get(i);
            int cellType = maze.getCell(point.getX(), point.getY());
            
            // Jangan draw over start/end
            if (cellType != MazeConstants.START && cellType != MazeConstants.END) {
                int x = point.getY() * MazeConstants.CELL_SIZE + 4;
                int y = point.getX() * MazeConstants.CELL_SIZE + 4;
                int size = MazeConstants.CELL_SIZE - 8;
                g2.fillRect(x, y, size, size);
            }
            
            // Draw connecting lines
            if (i > 0) {
                Point2D prev = solutionPath.get(i - 1);
                g2.setStroke(new BasicStroke(3));
                
                int x1 = prev.getY() * MazeConstants.CELL_SIZE + MazeConstants.CELL_SIZE / 2;
                int y1 = prev.getX() * MazeConstants.CELL_SIZE + MazeConstants.CELL_SIZE / 2;
                int x2 = point.getY() * MazeConstants.CELL_SIZE + MazeConstants.CELL_SIZE / 2;
                int y2 = point.getX() * MazeConstants.CELL_SIZE + MazeConstants.CELL_SIZE / 2;
                
                g2.drawLine(x1, y1, x2, y2);
            }
        }
    }
}

