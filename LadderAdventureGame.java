import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.sound.sampled.*;

// Class Player untuk menyimpan data pemain
class Player {
    String name;
    int position;
    Color color;
    List<Integer> steps;

    public Player(String name, Color color) {
        this.name = name;
        this.position = 0;
        this.color = color;
        this.steps = new ArrayList<>();
    }

    public void addStep(int step) {
        steps.add(step);
    }
}

// Class untuk Tangga
class Ladder {
    int bottom;
    int top;

    public Ladder(int bottom, int top) {
        this.bottom = bottom;
        this.top = top;
    }
}

// Class untuk Sound Effects
class SoundPlayer {
    public static void playSound(String soundType) {
        new Thread(() -> {
            try {
                // Generate simple beep sounds based on type
                AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(format);
                line.start();

                byte[] buffer = new byte[4410];

                switch (soundType) {
                    case "forward":
                        generateTone(buffer, 440, 0.1); // A note
                        break;
                    case "backward":
                        generateTone(buffer, 330, 0.1); // E note
                        break;
                    case "ladder":
                        generateTone(buffer, 523, 0.2); // C note
                        generateTone(buffer, 659, 0.2); // E note
                        break;
                    case "win":
                        generateTone(buffer, 523, 0.15); // C
                        generateTone(buffer, 659, 0.15); // E
                        generateTone(buffer, 784, 0.3); // G
                        break;
                }

                line.write(buffer, 0, buffer.length);
                line.drain();
                line.close();
            } catch (Exception e) {
                // Silent fail for sound
            }
        }).start();
    }

    private static void generateTone(byte[] buffer, double frequency, double duration) {
        int samples = (int) (44100 * duration);
        for (int i = 0; i < samples && i < buffer.length / 2; i++) {
            double angle = 2.0 * Math.PI * i * frequency / 44100;
            short value = (short) (Math.sin(angle) * 32767 * 0.3);
            buffer[i * 2] = (byte) (value & 0xFF);
            buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
        }
    }
}

// Panel untuk setup game
class SetupPanel extends JPanel {
    private JTextField playerCountField;
    private JButton startButton;
    private LadderAdventureGame game;

    public SetupPanel(LadderAdventureGame game) {
        this.game = game;
        setLayout(new GridBagLayout());
        setBackground(new Color(15, 23, 42));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        // Title
        JLabel titleLabel = new JLabel("LADDER ADVENTURE");
        titleLabel.setFont(new Font("Arial Black", Font.BOLD, 48));
        titleLabel.setForeground(new Color(96, 165, 250));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("Epic Climbing Challenge");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 20));
        subtitleLabel.setForeground(new Color(148, 163, 184));
        gbc.gridy = 1;
        add(subtitleLabel, gbc);

        JLabel instructionLabel = new JLabel("Enter Number of Players (2-6)");
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 22));
        instructionLabel.setForeground(Color.WHITE);
        gbc.gridy = 2;
        gbc.insets = new Insets(30, 15, 10, 15);
        add(instructionLabel, gbc);

        playerCountField = new JTextField(10);
        playerCountField.setFont(new Font("Arial", Font.PLAIN, 28));
        playerCountField.setHorizontalAlignment(JTextField.CENTER);
        playerCountField.setBackground(new Color(30, 41, 59));
        playerCountField.setForeground(Color.WHITE);
        playerCountField.setCaretColor(Color.WHITE);
        playerCountField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(96, 165, 250), 3),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 15, 30, 15);
        add(playerCountField, gbc);

        startButton = new JButton("START ADVENTURE");
        startButton.setFont(new Font("Arial Black", Font.BOLD, 22));
        startButton.setBackground(new Color(34, 197, 94));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setBorderPainted(false);
        startButton.setPreferredSize(new Dimension(300, 70));
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                startButton.setBackground(new Color(22, 163, 74));
            }

            public void mouseExited(MouseEvent e) {
                startButton.setBackground(new Color(34, 197, 94));
            }
        });
        startButton.addActionListener(e -> startGame());
        gbc.gridy = 4;
        add(startButton, gbc);
    }

    private void startGame() {
        try {
            int playerCount = Integer.parseInt(playerCountField.getText());
            if (playerCount < 2 || playerCount > 6) {
                JOptionPane.showMessageDialog(this, "Please enter a number between 2-6!",
                        "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }
            game.setupPlayers(playerCount);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number!",
                    "Invalid Input", JOptionPane.WARNING_MESSAGE);
        }
    }
}

