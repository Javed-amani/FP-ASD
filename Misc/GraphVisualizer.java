package Misc;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

// Node class
class Node {
    private int id;
    private int x, y;
    private static final int RADIUS = 25;

    public Node(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public int getId() { return id; }
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public int getRadius() { return RADIUS; }

    public boolean contains(int px, int py) {
        int dx = px - x;
        int dy = py - y;
        return dx * dx + dy * dy <= RADIUS * RADIUS;
    }
}

// Edge class
class Edge {
    private Node source;
    private Node target;
    private int weight;

    public Edge(Node source, Node target, int weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
    }

    public Node getSource() { return source; }
    public Node getTarget() { return target; }
    public int getWeight() { return weight; }
}

// Dijkstra Result class
class DijkstraResult {
    private java.util.List<Integer> path;
    private int totalDistance;
    private java.util.List<String> pathLabels;

    public DijkstraResult(java.util.List<Integer> path, int totalDistance, java.util.List<String> pathLabels) {
        this.path = path;
        this.totalDistance = totalDistance;
        this.pathLabels = pathLabels;
    }

    public java.util.List<Integer> getPath() { return path; }
    public int getTotalDistance() { return totalDistance; }
    public java.util.List<String> getPathLabels() { return pathLabels; }
}

// Graph class
class Graph {
    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;
    private int[][] adjacencyMatrix;
    private String[] label;

    public Graph(int[][] adjacencyMatrix, String[] l) {
        this.adjacencyMatrix = adjacencyMatrix;
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.label = l;
        initializeGraph();
    }

    private void initializeGraph() {
        int n = adjacencyMatrix.length;
        int centerX = 400;
        int centerY = 300;
        int radius = 200;

        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;
            int x = centerX + (int)(radius * Math.cos(angle));
            int y = centerY + (int)(radius * Math.sin(angle));
            nodes.add(new Node(i, x, y));
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (adjacencyMatrix[i][j] != 0) {
                    edges.add(new Edge(nodes.get(i), nodes.get(j), adjacencyMatrix[i][j]));
                }
            }
        }
    }

    public ArrayList<Node> getNodes() { return nodes; }
    public ArrayList<Edge> getEdges() { return edges; }
    public int[][] getAdjacencyMatrix() { return adjacencyMatrix; }
    public String[] getLabel() { return label; }

    // Dijkstra Algorithm Implementation
    public DijkstraResult dijkstra(int start, int end) {
        int n = adjacencyMatrix.length;
        int[] distance = new int[n];
        int[] previous = new int[n];
        boolean[] visited = new boolean[n];

        Arrays.fill(distance, Integer.MAX_VALUE);
        Arrays.fill(previous, -1);
        distance[start] = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[1] - b[1]);
        pq.offer(new int[]{start, 0});

        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int u = current[0];

            if (visited[u]) continue;
            visited[u] = true;

            for (int v = 0; v < n; v++) {
                if (adjacencyMatrix[u][v] != 0 && !visited[v]) {
                    int newDist = distance[u] + adjacencyMatrix[u][v];
                    if (newDist < distance[v]) {
                        distance[v] = newDist;
                        previous[v] = u;
                        pq.offer(new int[]{v, newDist});
                    }
                }
            }
        }

        java.util.List<Integer> path = new ArrayList<>();
        java.util.List<String> pathLabels = new ArrayList<>();
        
        if (distance[end] == Integer.MAX_VALUE) {
            return new DijkstraResult(path, -1, pathLabels);
        }

        int current = end;
        while (current != -1) {
            path.add(0, current);
            pathLabels.add(0, label[current]);
            current = previous[current];
        }

        return new DijkstraResult(path, distance[end], pathLabels);
    }

    public int getNodeIndexByLabel(String labelName) {
        for (int i = 0; i < label.length; i++) {
            if (label[i].equals(labelName)) {
                return i;
            }
        }
        return -1;
    }
}

// GraphPanel class
class GraphPanel extends JPanel {
    private Graph graph;
    private Node draggedNode = null;
    private DijkstraResult dijkstraResult = null;
    private javax.swing.Timer animationTimer;
    private float radarAngle = 0f;
    private java.util.List<RadarPulse> radarPulses = new ArrayList<>();
    
