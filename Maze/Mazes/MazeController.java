// ============================================================================
// FILE: MazeController.java
// Controller yang mengatur logic aplikasi dan koordinasi antar komponen
// ============================================================================
package Maze.Mazes;

import javax.swing.Timer;

import Maze.Path.BFSPathFinder;
import Maze.Path.DFSPathFinder;
import Maze.Support.Point2D;
import Maze.Support.SearchResult;
import Maze.Support.SoundEffect;

import java.util.ArrayList;
import java.util.List;

public class MazeController {
    private final Maze maze;
    private final MazeGenerator generator;
    private final MazePanel panel;
    private final SoundEffect soundEffect;
    private final StatusListener statusListener;
    
    private Timer animationTimer;
    private List<Point2D> currentExploredNodes;
    private List<Point2D> currentSolutionPath;
    
    // Interface untuk update status UI
    public interface StatusListener {
        void onStatusUpdate(String status);
    }
    
    public MazeController(Maze maze, MazePanel panel, StatusListener statusListener) {
        this.maze = maze;
        this.panel = panel;
        this.statusListener = statusListener;
        this.generator = new MazeGenerator(maze);
        this.soundEffect = new SoundEffect();
        this.currentExploredNodes = new ArrayList<>();
        this.currentSolutionPath = new ArrayList<>();
    }
    
    // Generate maze baru
    public void generateNewMaze() {
        soundEffect.playGenerate();
        updateStatus("Generating maze...");
        
        generator.generate();
        resetVisualization();
        panel.repaint();
        
        updateStatus("Ready");
    }
    
    // Solve maze menggunakan BFS
    public void solveBFS() {
        if (animationTimer != null && animationTimer.isRunning()) {
            return;
        }
        
        soundEffect.playStart();
        updateStatus("Solving with BFS...");
        
        BFSPathFinder pathFinder = new BFSPathFinder();
        SearchResult result = pathFinder.findPath(maze);
        
        animateSearch(result, "BFS");
    }
    
    // Solve maze menggunakan DFS
    public void solveDFS() {
        if (animationTimer != null && animationTimer.isRunning()) {
            return;
        }
        
        soundEffect.playStart();
        updateStatus("Solving with DFS...");
        
        DFSPathFinder pathFinder = new DFSPathFinder();
        SearchResult result = pathFinder.findPath(maze);
        
        animateSearch(result, "DFS");
    }
    
    // Reset visualization (clear path dan explored nodes)
    public void resetVisualization() {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
        
        currentExploredNodes.clear();
        currentSolutionPath.clear();
        panel.setExploredNodes(new ArrayList<>());
        panel.setSolutionPath(new ArrayList<>());
        maze.resetVisited();
        panel.repaint();
        
        updateStatus("Ready");
    }
    
    // Animate proses pencarian path
    private void animateSearch(SearchResult result, String algorithmName) {
        resetVisualization();
        
        List<Point2D> exploredNodes = result.getExploredNodes();
        final int[] index = {0};
        
        // Timer untuk animasi step-by-step
        animationTimer = new Timer(MazeConstants.ANIMATION_DELAY, e -> {
            if (index[0] < exploredNodes.size()) {
                // Tambahkan explored node satu per satu
                currentExploredNodes.add(exploredNodes.get(index[0]));
                panel.setExploredNodes(new ArrayList<>(currentExploredNodes));
                soundEffect.playStep();
                index[0]++;
                panel.repaint();
            } else {
                // Animasi selesai
                animationTimer.stop();
                showSearchResult(result, algorithmName);
            }
        });
        
        animationTimer.start();
    }
    
    // Tampilkan hasil pencarian
    private void showSearchResult(SearchResult result, String algorithmName) {
        if (result.isPathFound()) {
            currentSolutionPath = result.getFinalPath();
            panel.setSolutionPath(currentSolutionPath);
            soundEffect.playSuccess();
            updateStatus(algorithmName + " - Solution found: " + currentSolutionPath.size() + " steps");
        } else {
            soundEffect.playFail();
            updateStatus(algorithmName + " - No solution found");
        }
        panel.repaint();
    }
    
    // Update status message
    private void updateStatus(String message) {
        if (statusListener != null) {
            statusListener.onStatusUpdate(message);
        }
    }
}

