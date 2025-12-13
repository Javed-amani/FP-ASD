import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * UlartanggaASD.java
 * Simple flow:
 *  - Menu awal (PLAY)
 *  - Pilih jumlah pemain (2/3/4)
 *  - Tampilkan board 8x8 (64 node) dengan nomor zig-zag
 *
 * Save as: UlartanggaASD.java
 * Compile: javac UlartanggaASD.java
 * Run:     java UlartanggaASD
 */
public class UlarTanggaASD {
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public UlarTanggaASD() {
        frame = new JFrame("Ular Tangga ");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 820);
        frame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createMenuPanel(), "menu");
        mainPanel.add(createPlayerSelectPanel(), "player_select");
        mainPanel.add(createBoardPanel(), "board");

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    // PANEL 1: Menu Awal (Play)
    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Game Ular Tangga", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        panel.add(title, BorderLayout.CENTER);

        JButton play = new JButton("PLAY");
        play.setFont(new Font("Arial", Font.BOLD, 24));
        play.addActionListener(e -> cardLayout.show(mainPanel, "player_select"));
        JPanel south = new JPanel();
        south.add(play);
        panel.add(south, BorderLayout.SOUTH);

        return panel;
    }

    // PANEL 2: Pilih Jumlah Player
    private JPanel createPlayerSelectPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8,8,8,8);
        c.gridx = 0;
        c.gridy = 0;

        JLabel label = new JLabel("Pilih Jumlah Pemain", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 28));
        panel.add(label, c);

        c.gridy++;
        JButton p2 = new JButton("2 Pemain");
        JButton p3 = new JButton("3 Pemain");
        JButton p4 = new JButton("4 Pemain");

        ActionListener goToBoard = e -> cardLayout.show(mainPanel, "board");
        p2.addActionListener(goToBoard);
        p3.addActionListener(goToBoard);
        p4.addActionListener(goToBoard);

        c.gridy++;
        panel.add(p2, c);
        c.gridy++;
        panel.add(p3, c);
        c.gridy++;
        panel.add(p4, c);

        return panel;
    }

    // PANEL 3: Board 64 Node (8x8) - zig-zag numbering
    private JPanel createBoardPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        JPanel board = new JPanel(new GridLayout(8, 8));
        board.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // Build rows from top-to-bottom but numbers filled bottom-left zig-zag
        // We'll compute numbers per row starting from 64 down to 1 and reverse every other row
        int num = 64;
        boolean reverseNext = false; // because we fill visual rows top->bottom, we need to reverse appropriately
        // we'll construct an array of rows then add to panel in visual top-down order
        int[][] grid = new int[8][8];
        for (int r = 7; r >= 0; r--) { // r=7 is bottom row in visual terms
            int[] rownums = new int[8];
            for (int c = 0; c < 8; c++) {
                rownums[c] = num--;
            }
            if (reverseNext) {
                for (int c=0;c<8;c++) grid[r][c] = rownums[7-c];
            } else {
                for (int c=0;c<8;c++) grid[r][c] = rownums[c];
            }
            reverseNext = !reverseNext;
        }

        // Now add to board from row 0 (top) to row 7 (bottom), columns left->right
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                JPanel cell = new JPanel(new BorderLayout());
                cell.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
                int val = grid[r][c];
                JLabel numLabel = new JLabel(String.valueOf(val), SwingConstants.LEFT);
                numLabel.setBorder(BorderFactory.createEmptyBorder(4,6,0,0));
                numLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
                cell.add(numLabel, BorderLayout.NORTH);

                // subtle background alternation
                if (((r + c) % 2) == 0) cell.setBackground(new Color(0xF0F8FF));
                else cell.setBackground(new Color(0xFFF5E6));

                board.add(cell);
            }
        }

        wrapper.add(board, BorderLayout.CENTER);

        // footer info
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wrapper.add(footer, BorderLayout.SOUTH);
        return wrapper;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(UlarTanggaASD::new);
    }
}