    // Inner class untuk efek pulse radar
    private class RadarPulse {
        float progress;
        int segmentIndex;
        
        RadarPulse(int segmentIndex) {
            this.segmentIndex = segmentIndex;
            this.progress = 0f;
        }
    }
    
    // Method untuk menggambar pesawat penumpang realistis (top view)
    private void drawAirplane(Graphics2D g2d, int x, int y, double angle, float alpha) {
        Graphics2D g2 = (Graphics2D) g2d.create();
        g2.rotate(angle, x, y);
        
        // Fuselage (badan utama) - lebih panjang dan ramping
        int[] xFuselage = {
            x, x - 3, x - 4, x - 4, x - 4, x - 3, x - 2, x - 2, x - 3, x
        };
        int[] yFuselage = {
            y - 30, y - 25, y - 15, y + 10, y + 18, y + 22, y + 24, y + 26, y + 28, y + 30
        };
        
        // Warna body pesawat (putih metalik)
        g2.setColor(new Color(240, 240, 245, (int)(255 * alpha)));
        g2.fillPolygon(xFuselage, yFuselage, 10);
        
        // Bayangan body (untuk dimensi)
        int[] xFuselageShadow = {x, x + 3, x + 4, x + 4, x + 4, x + 3, x + 2, x + 2, x + 3, x};
        g2.setColor(new Color(200, 200, 210, (int)(255 * alpha)));
        g2.fillPolygon(xFuselageShadow, yFuselage, 10);
        
        // Main wings (sayap utama) - lebih lebar dan tipis
        // Sayap kiri
        int[] xWingL = {x - 4, x - 35, x - 32, x - 4};
        int[] yWingL = {y - 5, y - 2, y + 3, y + 5};
        g2.setColor(new Color(220, 220, 230, (int)(255 * alpha)));
        g2.fillPolygon(xWingL, yWingL, 4);
        
        // Sayap kanan
        int[] xWingR = {x + 4, x + 35, x + 32, x + 4};
        int[] yWingR = {y - 5, y - 2, y + 3, y + 5};
        g2.fillPolygon(xWingR, yWingR, 4);
        
        // Wing tips (ujung sayap yang lebih gelap)
        g2.setColor(new Color(180, 180, 200, (int)(255 * alpha)));
        int[] xWingTipL = {x - 32, x - 35, x - 33};
        int[] yWingTipL = {y + 3, y - 2, y + 1};
        g2.fillPolygon(xWingTipL, yWingTipL, 3);
        
        int[] xWingTipR = {x + 32, x + 35, x + 33};
        int[] yWingTipR = {y + 3, y - 2, y + 1};
        g2.fillPolygon(xWingTipR, yWingTipR, 3);
        
        // Horizontal stabilizer (sayap belakang)
        int[] xStabL = {x - 3, x - 12, x - 10, x - 3};
        int[] yStabL = {y + 22, y + 23, y + 25, y + 24};
        g2.setColor(new Color(220, 220, 230, (int)(255 * alpha)));
        g2.fillPolygon(xStabL, yStabL, 4);
        
        int[] xStabR = {x + 3, x + 12, x + 10, x + 3};
        int[] yStabR = {y + 22, y + 23, y + 25, y + 24};
        g2.fillPolygon(xStabR, yStabR, 4);
        
        // Vertical stabilizer (ekor vertikal)
        int[] xVStab = {x - 1, x - 2, x - 1, x + 1, x + 2, x + 1};
        int[] yVStab = {y + 22, y + 24, y + 30, y + 30, y + 24, y + 22};
        g2.setColor(new Color(200, 200, 220, (int)(255 * alpha)));
        g2.fillPolygon(xVStab, yVStab, 6);
        
        // Cockpit/nose (bagian depan - lebih gelap)
        g2.setColor(new Color(80, 120, 180, (int)(220 * alpha)));
        g2.fillOval(x - 3, y - 32, 6, 8);
        
        // Windows (jendela cockpit)
        g2.setColor(new Color(50, 80, 120, (int)(200 * alpha)));
        g2.fillOval(x - 2, y - 30, 4, 4);
        
        // Engine nacelles (mesin di bawah sayap) - kiri
        g2.setColor(new Color(180, 180, 190, (int)(255 * alpha)));
        g2.fillRoundRect(x - 18, y, 4, 8, 2, 2);
        g2.setColor(new Color(60, 60, 70, (int)(200 * alpha)));
        g2.fillOval(x - 17, y, 2, 3);
        
        // Engine nacelles - kanan
        g2.setColor(new Color(180, 180, 190, (int)(255 * alpha)));
        g2.fillRoundRect(x + 14, y, 4, 8, 2, 2);
        g2.setColor(new Color(60, 60, 70, (int)(200 * alpha)));
        g2.fillOval(x + 15, y, 2, 3);
        
        // Outline detail (garis tipis untuk detail)
        g2.setColor(new Color(100, 100, 120, (int)(180 * alpha)));
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawPolygon(xFuselage, yFuselage, 10);
        g2.drawPolygon(xWingL, yWingL, 4);
        g2.drawPolygon(xWingR, yWingR, 4);
        
        // Detail lines (garis detail di body)
        g2.setStroke(new BasicStroke(0.8f));
        g2.drawLine(x - 4, y - 10, x - 4, y + 15);
        g2.drawLine(x + 4, y - 10, x + 4, y + 15);
        
        g2.dispose();
    }