// Panel untuk input nama pemain
class PlayerInputPanel extends JPanel {
    private List<JTextField> nameFields;
    private JButton confirmButton;
    private LadderAdventureGame game;

    public PlayerInputPanel(LadderAdventureGame game, int playerCount) {
        this.game = game;
        setLayout(new GridBagLayout());
        setBackground(new Color(15, 23, 42));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("ENTER PLAYER NAMES");
        titleLabel.setFont(new Font("Arial Black", Font.BOLD, 36));
        titleLabel.setForeground(new Color(96, 165, 250));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        nameFields = new ArrayList<>();
        Color[] colors = {
                new Color(239, 68, 68), // Red
                new Color(59, 130, 246), // Blue
                new Color(34, 197, 94), // Green
                new Color(249, 115, 22), // Orange
                new Color(168, 85, 247), // Purple
                new Color(6, 182, 212) // Cyan
        };

        gbc.gridwidth = 1;
        for (int i = 0; i < playerCount; i++) {
            JPanel playerPanel = new JPanel();
            playerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
            playerPanel.setBackground(new Color(30, 41, 59));
            playerPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(colors[i], 3),
                    BorderFactory.createEmptyBorder(12, 15, 12, 15)));

            JLabel label = new JLabel("Player " + (i + 1));
            label.setFont(new Font("Arial Black", Font.BOLD, 20));
            label.setForeground(colors[i]);
            label.setPreferredSize(new Dimension(120, 35));

            JTextField nameField = new JTextField(15);
            nameField.setFont(new Font("Arial", Font.PLAIN, 20));
            nameField.setBackground(new Color(15, 23, 42));
            nameField.setForeground(Color.WHITE);
            nameField.setCaretColor(Color.WHITE);
            nameField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            nameFields.add(nameField);

            playerPanel.add(label);
            playerPanel.add(nameField);

            gbc.gridx = 0;
            gbc.gridy = i + 1;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            add(playerPanel, gbc);
        }

        confirmButton = new JButton("START CLIMBING!");
        confirmButton.setFont(new Font("Arial Black", Font.BOLD, 22));
        confirmButton.setBackground(new Color(34, 197, 94));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFocusPainted(false);
        confirmButton.setBorderPainted(false);
        confirmButton.setPreferredSize(new Dimension(350, 70));
        confirmButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                confirmButton.setBackground(new Color(22, 163, 74));
            }

            public void mouseExited(MouseEvent e) {
                confirmButton.setBackground(new Color(34, 197, 94));
            }
        });
        confirmButton.addActionListener(e -> confirmPlayers());
        gbc.gridx = 0;
        gbc.gridy = playerCount + 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 10, 10, 10);
        add(confirmButton, gbc);
    }

    private void confirmPlayers() {
        List<String> names = new ArrayList<>();
        for (JTextField field : nameFields) {
            String name = field.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All names must be filled!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            names.add(name);
        }
        game.startGameWithPlayers(names);
    }
}

// Panel untuk board game
class GamePanel extends JPanel {
    LadderAdventureGame game;
    private static final int NODE_SIZE = 70;
    private static final int BOARD_COLS = 8;
    private static final int BOARD_ROWS = 8;

