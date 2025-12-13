// ============================================================================
// FILE: PathFinder.java
// Interface untuk algoritma pathfinding
// ============================================================================
package Maze.Path;

import java.util.List;

import Maze.Mazes.Maze;
import Maze.Support.SearchResult;

public interface PathFinder {
    SearchResult findPath(Maze maze);
}