    public GraphPanel(Graph graph) {
        this.graph = graph;
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.WHITE);

        // Timer untuk animasi radar
        animationTimer = new javax.swing.Timer(50, e -> {
            radarAngle += 3f;
            if (radarAngle >= 360) {
                radarAngle = 0;
                // Reset pulses untuk cycle berikutnya
                if (dijkstraResult != null && dijkstraResult.getPath().size() > 1) {
                    radarPulses.clear();
                    for (int i = 0; i < dijkstraResult.getPath().size() - 1; i++) {
                        radarPulses.add(new RadarPulse(i));
                    }
                }
            }
            
            // Update radar pulses
            for (RadarPulse pulse : radarPulses) {
                pulse.progress += 0.02f;
            }
            radarPulses.removeIf(pulse -> pulse.progress > 1.0f);
            
            repaint();
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                for (Node node : graph.getNodes()) {
                    if (node.contains(e.getX(), e.getY())) {
                        draggedNode = node;
                        break;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                draggedNode = null;
                setCursor(Cursor.getDefaultCursor());
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggedNode != null) {
                    draggedNode.setX(e.getX());
                    draggedNode.setY(e.getY());
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                boolean overNode = false;
                for (Node node : graph.getNodes()) {
                    if (node.contains(e.getX(), e.getY())) {
                        overNode = true;
                        break;
                    }
                }
                setCursor(overNode ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
            }
        });
    }

    public void setDijkstraResult(DijkstraResult result) {
        this.dijkstraResult = result;
        if (result != null && result.getPath().size() > 1) {
            radarAngle = 0;
            radarPulses.clear();
            for (int i = 0; i < result.getPath().size() - 1; i++) {
                radarPulses.add(new RadarPulse(i));
            }
            animationTimer.start();
        }
        repaint();
    }

    public void clearDijkstraResult() {
        this.dijkstraResult = null;
        radarPulses.clear();
        animationTimer.stop();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw all edges
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.setStroke(new BasicStroke(2));
        for (Edge edge : graph.getEdges()) {
            Node source = edge.getSource();
            Node target = edge.getTarget();
            g2d.drawLine(source.getX(), source.getY(), target.getX(), target.getY());
            drawArrow(g2d, source.getX(), source.getY(), target.getX(), target.getY(), Color.LIGHT_GRAY);
        }

        // Highlight shortest path dengan animasi radar
        if (dijkstraResult != null && dijkstraResult.getPath().size() > 1) {
            java.util.List<Integer> path = dijkstraResult.getPath();
            
            // Gambar garis dasar merah solid
            g2d.setColor(new Color(255, 60, 60));
            g2d.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int i = 0; i < path.size() - 1; i++) {
                Node source = graph.getNodes().get(path.get(i));
                Node target = graph.getNodes().get(path.get(i + 1));
                g2d.drawLine(source.getX(), source.getY(), target.getX(), target.getY());
                drawArrow(g2d, source.getX(), source.getY(), target.getX(), target.getY(), new Color(255, 60, 60));
            }
            
            // Gambar efek radar sweep (gelombang yang menyapu)
            for (int i = 0; i < path.size() - 1; i++) {
                Node source = graph.getNodes().get(path.get(i));
                Node target = graph.getNodes().get(path.get(i + 1));
                
                // Hitung angle dari segment ini
                double segmentAngle = Math.toDegrees(Math.atan2(
                    target.getY() - source.getY(),
                    target.getX() - source.getX()
                ));
                if (segmentAngle < 0) segmentAngle += 360;
                
                // Cek apakah radar sweep melewati segment ini
                float angleDiff = Math.abs((float)segmentAngle - radarAngle);
                if (angleDiff > 180) angleDiff = 360 - angleDiff;
                
                if (angleDiff < 30) { // Dalam range radar sweep
                    float intensity = 1.0f - (angleDiff / 30f);
                    
                    // Glow effect
                    for (int glow = 10; glow > 0; glow--) {
                        int alpha = (int)(80 * intensity * (glow / 10f));
                        g2d.setColor(new Color(0, 255, 255, alpha));
                        g2d.setStroke(new BasicStroke(5 + glow * 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g2d.drawLine(source.getX(), source.getY(), target.getX(), target.getY());
                    }
                    
                    // Core bright line
                    g2d.setColor(new Color(0, 255, 255, (int)(255 * intensity)));
                    g2d.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2d.drawLine(source.getX(), source.getY(), target.getX(), target.getY());
                }
            }
            
            // Gambar radar pulses (pesawat yang bergerak sepanjang path)
            for (RadarPulse pulse : radarPulses) {
                if (pulse.segmentIndex < path.size() - 1) {
                    Node source = graph.getNodes().get(path.get(pulse.segmentIndex));
                    Node target = graph.getNodes().get(path.get(pulse.segmentIndex + 1));
                    
                    // Interpolasi posisi pulse
                    int pulseX = (int)(source.getX() + (target.getX() - source.getX()) * pulse.progress);
                    int pulseY = (int)(source.getY() + (target.getY() - source.getY()) * pulse.progress);
                    
                    // Hitung angle untuk rotasi pesawat
                    double planeAngle = Math.atan2(target.getY() - source.getY(), target.getX() - source.getX());
                    planeAngle += Math.PI / 2; // Adjust agar pesawat menghadap arah pergerakan
                    
                    float alpha = 1.0f - pulse.progress;
                    
                    // Gambar shadow/glow di belakang pesawat
                    for (int r = 15; r > 0; r -= 2) {
                        int a = (int)(100 * alpha * (r / 15f));
                        g2d.setColor(new Color(255, 200, 0, a));
                        g2d.fillOval(pulseX - r, pulseY - r, r * 2, r * 2);
                    }
                    
                    // Gambar pesawat
                    drawAirplane(g2d, pulseX, pulseY, planeAngle, alpha);
                }
            }
        }

        // Draw edge weights
        g2d.setStroke(new BasicStroke(1));
        for (Edge edge : graph.getEdges()) {
            Node source = edge.getSource();
            Node target = edge.getTarget();
            int midX = (source.getX() + target.getX()) / 2;
            int midY = (source.getY() + target.getY()) / 2;
            g2d.setColor(Color.WHITE);
            g2d.fillRect(midX - 10, midY - 10, 20, 20);
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString(String.valueOf(edge.getWeight()), midX - 5, midY + 5);
        }

        // Draw nodes
        for (Node node : graph.getNodes()) {
            if (dijkstraResult != null && dijkstraResult.getPath().contains(node.getId())) {
                g2d.setColor(new Color(255, 100, 100));
            } else {
                g2d.setColor(new Color(100, 150, 255));
            }
            
            g2d.fillOval(node.getX() - node.getRadius(), node.getY() - node.getRadius(),
                    node.getRadius() * 2, node.getRadius() * 2);
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(node.getX() - node.getRadius(), node.getY() - node.getRadius(),
                    node.getRadius() * 2, node.getRadius() * 2);

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            String label = graph.getLabel()[node.getId()];
            FontMetrics fm = g2d.getFontMetrics();
            int labelX = node.getX() - fm.stringWidth(label) / 2;
            int labelY = node.getY() + fm.getAscent() / 2;
            g2d.drawString(label, labelX, labelY);
        }
    }

    private void drawArrow(Graphics2D g2d, int x1, int y1, int x2, int y2, Color color) {
        Color oldColor = g2d.getColor();
        g2d.setColor(color);
        
        double angle = Math.atan2(y2 - y1, x2 - x1);
        int arrowSize = 10;
        int arrowX = x2 - (int)(25 * Math.cos(angle));
        int arrowY = y2 - (int)(25 * Math.sin(angle));

        int[] xPoints = {
            arrowX,
            arrowX - (int)(arrowSize * Math.cos(angle - Math.PI / 6)),
            arrowX - (int)(arrowSize * Math.cos(angle + Math.PI / 6))
        };
        int[] yPoints = {
            arrowY,
            arrowY - (int)(arrowSize * Math.sin(angle - Math.PI / 6)),
            arrowY - (int)(arrowSize * Math.sin(angle + Math.PI / 6))
        };
        g2d.fillPolygon(xPoints, yPoints, 3);
        g2d.setColor(oldColor);
    }
}

// Main application class
public class GraphVisualizer extends JFrame {
    private Graph graph;
    private GraphPanel graphPanel;
    private JTextArea outputArea;
    private JComboBox<String> fromComboBox;
    private JComboBox<String> toComboBox;

    public GraphVisualizer(int[][] adjacencyMatrix, String[] labels) {
        setTitle("Graph Visualizer with Dijkstra Algorithm");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        graph = new Graph(adjacencyMatrix, labels);
        graphPanel = new GraphPanel(graph);
        add(graphPanel, BorderLayout.CENTER);

        // Control Panel with Dropdown
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // From Label and ComboBox
        JLabel fromLabel = new JLabel("From:");
        fromLabel.setFont(new Font("Arial", Font.BOLD, 14));
        fromComboBox = new JComboBox<>(labels);
        fromComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        fromComboBox.setPreferredSize(new Dimension(100, 30));
        
        // To Label and ComboBox
        JLabel toLabel = new JLabel("To:");
        toLabel.setFont(new Font("Arial", Font.BOLD, 14));
        toComboBox = new JComboBox<>(labels);
        toComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        toComboBox.setPreferredSize(new Dimension(100, 30));
        toComboBox.setSelectedIndex(8); // Default: BTM
        
        // Buttons
        JButton findPathButton = new JButton("Find Shortest Path");
        findPathButton.setFont(new Font("Arial", Font.BOLD, 14));
        findPathButton.setBackground(new Color(0, 120, 215));
        findPathButton.setForeground(Color.WHITE);
        findPathButton.setFocusPainted(false);
        findPathButton.setPreferredSize(new Dimension(180, 35));
        
        JButton clearButton = new JButton("Clear Path");
        clearButton.setFont(new Font("Arial", Font.BOLD, 14));
        clearButton.setBackground(new Color(200, 50, 50));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton.setPreferredSize(new Dimension(120, 35));
        
        // Action Listeners
        findPathButton.addActionListener(e -> {
            String from = (String) fromComboBox.getSelectedItem();
            String to = (String) toComboBox.getSelectedItem();
            runDijkstra(from, to);
        });
        
        clearButton.addActionListener(e -> clearPath());
        
        // Add components to control panel
        controlPanel.add(fromLabel);
        controlPanel.add(fromComboBox);
        controlPanel.add(new JLabel("  "));
        controlPanel.add(toLabel);
        controlPanel.add(toComboBox);
        controlPanel.add(new JLabel("  "));
        controlPanel.add(findPathButton);
        controlPanel.add(clearButton);
        
        add(controlPanel, BorderLayout.NORTH);

        // Output Panel
        JPanel outputPanel = new JPanel();
        outputPanel.setLayout(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder("Dijkstra Algorithm Output"));
        
        outputArea = new JTextArea(10, 50);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        outputPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(outputPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private void runDijkstra(String startLabel, String endLabel) {
        int startIndex = graph.getNodeIndexByLabel(startLabel);
        int endIndex = graph.getNodeIndexByLabel(endLabel);

        if (startIndex == -1 || endIndex == -1) {
            outputArea.setText("Error: Node " + startLabel + " atau " + endLabel + " tidak ditemukan!");
            return;
        }

        if (startIndex == endIndex) {
            outputArea.setText("Error: Start dan End node tidak boleh sama!");
            return;
        }

        DijkstraResult result = graph.dijkstra(startIndex, endIndex);

        StringBuilder output = new StringBuilder();
        output.append("=== DIJKSTRA ALGORITHM ===\n");
        output.append("Input: Adjacency Matrix (10x10)\n");
        output.append(String.format("Start: %s (Node %d)\n", startLabel, startIndex));
        output.append(String.format("End: %s (Node %d)\n\n", endLabel, endIndex));
        
        if (result.getTotalDistance() == -1) {
            output.append("Output: No path found!\n");
            output.append("Tidak ada jalur yang menghubungkan " + startLabel + " ke " + endLabel);
        } else {
            output.append("Output:\n");
            output.append("Node Sequence: ");
            
            for (int i = 0; i < result.getPathLabels().size(); i++) {
                output.append(result.getPathLabels().get(i));
                if (i < result.getPathLabels().size() - 1) {
                    output.append(" → ");
                }
            }
            output.append("\n\n");
            
            output.append("Node Indices: ");
            for (int i = 0; i < result.getPath().size(); i++) {
                output.append(result.getPath().get(i));
                if (i < result.getPath().size() - 1) {
                    output.append(" → ");
                }
            }
            output.append("\n\n");
            
            output.append("Detailed Path:\n");
            for (int i = 0; i < result.getPath().size(); i++) {
                int nodeIndex = result.getPath().get(i);
                String nodeLabel = result.getPathLabels().get(i);
                output.append(String.format("  %d. Node %d (%s)", i + 1, nodeIndex, nodeLabel));
                
                if (i < result.getPath().size() - 1) {
                    int nextIndex = result.getPath().get(i + 1);
                    int weight = graph.getAdjacencyMatrix()[nodeIndex][nextIndex];
                    output.append(String.format(" --[weight=%d]--> ", weight));
                } else {
                    output.append(" (Destination)");
                }
                output.append("\n");
            }
            
            output.append("\n");
            output.append("================================================\n");
            output.append(String.format("TOTAL JARAK TEMPUH: %d\n", result.getTotalDistance()));
            output.append("================================================\n");
        }

        outputArea.setText(output.toString());
        graphPanel.setDijkstraResult(result);
        
        System.out.println("\n" + output.toString());
    }

    private void clearPath() {
        graphPanel.clearDijkstraResult();
        outputArea.setText("");
    }

    public static void main(String[] args) {
        String[] labels = {"MKS", "SUB", "CGK", "BDG", "DPS", "YOG", "DHS", "MLG", "BTM", "PDG"};
        
        int[][] adjacencyMatrix = {
            {0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 8, 3, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 4, 0, 4, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 1, 0, 3},
            {0, 0, 0, 0, 0, 3, 2, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 4, 0},
            {0, 0, 0, 0, 0, 0, 0, 8, 3, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 4},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 7},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        };

        System.out.println("Adjacency Matrix (Input):");
        for (int i = 0; i < adjacencyMatrix.length; i++) {
            for (int j = 0; j < adjacencyMatrix[i].length; j++) {
                System.out.print(adjacencyMatrix[i][j] + " ");
            }
            System.out.println();
        }

        SwingUtilities.invokeLater(() -> {
            GraphVisualizer visualizer = new GraphVisualizer(adjacencyMatrix, labels);
            visualizer.setVisible(true);
        });
    }
}