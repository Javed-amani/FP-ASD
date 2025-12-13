// ============================================================================
// FILE: MazeSolverApp.java
// Main application class - setup UI dan koordinasi
// ============================================================================

package Maze.Mazes;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class MazeSolverApp extends JFrame {
    private final Maze maze;
    private final MazePanel mazePanel;
    private final MazeController controller;
    private JLabel statusLabel;
    
    public MazeSolverApp() {
        // Inisialisasi model dan components
        maze = new Maze(MazeConstants.ROWS, MazeConstants.COLS);
        mazePanel = new MazePanel(maze);
        controller = new MazeController(maze, mazePanel, this::updateStatusLabel);
        
        // Setup window
        setupWindow();
        
        // Setup UI components
        setupUI();
        
        // Generate maze awal
        controller.generateNewMaze();
    }
    
    // Setup window properties
    private void setupWindow() {
        setTitle("Maze Solver - BFS & DFS Visualization");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(MazeConstants.BG_COLOR);
        setResizable(false);
    }
    
    // Setup semua UI components
    private void setupUI() {
        // Main panel dengan maze
        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);
        
        // Control panel dengan buttons
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    // Buat main panel yang berisi maze dan status label
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MazeConstants.BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Tambahkan maze panel
        panel.add(mazePanel, BorderLayout.CENTER);
        
        // Tambahkan status label
        statusLabel = createStatusLabel();
        panel.add(statusLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // Buat status label untuk menampilkan info
    private JLabel createStatusLabel() {
        JLabel label = new JLabel("Ready", SwingConstants.CENTER);
        label.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        label.setForeground(MazeConstants.STATUS_TEXT_COLOR);
        label.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        return label;
    }
    
    // Buat control panel dengan buttons
    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 12, 20));
        panel.setBackground(MazeConstants.BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        // Buat dan tambahkan buttons
        JButton generateBtn = createStyledButton("Generate");
        JButton bfsBtn = createStyledButton("BFS");
        JButton dfsBtn = createStyledButton("DFS");
        JButton resetBtn = createStyledButton("Reset");
        
        // Attach event listeners
        generateBtn.addActionListener(e -> controller.generateNewMaze());
        bfsBtn.addActionListener(e -> controller.solveBFS());
        dfsBtn.addActionListener(e -> controller.solveDFS());
        resetBtn.addActionListener(e -> controller.resetVisualization());
        
        panel.add(generateBtn);
        panel.add(bfsBtn);
        panel.add(dfsBtn);
        panel.add(resetBtn);
        
        return panel;
    }
    
    // Buat styled button dengan hover effect
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SF Pro Display", Font.BOLD, 13));
        button.setForeground(MazeConstants.TEXT_COLOR);
        button.setBackground(MazeConstants.BUTTON_BG);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(100, 38));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Tambahkan hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(MazeConstants.BUTTON_HOVER);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(MazeConstants.BUTTON_BG);
            }
        });
        
        return button;
    }
    
    // Update status label text
    private void updateStatusLabel(String status) {
        statusLabel.setText(status);
    }
    
    // Main method - entry point aplikasi
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Launch aplikasi di Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new MazeSolverApp().setVisible(true);
        });
    }
}
    