    public GamePanel(LadderAdventureGame game) {
        this.game = game;
        setPreferredSize(new Dimension(BOARD_COLS * NODE_SIZE + 400, BOARD_ROWS * NODE_SIZE + 250));
        setBackground(new Color(15, 23, 42));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Draw background gradient
        GradientPaint gradient = new GradientPaint(0, 0, new Color(15, 23, 42),
                0, getHeight(), new Color(30, 41, 59));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        drawBoard(g2d);
        drawLadders(g2d);
        drawPlayers(g2d);
        drawSidebar(g2d);
        drawDice(g2d);
    }

    private void drawBoard(Graphics2D g) {
        int nodeNum = 0;
        for (int row = BOARD_ROWS - 1; row >= 0; row--) {
            boolean leftToRight = (BOARD_ROWS - 1 - row) % 2 == 0;

            for (int col = 0; col < BOARD_COLS; col++) {
                int actualCol = leftToRight ? col : (BOARD_COLS - 1 - col);
                int x = 30 + actualCol * NODE_SIZE;
                int y = 30 + row * NODE_SIZE;

                // Shadow effect
                g.setColor(new Color(0, 0, 0, 50));
                g.fillRoundRect(x + 3, y + 3, NODE_SIZE - 10, NODE_SIZE - 10, 15, 15);

                // Node color with gradient
                GradientPaint nodeGradient = new GradientPaint(x, y, new Color(248, 250, 252),
                        x, y + NODE_SIZE, new Color(226, 232, 240));
                g.setPaint(nodeGradient);
                g.fillRoundRect(x, y, NODE_SIZE - 10, NODE_SIZE - 10, 15, 15);

                // Border
                g.setColor(new Color(148, 163, 184));
                g.setStroke(new BasicStroke(2));
                g.drawRoundRect(x, y, NODE_SIZE - 10, NODE_SIZE - 10, 15, 15);

                // Node number
                g.setColor(new Color(51, 65, 85));
                g.setFont(new Font("Arial Black", Font.BOLD, 16));
                String numStr = String.valueOf(nodeNum);
                FontMetrics fm = g.getFontMetrics();
                int textX = x + (NODE_SIZE - 10 - fm.stringWidth(numStr)) / 2;
                int textY = y + (NODE_SIZE - 10 + fm.getAscent()) / 2 - 2;

                // Text shadow
                g.setColor(new Color(0, 0, 0, 50));
                g.drawString(numStr, textX + 1, textY + 1);
                g.setColor(new Color(51, 65, 85));
                g.drawString(numStr, textX, textY);

                nodeNum++;
            }
        }
    }

    private void drawLadders(Graphics2D g) {
        // Draw Ladders with realistic appearance
        g.setStroke(new BasicStroke(6));
        for (Ladder ladder : game.ladders) {
            Point bottom = getNodePosition(ladder.bottom);
            Point top = getNodePosition(ladder.top);

            // Calculate perpendicular offset for parallel rails
            double dx = top.x - bottom.x;
            double dy = top.y - bottom.y;
            double length = Math.sqrt(dx * dx + dy * dy);
            double offsetX = -dy / length * 6;
            double offsetY = dx / length * 6;

            // Shadow for both rails
            g.setColor(new Color(0, 0, 0, 50));
            g.drawLine((int) (bottom.x - offsetX + 2), (int) (bottom.y - offsetY + 2),
                    (int) (top.x - offsetX + 2), (int) (top.y - offsetY + 2));
            g.drawLine((int) (bottom.x + offsetX + 2), (int) (bottom.y + offsetY + 2),
                    (int) (top.x + offsetX + 2), (int) (top.y + offsetY + 2));

            // Left rail (brown)
            g.setColor(new Color(180, 83, 9));
            g.drawLine((int) (bottom.x - offsetX), (int) (bottom.y - offsetY),
                    (int) (top.x - offsetX), (int) (top.y - offsetY));

            // Right rail (brown)
            g.drawLine((int) (bottom.x + offsetX), (int) (bottom.y + offsetY),
                    (int) (top.x + offsetX), (int) (top.y + offsetY));

            // Draw rungs (horizontal steps)
            g.setStroke(new BasicStroke(5));
            int steps = Math.max(5, (int) (length / 40));
            for (int i = 0; i <= steps; i++) {
                double t = (double) i / steps;
                int x1 = (int) (bottom.x + (top.x - bottom.x) * t - offsetX);
                int y1 = (int) (bottom.y + (top.y - bottom.y) * t - offsetY);
                int x2 = (int) (bottom.x + (top.x - bottom.x) * t + offsetX);
                int y2 = (int) (bottom.y + (top.y - bottom.y) * t + offsetY);

                // Rung shadow
                g.setColor(new Color(0, 0, 0, 50));
                g.drawLine(x1 + 1, y1 + 1, x2 + 1, y2 + 1);

                // Rung
                g.setColor(new Color(146, 64, 14));
                g.drawLine(x1, y1, x2, y2);
            }

            g.setStroke(new BasicStroke(6));
        }
    }

