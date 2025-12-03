import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.sound.sampled.*;

public class MazeGenerator extends JFrame {
    private static final int CELL_SIZE = 25;
    private static final int ROWS = 25;
    private static final int COLS = 25;
    private static final int DELAY = 15;
    
    private int[][] maze;
    private boolean[][] visited;
    private List<Point> path;
    private List<Point> explored;
    private MazePanel mazePanel;
    private Timer timer;
    private JLabel statusLabel;
    private JButton generateBtn, bfsBtn, dfsBtn, resetBtn;
    
    private static final int WALL = 1;
    private static final int PATH = 0;
    private static final int START = 2;
    private static final int END = 3;
    
    private Point start, end;
    private SoundEffect soundEffect;
    
    // Modern color palette
    private static final Color BG_COLOR = new Color(18, 18, 18);
    private static final Color PANEL_BG = new Color(28, 28, 30);
    private static final Color WALL_COLOR = new Color(45, 45, 48);
    private static final Color PATH_COLOR = new Color(28, 28, 30);
    private static final Color START_COLOR = new Color(52, 199, 89);
    private static final Color END_COLOR = new Color(255, 69, 58);
    private static final Color EXPLORED_COLOR = new Color(94, 92, 230, 120);
    private static final Color SOLUTION_COLOR = new Color(255, 214, 10);
    private static final Color BUTTON_BG = new Color(48, 48, 51);
    private static final Color BUTTON_HOVER = new Color(58, 58, 61);
    private static final Color TEXT_COLOR = new Color(242, 242, 247);
    
    public MazeGenerator() {
        setTitle("Maze Solver");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(BG_COLOR);
        
        maze = new int[ROWS][COLS];
        visited = new boolean[ROWS][COLS];
        path = new ArrayList<>();
        explored = new ArrayList<>();
        soundEffect = new SoundEffect();
        
        // Main panel with maze
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        mazePanel = new MazePanel();
        mainPanel.add(mazePanel, BorderLayout.CENTER);
        
        // Status label
        statusLabel = new JLabel("Ready", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(142, 142, 147));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        mainPanel.add(statusLabel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 12, 20));
        controlPanel.setBackground(BG_COLOR);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        generateBtn = createStyledButton("Generate");
        bfsBtn = createStyledButton("BFS");
        dfsBtn = createStyledButton("DFS");
        resetBtn = createStyledButton("Reset");
        
        generateBtn.addActionListener(e -> generateMaze());
        bfsBtn.addActionListener(e -> solveBFS());
        dfsBtn.addActionListener(e -> solveDFS());
        resetBtn.addActionListener(e -> reset());
        
