// ============================================================================
// FILE: SearchResult.java
// Menyimpan hasil pencarian path (explored nodes dan final path)
// ============================================================================
package Maze;

import java.util.*;

public class SearchResult {
    private final List<Point2D> exploredNodes;
    private final List<Point2D> finalPath;
    private final boolean pathFound;
    
    public SearchResult(List<Point2D> exploredNodes, List<Point2D> finalPath, boolean pathFound) {
        this.exploredNodes = exploredNodes;
        this.finalPath = finalPath;
        this.pathFound = pathFound;
    }
    
    public List<Point2D> getExploredNodes() { return exploredNodes; }
    public List<Point2D> getFinalPath() { return finalPath; }
    public boolean isPathFound() { return pathFound; }
}