    private void drawPlayers(Graphics2D g) {
        for (int i = 0; i < game.players.size(); i++) {
            Player player = game.players.get(i);
            Point pos = getNodePosition(player.position);

            int offset = i * 18;
            int x = pos.x + offset - 25;
            int y = pos.y + offset - 25;

            // Shadow
            g.setColor(new Color(0, 0, 0, 100));
            g.fillOval(x + 2, y + 2, 28, 28);

            // Player circle with gradient
            GradientPaint playerGradient = new GradientPaint(x, y, player.color.brighter(),
                    x, y + 28, player.color.darker());
            g.setPaint(playerGradient);
            g.fillOval(x, y, 28, 28);

            // Border
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(3));
            g.drawOval(x, y, 28, 28);

            // Current player indicator
            if (player == game.getCurrentPlayer()) {
                g.setColor(new Color(255, 215, 0));
                g.setStroke(new BasicStroke(3));
                for (int r = 0; r < 3; r++) {
                    g.drawOval(x - 3 - r * 2, y - 3 - r * 2, 34 + r * 4, 34 + r * 4);
                }
            }

            // Player initial
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial Black", Font.BOLD, 12));
            String initial = player.name.substring(0, Math.min(2, player.name.length())).toUpperCase();
            FontMetrics fm = g.getFontMetrics();
            int textX = x + (28 - fm.stringWidth(initial)) / 2;
            int textY = y + (28 + fm.getAscent()) / 2 - 2;
            g.drawString(initial, textX, textY);
        }
    }

    private void drawSidebar(Graphics2D g) {
        int sidebarX = BOARD_COLS * NODE_SIZE + 50;
        int sidebarY = 30;

        // Title
        g.setFont(new Font("Arial Black", Font.BOLD, 24));
        g.setColor(new Color(96, 165, 250));
        g.drawString("GAME STATUS", sidebarX, sidebarY);

        sidebarY += 50;

        // Current turn
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.setColor(Color.WHITE);
        g.drawString("Current Turn:", sidebarX, sidebarY);

        sidebarY += 30;
        Player current = game.getCurrentPlayer();
        if (current != null) {
            g.setColor(current.color);
            g.setFont(new Font("Arial Black", Font.BOLD, 22));
            g.drawString(current.name, sidebarX, sidebarY);
        }

        sidebarY += 50;

        // Leaderboard
        g.setFont(new Font("Arial Black", Font.BOLD, 20));
        g.setColor(new Color(96, 165, 250));
        g.drawString("LEADERBOARD", sidebarX, sidebarY);

        sidebarY += 35;

        List<Player> sortedPlayers = new ArrayList<>(game.players);
        sortedPlayers.sort((p1, p2) -> Integer.compare(p2.position, p1.position));

        for (int i = 0; i < sortedPlayers.size(); i++) {
            Player p = sortedPlayers.get(i);

            // Rank background
            g.setColor(new Color(30, 41, 59, 180));
            g.fillRoundRect(sidebarX - 10, sidebarY - 20, 250, 35, 10, 10);

            // Rank number
            g.setColor(new Color(148, 163, 184));
            g.setFont(new Font("Arial Black", Font.BOLD, 16));
            g.drawString("#" + (i + 1), sidebarX, sidebarY);

            // Player name and position
            g.setColor(p.color);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString(p.name, sidebarX + 35, sidebarY);

            g.setColor(Color.WHITE);
            g.drawString("Node " + p.position, sidebarX + 150, sidebarY);

            sidebarY += 40;
        }
    }

    private void drawDice(Graphics2D g) {
        if (game.lastDiceRoll > 0) {
            int diceX = BOARD_COLS * NODE_SIZE + 80;
            int diceY = BOARD_ROWS * NODE_SIZE - 150;
            int diceSize = 100;

            // Dice shadow
            g.setColor(new Color(0, 0, 0, 100));
            g.fillRoundRect(diceX + 5, diceY + 5, diceSize, diceSize, 20, 20);

            // Dice background with gradient based on color
            GradientPaint diceGradient;
            if (game.lastDiceGreen) {
                diceGradient = new GradientPaint(diceX, diceY, new Color(134, 239, 172),
                        diceX, diceY + diceSize, new Color(34, 197, 94));
            } else {
                diceGradient = new GradientPaint(diceX, diceY, new Color(252, 165, 165),
                        diceX, diceY + diceSize, new Color(239, 68, 68));
            }
            g.setPaint(diceGradient);
            g.fillRoundRect(diceX, diceY, diceSize, diceSize, 20, 20);

            // Dice border
            g.setColor(new Color(100, 100, 100));
            g.setStroke(new BasicStroke(3));
            g.drawRoundRect(diceX, diceY, diceSize, diceSize, 20, 20);

            // Draw dice dots
            g.setColor(Color.WHITE);
            drawDiceDots(g, diceX, diceY, diceSize, game.lastDiceRoll);

            // Direction indicator
            int indicatorY = diceY + diceSize + 20;
            g.setFont(new Font("Arial Black", Font.BOLD, 18));
            if (game.lastDiceGreen) {
                g.setColor(new Color(34, 197, 94));
                g.drawString("FORWARD", diceX + 10, indicatorY);
            } else {
                g.setColor(new Color(239, 68, 68));
                g.drawString("BACKWARD", diceX + 5, indicatorY);
            }
        }
    }

    private void drawDiceDots(Graphics2D g, int x, int y, int size, int value) {
        int dotSize = 15;
        int margin = 20;
        int center = size / 2;

        switch (value) {
            case 1:
                g.fillOval(x + center - dotSize / 2, y + center - dotSize / 2, dotSize, dotSize);
                break;
            case 2:
                g.fillOval(x + margin, y + margin, dotSize, dotSize);
                g.fillOval(x + size - margin - dotSize, y + size - margin - dotSize, dotSize, dotSize);
                break;
            case 3:
                g.fillOval(x + margin, y + margin, dotSize, dotSize);
                g.fillOval(x + center - dotSize / 2, y + center - dotSize / 2, dotSize, dotSize);
                g.fillOval(x + size - margin - dotSize, y + size - margin - dotSize, dotSize, dotSize);
                break;
            case 4:
                g.fillOval(x + margin, y + margin, dotSize, dotSize);
                g.fillOval(x + size - margin - dotSize, y + margin, dotSize, dotSize);
                g.fillOval(x + margin, y + size - margin - dotSize, dotSize, dotSize);
                g.fillOval(x + size - margin - dotSize, y + size - margin - dotSize, dotSize, dotSize);
                break;
            case 5:
                g.fillOval(x + margin, y + margin, dotSize, dotSize);
                g.fillOval(x + size - margin - dotSize, y + margin, dotSize, dotSize);
                g.fillOval(x + center - dotSize / 2, y + center - dotSize / 2, dotSize, dotSize);
                g.fillOval(x + margin, y + size - margin - dotSize, dotSize, dotSize);
                g.fillOval(x + size - margin - dotSize, y + size - margin - dotSize, dotSize, dotSize);
                break;
            case 6:
                g.fillOval(x + margin, y + margin, dotSize, dotSize);
                g.fillOval(x + margin, y + center - dotSize / 2, dotSize, dotSize);
                g.fillOval(x + margin, y + size - margin - dotSize, dotSize, dotSize);
                g.fillOval(x + size - margin - dotSize, y + margin, dotSize, dotSize);
                g.fillOval(x + size - margin - dotSize, y + center - dotSize / 2, dotSize, dotSize);
                g.fillOval(x + size - margin - dotSize, y + size - margin - dotSize, dotSize, dotSize);
                break;
        }
    }

    private Point getNodePosition(int nodeNum) {
        int row = nodeNum / BOARD_COLS;
        int col = nodeNum % BOARD_COLS;

        boolean leftToRight = row % 2 == 0;
        int actualCol = leftToRight ? col : (BOARD_COLS - 1 - col);
        int actualRow = BOARD_ROWS - 1 - row;

        return new Point(30 + actualCol * NODE_SIZE + 30, 30 + actualRow * NODE_SIZE + 30);
    }
}