        controlPanel.add(generateBtn);
        controlPanel.add(bfsBtn);
        controlPanel.add(dfsBtn);
        controlPanel.add(resetBtn);
        add(controlPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        
        generateMaze();
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SF Pro Display", Font.BOLD, 13));
        button.setForeground(TEXT_COLOR);
        button.setBackground(BUTTON_BG);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(100, 38));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BUTTON_HOVER);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_BG);
            }
        });
        
        return button;
    }
    
    private void generateMaze() {
        soundEffect.playGenerate();
        statusLabel.setText("Generating maze...");
        
        for (int i = 0; i < ROWS; i++) {
            Arrays.fill(maze[i], WALL);
        }
        
        Random rand = new Random();
        List<Wall> walls = new ArrayList<>();
        
        int startRow = 1 + 2 * rand.nextInt(ROWS / 2);
        int startCol = 1 + 2 * rand.nextInt(COLS / 2);
        maze[startRow][startCol] = PATH;
        
        addWalls(startRow, startCol, walls);
        
        while (!walls.isEmpty()) {
            Wall wall = walls.remove(rand.nextInt(walls.size()));
            
            int r = wall.r;
            int c = wall.c;
            
            int[] dirs = wall.getDirection();
            int nr = r + dirs[0];
            int nc = c + dirs[1];
            int pr = r - dirs[0];
            int pc = c - dirs[1];
            
            if (isValid(nr, nc) && isValid(pr, pc)) {
                if (maze[nr][nc] == WALL && maze[pr][pc] == PATH) {
                    maze[r][c] = PATH;
                    maze[nr][nc] = PATH;
                    addWalls(nr, nc, walls);
                } else if (maze[pr][pc] == WALL && maze[nr][nc] == PATH) {
                    maze[r][c] = PATH;
                    maze[pr][pc] = PATH;
                    addWalls(pr, pc, walls);
                }
            }
        }
        
        start = new Point(1, 1);
        end = new Point(ROWS - 2, COLS - 2);
        maze[start.x][start.y] = START;
        maze[end.x][end.y] = END;
        
        path.clear();
        explored.clear();
        resetVisited();
        mazePanel.repaint();
        statusLabel.setText("Ready");
    }
    
    private void addWalls(int r, int c, List<Wall> walls) {
        int[][] directions = {{-2, 0}, {2, 0}, {0, -2}, {0, 2}};
        for (int[] dir : directions) {
            int wallR = r + dir[0] / 2;
            int wallC = c + dir[1] / 2;
            if (isValid(wallR, wallC) && maze[wallR][wallC] == WALL) {
                walls.add(new Wall(wallR, wallC));
            }
        }
    }
    
    private boolean isValid(int r, int c) {
        return r > 0 && r < ROWS - 1 && c > 0 && c < COLS - 1;
    }
    
    private void solveBFS() {
        soundEffect.playStart();
        statusLabel.setText("Solving with BFS...");
        resetVisited();
        path.clear();
        explored.clear();
        
        Queue<Node> queue = new LinkedList<>();
        queue.offer(new Node(start.x, start.y, null));
        visited[start.x][start.y] = true;
        
        List<Node> searchOrder = new ArrayList<>();
        Node endNode = null;
        
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        
        while (!queue.isEmpty()) {
            Node current = queue.poll();
            searchOrder.add(current);
            
            if (current.x == end.x && current.y == end.y) {
                endNode = current;
                break;
            }
            
            for (int[] dir : directions) {
                int nx = current.x + dir[0];
                int ny = current.y + dir[1];
                
                if (isValid(nx, ny) && !visited[nx][ny] && 
                    (maze[nx][ny] == PATH || maze[nx][ny] == END)) {
                    visited[nx][ny] = true;
                    queue.offer(new Node(nx, ny, current));
                }
            }
        }
        
        animateSearch(searchOrder, endNode, "BFS");
    }
    
    private void solveDFS() {
        soundEffect.playStart();
        statusLabel.setText("Solving with DFS...");
        resetVisited();
        path.clear();
        explored.clear();
        
        Stack<Node> stack = new Stack<>();
        stack.push(new Node(start.x, start.y, null));
        visited[start.x][start.y] = true;
        
        List<Node> searchOrder = new ArrayList<>();
        Node endNode = null;
        
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        
        while (!stack.isEmpty()) {
            Node current = stack.pop();
            searchOrder.add(current);
            
            if (current.x == end.x && current.y == end.y) {
                endNode = current;
                break;
            }
            
            for (int[] dir : directions) {
                int nx = current.x + dir[0];
                int ny = current.y + dir[1];
                
                if (isValid(nx, ny) && !visited[nx][ny] && 
                    (maze[nx][ny] == PATH || maze[nx][ny] == END)) {
                    visited[nx][ny] = true;
                    stack.push(new Node(nx, ny, current));
                }
            }
        }
        
        animateSearch(searchOrder, endNode, "DFS");
    }
    
    private void animateSearch(List<Node> searchOrder, Node endNode, String algorithm) {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
        
        final int[] index = {0};
        
        timer = new Timer(DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (index[0] < searchOrder.size()) {
                    Node node = searchOrder.get(index[0]);
                    explored.add(new Point(node.x, node.y));
                    soundEffect.playStep();
                    index[0]++;
                    mazePanel.repaint();
                } else {
                    timer.stop();
                    if (endNode != null) {
                        buildPath(endNode);
                        soundEffect.playSuccess();
                        statusLabel.setText(algorithm + " - Solution found: " + path.size() + " steps");
                    } else {
                        soundEffect.playFail();
                        statusLabel.setText(algorithm + " - No solution found");
                    }
                    mazePanel.repaint();
                }
            }
        });
        timer.start();
    }
    
    private void buildPath(Node endNode) {
        Node current = endNode;
        while (current != null) {
            path.add(0, new Point(current.x, current.y));
            current = current.parent;
        }
    }
    
    private void resetVisited() {
        for (int i = 0; i < ROWS; i++) {
            Arrays.fill(visited[i], false);
        }
    }
    
    private void reset() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
        path.clear();
        explored.clear();
        resetVisited();
        mazePanel.repaint();
        statusLabel.setText("Ready");
    }
    
    class Wall {
        int r, c;
        
        Wall(int r, int c) {
            this.r = r;
            this.c = c;
        }
        
        int[] getDirection() {
            if (r % 2 == 0) return new int[]{1, 0};
            else return new int[]{0, 1};
        }
    }
    
    class Node {
        int x, y;
        Node parent;
        
        Node(int x, int y, Node parent) {
            this.x = x;
            this.y = y;
            this.parent = parent;
        }
    }
    
    class MazePanel extends JPanel {
        public MazePanel() {
            setPreferredSize(new Dimension(COLS * CELL_SIZE, ROWS * CELL_SIZE));
            setBackground(PANEL_BG);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw maze
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLS; j++) {
                    if (maze[i][j] == WALL) {
                        g2.setColor(WALL_COLOR);
                        g2.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    } else if (maze[i][j] == PATH) {
                        g2.setColor(PATH_COLOR);
                        g2.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    } else if (maze[i][j] == START) {
                        g2.setColor(PATH_COLOR);
                        g2.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                        g2.setColor(START_COLOR);
                        g2.fillOval(j * CELL_SIZE + 5, i * CELL_SIZE + 5, CELL_SIZE - 10, CELL_SIZE - 10);
                    } else if (maze[i][j] == END) {
                        g2.setColor(PATH_COLOR);
                        g2.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                        g2.setColor(END_COLOR);
                        g2.fillOval(j * CELL_SIZE + 5, i * CELL_SIZE + 5, CELL_SIZE - 10, CELL_SIZE - 10);
                    }
                }
            }
            
            // Draw explored cells
            g2.setColor(EXPLORED_COLOR);
            for (Point p : explored) {
                if (maze[p.x][p.y] != START && maze[p.x][p.y] != END) {
                    g2.fillRect(p.y * CELL_SIZE + 2, p.x * CELL_SIZE + 2, CELL_SIZE - 4, CELL_SIZE - 4);
                }
            }
            
            // Draw path
            g2.setColor(SOLUTION_COLOR);
            for (int i = 0; i < path.size(); i++) {
                Point p = path.get(i);
                if (maze[p.x][p.y] != START && maze[p.x][p.y] != END) {
                    g2.fillRect(p.y * CELL_SIZE + 4, p.x * CELL_SIZE + 4, CELL_SIZE - 8, CELL_SIZE - 8);
                }
                
                // Draw connecting lines
                if (i > 0) {
                    Point prev = path.get(i - 1);
                    g2.setStroke(new BasicStroke(3));
                    g2.drawLine(
                        prev.y * CELL_SIZE + CELL_SIZE / 2,
                        prev.x * CELL_SIZE + CELL_SIZE / 2,
                        p.y * CELL_SIZE + CELL_SIZE / 2,
                        p.x * CELL_SIZE + CELL_SIZE / 2
                    );
                }
            }
        }
    }
    
    class SoundEffect {
        public void playGenerate() {
            playTone(400, 100);
        }
        
        public void playStart() {
            playTone(600, 150);
        }
        
        public void playStep() {
            playTone(800, 20);
        }
        
        public void playSuccess() {
            new Thread(() -> {
                playTone(600, 100);
                try { Thread.sleep(80); } catch (InterruptedException e) {}
                playTone(800, 100);
                try { Thread.sleep(80); } catch (InterruptedException e) {}
                playTone(1000, 200);
            }).start();
        }
        
        public void playFail() {
            new Thread(() -> {
                playTone(400, 150);
                try { Thread.sleep(100); } catch (InterruptedException e) {}
                playTone(300, 300);
            }).start();
        }
        
        private void playTone(int hz, int msecs) {
            try {
                byte[] buf = new byte[1];
                AudioFormat af = new AudioFormat(8000f, 8, 1, true, false);
                SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
                sdl.open(af);
                sdl.start();
                
                for (int i = 0; i < msecs * 8; i++) {
                    double angle = i / (8000f / hz) * 2.0 * Math.PI;
                    buf[0] = (byte) (Math.sin(angle) * 80.0);
                    sdl.write(buf, 0, 1);
                }
                
                sdl.drain();
                sdl.stop();
                sdl.close();
            } catch (LineUnavailableException e) {
                // Silent fail
            }
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new MazeGenerator().setVisible(true);
        });
    }
}
