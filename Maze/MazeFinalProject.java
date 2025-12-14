package Maze;

import javax.swing.*;
import java.awt.*;

// MazeFinalProject (Main Class & UI)
public class MazeFinalProject extends JFrame {

    private MazePanel mazePanel;

    public MazeFinalProject() {
        // Setup dasar JFrame
        setTitle("Final Project: Advanced Maze Solver & Generator");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Inisialisasi Panel Maze (Pusat Logika)
        mazePanel = new MazePanel();
        add(mazePanel, BorderLayout.CENTER);

        // Setup Panel Kontrol (Bagian Bawah)
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        controlPanel.setBackground(new Color(30, 30, 30)); // Dark Theme Background

        // Tombol-tombol kontrol
        JButton btnGenerate = createStyledButton("New Maze");
        JButton btnBFS = createStyledButton("Solve BFS");
        JButton btnDFS = createStyledButton("Solve DFS");
        JButton btnDijkstra = createStyledButton("Solve Dijkstra");
        JButton btnAStar = createStyledButton("Solve A*");
        JButton btnCompare = createStyledButton("Compare All");
        JButton btnReset = createStyledButton("Reset Path");

        // Menambahkan Action Listener (Event Handling)
        btnGenerate.addActionListener(e -> mazePanel.generateMaze());
        btnBFS.addActionListener(e -> mazePanel.startSolving("BFS"));
        btnDFS.addActionListener(e -> mazePanel.startSolving("DFS"));
        btnDijkstra.addActionListener(e -> mazePanel.startSolving("Dijkstra"));
        btnAStar.addActionListener(e -> mazePanel.startSolving("A*"));
        btnCompare.addActionListener(e -> mazePanel.compareAlgorithms());
        btnReset.addActionListener(e -> mazePanel.resetPath());

        // Menambahkan tombol ke panel
        controlPanel.add(btnGenerate);
        controlPanel.add(btnReset);
        controlPanel.add(new JSeparator(SwingConstants.VERTICAL));
        controlPanel.add(btnBFS);
        controlPanel.add(btnDFS);
        controlPanel.add(btnDijkstra);
        controlPanel.add(btnAStar);
        controlPanel.add(new JSeparator(SwingConstants.VERTICAL));
        controlPanel.add(btnCompare);

        add(controlPanel, BorderLayout.SOUTH);
    }

    // Helper untuk membuat tombol dengan tema gelap
    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(70, 70, 70));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }

    public static void main(String[] args) {
        // Menjalankan aplikasi di Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new MazeFinalProject().setVisible(true);
        });
    }
}