// Main game class
public class LadderAdventureGame extends JFrame {
    private static final int TOTAL_NODES = 64; // 0 .. 63
    List<Player> players;
    private Queue<Player> playerQueue;
    List<Ladder> ladders;
    private Random random;
    int lastDiceRoll = 0;
    boolean lastDiceGreen = true;

    private JPanel currentPanel;
    private GamePanel gamePanel;
    private JButton rollDiceButton;
    private boolean isAnimating = false;
    private boolean gameEnded = false;

    public LadderAdventureGame() {
        setTitle("Ladder Adventure Game - Epic Climbing Challenge");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(15, 23, 42));

        players = new ArrayList<>();
        playerQueue = new LinkedList<>();
        ladders = new ArrayList<>();
        random = new Random();

        // Initialize ladders ONLY (NO SNAKES!)
        ladders.add(new Ladder(2, 23)); // Long ladder
        ladders.add(new Ladder(4, 14)); // Short ladder
        ladders.add(new Ladder(8, 30)); // Long ladder
        ladders.add(new Ladder(15, 37)); // Long ladder
        ladders.add(new Ladder(20, 42)); // Medium ladder
        ladders.add(new Ladder(28, 50)); // Long ladder
        ladders.add(new Ladder(40, 59)); // Medium ladder
        ladders.add(new Ladder(51, 61)); // Short ladder near end

        showSetupPanel();

        setSize(1000, 850);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void showSetupPanel() {
        if (currentPanel != null) {
            remove(currentPanel);
        }
        currentPanel = new SetupPanel(this);
        add(currentPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    void setupPlayers(int playerCount) {
        if (currentPanel != null) {
            remove(currentPanel);
        }
        currentPanel = new PlayerInputPanel(this, playerCount);
        add(currentPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    void startGameWithPlayers(List<String> names) {
        Color[] colors = {
                new Color(239, 68, 68),
                new Color(59, 130, 246),
                new Color(34, 197, 94),
                new Color(249, 115, 22),
                new Color(168, 85, 247),
                new Color(6, 182, 212)
        };

        for (int i = 0; i < names.size(); i++) {
            Player player = new Player(names.get(i), colors[i]);
            players.add(player);
            playerQueue.offer(player);
        }

        showGamePanel();
    }

    private void showGamePanel() {
        if (currentPanel != null) {
            remove(currentPanel);
        }

        gamePanel = new GamePanel(this);
        add(gamePanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(15, 23, 42));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));

        rollDiceButton = new JButton("ROLL DICE");
        rollDiceButton.setFont(new Font("Arial Black", Font.BOLD, 18));
        rollDiceButton.setBackground(new Color(34, 197, 94));
        rollDiceButton.setForeground(Color.WHITE);
        rollDiceButton.setFocusPainted(false);
        rollDiceButton.setBorderPainted(false);
        rollDiceButton.setPreferredSize(new Dimension(200, 60));
        rollDiceButton.addActionListener(e -> rollDiceAction());
        controlPanel.add(rollDiceButton);

        add(controlPanel, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    public Player getCurrentPlayer() {
        return playerQueue.peek();
    }

    private void rollDiceAction() {
        if (isAnimating || gameEnded)
            return;

        Player current = getCurrentPlayer();
        if (current == null)
            return;

        rollDiceButton.setEnabled(false);
        isAnimating = true;

        // Roll dice
        int roll = random.nextInt(6) + 1;
        lastDiceRoll = roll;

        // 70% green (forward), 30% red (backward)
        double randomValue = random.nextDouble();
        lastDiceGreen = (randomValue < 0.7);

        current.addStep(roll);

        // Special rule: dice 5 = move 2x
        int actualMove = (roll == 5) ? roll * 2 : roll;

        // Calculate new position
        int startPos = current.position;
        int targetPos;
        if (lastDiceGreen) {
            targetPos = Math.min(TOTAL_NODES - 1, current.position + actualMove);
            SoundPlayer.playSound("forward");
        } else {
            targetPos = Math.max(0, current.position - actualMove);
            SoundPlayer.playSound("backward");
        }

        // Animate movement
        animatePlayerMovement(current, startPos, targetPos);
    }

    private void animatePlayerMovement(Player player, int startPos, int targetPos) {
        javax.swing.Timer animationTimer = new javax.swing.Timer(150, null);
        final int[] currentAnimPos = { startPos };
        final int step = (targetPos > startPos) ? 1 : -1;

        animationTimer.addActionListener(e -> {
            if (currentAnimPos[0] == targetPos) {
                animationTimer.stop();
                player.position = targetPos;

                // Check for ladder
                checkLadder(player);

                // Check victory
                if (player.position >= TOTAL_NODES - 1) {
                    gamePanel.repaint();
                    SoundPlayer.playSound("win");
                    JOptionPane.showMessageDialog(this,
                            player.name + " wins the game! 🎉\n" +
                                    "Congratulations on reaching the top!",
                            "WINNER!", JOptionPane.PLAIN_MESSAGE);
                    rollDiceButton.setEnabled(false);
                    gameEnded = true;
                    isAnimating = false;
                    return;
                }

                // Auto next player
                javax.swing.Timer nextPlayerTimer = new javax.swing.Timer(1000, evt -> {
                    Player p = playerQueue.poll();
                    if (p != null) {
                        playerQueue.offer(p);
                    }
                    isAnimating = false;
                    rollDiceButton.setEnabled(true);
                    gamePanel.repaint();
                });
                nextPlayerTimer.setRepeats(false);
                nextPlayerTimer.start();
            } else {
                currentAnimPos[0] += step;
                player.position = currentAnimPos[0];
                gamePanel.repaint();
            }
        });
        animationTimer.start();
    }

    private void checkLadder(Player player) {
        for (Ladder ladder : ladders) {
            if (player.position == ladder.bottom) {
                int oldPos = player.position;
                player.position = ladder.top;
                SoundPlayer.playSound("ladder");

                javax.swing.Timer ladderTimer = new javax.swing.Timer(300, e -> {
                    JOptionPane.showMessageDialog(this,
                            player.name + " found a ladder!\n" +
                                    "Climb from node " + oldPos + " to node " + ladder.top + "! 🪜",
                            "LADDER!", JOptionPane.INFORMATION_MESSAGE);
                });
                ladderTimer.setRepeats(false);
                ladderTimer.start();
                break;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LadderAdventureGame());
    }
}
