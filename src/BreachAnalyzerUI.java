import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class BreachAnalyzerUI extends JFrame {

    private UserService userService;

    // Web Colors & Gradients
    private static final Color GRADIENT_START = new Color(8, 34, 58); // Purple
    private static final Color GRADIENT_END = new Color(0, 78, 146); // Pink/Red
    private static final Color CARD_BG = new Color(255, 255, 255, 245); // Glass white

    private static final Color BTN_BG = new Color(0, 120, 215); // Orange
    private static final Color BTN_HOVER = new Color(0, 150, 255);

    private static final Color DANGER_COLOR = new Color(231, 76, 60); // Coral Red
    private static final Color SAFE_COLOR = new Color(39, 174, 96); // Mint Green
    private static final Color WARN_COLOR = new Color(243, 156, 18); // Yellow/Orange
    private static final Color TEXT_DARK = new Color(32, 40, 56);

    // Layout
    private CardLayout cardLayout;
    private JPanel cardContainer;

    // Components
    private JTextField nameField;
    private JTextField deptField;
    private JPasswordField passField;
    private JLabel statusLabel;
    private JLabel registeredCountLabel;

    private DefaultTableModel tableModel;
    private GraphPanel graphPanel;
    private JTextArea warningArea;
    private int targetUserCount = 0;
    private JTextField targetCountField;

    public BreachAnalyzerUI() {
        userService = new UserService();

        setTitle("Password Breach Impact Analyzer");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Gradient Background for the entire window
        GradientPanel mainBackground = new GradientPanel();
        mainBackground.setLayout(new BorderLayout());
        setContentPane(mainBackground);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(30, 0, 10, 0));
        JLabel titleLabel = new JLabel("Password Breach Impact Analyzer");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel subtitle = new JLabel("Graph-Based Password Security Analysis");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 18));
        subtitle.setForeground(new Color(230,230,230));

        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0,8)));
        headerPanel.add(subtitle);
        mainBackground.add(headerPanel, BorderLayout.NORTH);

        // Card Layout Container
        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        cardContainer.setOpaque(false);
        cardContainer.setBorder(new EmptyBorder(20, 40, 40, 40));

        // Build Slides
        cardContainer.add(buildSlide1(), "SLIDE1");
        cardContainer.add(buildSlide2(), "SLIDE2");

        mainBackground.add(cardContainer, BorderLayout.CENTER);

        cardLayout.show(cardContainer, "SLIDE1");
    }

    // --- SLIDE 1: REGISTRATION ---
    private JPanel buildSlide1() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        RoundedCard formCard = new RoundedCard(30);
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBorder(new EmptyBorder(40, 50, 40, 50));
        formCard.setPreferredSize(new Dimension(450, 500));

        JLabel formTitle = new JLabel("User Registration");
        formTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        formTitle.setForeground(TEXT_DARK);
        formTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        formCard.add(formTitle);
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));

        formCard.add(createLabel("Number of Users"));
        targetCountField = createTextField();
        targetCountField.setHorizontalAlignment(JTextField.CENTER);
        formCard.add(targetCountField);
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));

        formCard.add(createLabel("Full Name"));
        nameField = createTextField();
        formCard.add(nameField);
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));

        formCard.add(createLabel("Department"));
        deptField = createTextField();
        formCard.add(deptField);
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));

        formCard.add(createLabel("Password"));
        passField = new JPasswordField();
        styleTextField(passField);
        formCard.add(passField);
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        statusPanel.setOpaque(false);
        JLabel strLbl = createLabel("Strength:  ");
        strLbl.setFont(new Font("SansSerif", Font.BOLD, 15));
        statusPanel.add(strLbl);

        statusLabel = new JLabel("N/A");
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        statusLabel.setForeground(new Color(150, 150, 150));
        statusPanel.add(statusLabel);
        formCard.add(statusPanel);
        formCard.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.setOpaque(false);
        RoundedButton checkBtn = new RoundedButton("Check Strength", new Color(0, 120, 215), new Color(0, 150, 255));
        checkBtn.setPreferredSize(new Dimension(120, 40));
        RoundedButton addBtn = new RoundedButton("Register User", BTN_BG, BTN_HOVER);
        addBtn.setPreferredSize(new Dimension(120, 40));
        btnPanel.add(checkBtn);
        btnPanel.add(addBtn);
        formCard.add(btnPanel);
        formCard.add(Box.createRigidArea(new Dimension(0, 30)));

        registeredCountLabel = new BadgeLabel("👥 Registered: 0 / 0");
        registeredCountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formCard.add(registeredCountLabel);

        formCard.add(Box.createRigidArea(new Dimension(0, 20)));
        RoundedButton analyzeBtn = new RoundedButton("Run Security Analysis", SAFE_COLOR, new Color(85, 239, 196));
        analyzeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        formCard.add(analyzeBtn);

        panel.add(formCard);

        // Listeners
        checkBtn.addActionListener(e -> checkStrength());
        addBtn.addActionListener(e -> addUser());
        analyzeBtn.addActionListener(e -> performAnalysis());

        return panel;
    }

    private void performAnalysis() {
        updateDataTable();
        graphPanel.repaint();
        cardLayout.show(cardContainer, "SLIDE2");

        // Update Warning Area for isolated weak nodes
        List<UserService.User> users = userService.getUsers();
        if (!users.isEmpty()) {
            ArrayList<ArrayList<Integer>> adj = userService.getRawSimilarityGraph();
            List<String> isolatedWeakUsers = new ArrayList<>();
            for (int i = 0; i < users.size(); i++) {
                UserService.User u = users.get(i);
                String strength = userService.check(u.originalPassword, u.name);
                if (strength.equals("Weak") && adj.get(i).isEmpty()) {
                    isolatedWeakUsers.add(u.name);
                }
            }

            if (!isolatedWeakUsers.isEmpty()) {
                StringBuilder sb = new StringBuilder(
                        "⚠️ CRITICAL WARNING! Isolated weak accounts must be changed:\n");
                for (String userName : isolatedWeakUsers) {
                    sb.append(" ➔ ").append(userName).append("\n");
                }
                warningArea.setText(sb.toString());
                warningArea.setVisible(true);
            } else {
                warningArea.setVisible(false);
            }
        } else {
            warningArea.setVisible(false);
        }
    }

    // --- SLIDE 2: ANALYSIS DASHBOARD ---
    private JPanel buildSlide2() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);

        // Top Control Bar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setOpaque(false);
        RoundedButton backBtn = new RoundedButton("← Register More Users", new Color(0, 120, 215),
                new Color(0, 150, 255));
        RoundedButton clearBtn = new RoundedButton("Clear Analysis", DANGER_COLOR, new Color(255, 130, 130));
        topBar.add(backBtn);
        topBar.add(clearBtn);
        panel.add(topBar, BorderLayout.NORTH);

        // Center split: Graph on left (60%), Table on right (40%)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setOpaque(false);
        splitPane.setDividerSize(0);
        splitPane.setBorder(null);

        // Left: Graph Card
        RoundedCard graphCard = new RoundedCard(30);
        graphCard.setLayout(new BorderLayout());
        graphCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel graphTitle = new JLabel("Password Relationship Graph");
        graphTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        graphTitle.setForeground(TEXT_DARK);
        graphCard.add(graphTitle, BorderLayout.NORTH);
        graphPanel = new GraphPanel();
        graphCard.add(graphPanel, BorderLayout.CENTER);

        // Right: Data Table Card (Replaces awful terminal)
        RoundedCard tableCard = new RoundedCard(30);
        tableCard.setLayout(new BorderLayout(0, 15));
        tableCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel tableTitle = new JLabel("Security Report");
        tableTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        tableTitle.setForeground(TEXT_DARK);
        tableCard.add(tableTitle, BorderLayout.NORTH);

        String[] columns = { "Name", "Strength", "SHA-256 Hash" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable dataTable = new JTable(tableModel);
        styleTable(dataTable);

        JScrollPane scrollPane = new JScrollPane(dataTable);
        scrollPane.setBorder(new RoundedBorder(15, new Color(230, 230, 230)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        warningArea = new JTextArea(4, 20);
        warningArea.setEditable(false);
        warningArea.setOpaque(false);
        warningArea.setFont(new Font("SansSerif", Font.BOLD, 14));
        warningArea.setForeground(DANGER_COLOR);
        warningArea.setWrapStyleWord(true);
        warningArea.setLineWrap(true);
        warningArea.setBorder(new EmptyBorder(10, 0, 0, 0));
        warningArea.setVisible(false); // Hidden by default
        tableCard.add(warningArea, BorderLayout.SOUTH);

        splitPane.setLeftComponent(graphCard);
        splitPane.setRightComponent(tableCard);
        splitPane.setResizeWeight(0.6); // 60% for graph
        panel.add(splitPane, BorderLayout.CENTER);

        // Listeners
        backBtn.addActionListener(e -> cardLayout.show(cardContainer, "SLIDE1"));
        clearBtn.addActionListener(e -> {
            userService.clearUsers();
            targetUserCount = 0;
            targetCountField.setText("");
            registeredCountLabel.setText("👥 Registered: 0 / 0");
            updateDataTable();
            warningArea.setVisible(false);
            graphPanel.repaint();
            cardLayout.show(cardContainer, "SLIDE1"); // Reset back to Registration
        });

        return panel;
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setRowHeight(35);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(240, 245, 255));
        table.setSelectionForeground(TEXT_DARK);
        table.setForeground(TEXT_DARK);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setBackground(new Color(15, 76, 129));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(220, 220, 220)));
        header.setPreferredSize(new Dimension(0, 40));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private void updateDataTable() {
        tableModel.setRowCount(0); // clear existing
        for (UserService.User u : userService.getUsers()) {
            String strength = userService.check(u.originalPassword, u.name);
            String shortHash = u.passHash.length() > 12 ? u.passHash.substring(0, 12) + "..." : u.passHash;
            tableModel.addRow(new Object[] { u.name, strength, shortHash });
        }
    }

    // --- LOGIC METHODS ---
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(new Color(100, 100, 100));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JTextField createTextField() {
        JTextField tf = new JTextField();
        styleTextField(tf);
        return tf;
    }

    private void styleTextField(JTextField tf) {
        tf.setMaximumSize(new Dimension(300, 45));
        tf.setPreferredSize(new Dimension(300, 45));
        tf.setFont(new Font("SansSerif", Font.PLAIN, 16));
        tf.setForeground(TEXT_DARK);
        tf.setBackground(Color.WHITE);
        tf.setCaretColor(TEXT_DARK);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15, new Color(220, 220, 220)),
                new EmptyBorder(5, 15, 5, 15)));
        tf.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private void checkStrength() {
        String name = nameField.getText().trim();
        String pass = new String(passField.getPassword());
        if (pass.isEmpty())
            return;

        String level = userService.check(pass, name);
        statusLabel.setText(level);
        if (level.equals("Strong"))
            statusLabel.setForeground(SAFE_COLOR);
        else if (level.equals("Medium"))
            statusLabel.setForeground(WARN_COLOR);
        else
            statusLabel.setForeground(DANGER_COLOR);
    }

    private void addUser() {
        String name = nameField.getText().trim();
        String dept = deptField.getText().trim();
        String pass = new String(passField.getPassword());

        try {
            int count = Integer.parseInt(targetCountField.getText().trim());
            if (count <= 0)
                throw new NumberFormatException();
            targetUserCount = count;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Target Number of Users.");
            return;
        }

        if (name.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fields cannot be empty.");
            return;
        }

        // Update strength label visually
        String level = userService.check(pass, name);
        statusLabel.setText(level);
        if (level.equals("Strong"))
            statusLabel.setForeground(SAFE_COLOR);
        else if (level.equals("Medium"))
            statusLabel.setForeground(WARN_COLOR);
        else
            statusLabel.setForeground(DANGER_COLOR);

        if (level.equals("Weak")) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "This password is Weak! Do you want to add it anyway for testing purposes?",
                    "Weak Password Warning",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            userService.addUser(name, dept, pass);
            nameField.setText("");
            deptField.setText("");
            passField.setText("");
            statusLabel.setText("N/A");
            statusLabel.setForeground(new Color(150, 150, 150));

            int current = userService.getUsers().size();
            registeredCountLabel.setText("👥 Registered: " + current + " / " + targetUserCount);

            // Auto transition if target is reached
            if (current >= targetUserCount && targetUserCount > 0) {
                performAnalysis();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding user.");
        }
    }

    // --- CUSTOM SWING COMPONENTS FOR WEB AESTHETICS ---

    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            GradientPaint gp = new GradientPaint(0, 0, GRADIENT_START, getWidth(), getHeight(), GRADIENT_END);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    class RoundedCard extends JPanel {
        private int radius;

        public RoundedCard(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Soft drop shadow
            g2.setColor(new Color(0, 0, 0, 20));
            g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, radius, radius);
            // White card
            g2.setColor(CARD_BG);
            g2.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    class RoundedButton extends JButton {
        private Color bgColor;
        private Color hoverColor;
        private boolean isHovered = false;

        public RoundedButton(String text, Color bg, Color hover) {
            super(text);
            this.bgColor = bg;
            this.hoverColor = hover;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("SansSerif", Font.BOLD, 15));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(200, 45));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isHovered ? hoverColor : bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    class RoundedBorder extends AbstractBorder {
        private int radius;
        private Color color;

        public RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
    }

    class BadgeLabel extends JLabel {
        public BadgeLabel(String text) {
            super(text);
            setFont(new Font("SansSerif", Font.BOLD, 14));
            setForeground(Color.WHITE);
            setHorizontalAlignment(SwingConstants.CENTER);
            setBorder(new EmptyBorder(8, 20, 8, 20));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Draw a beautiful soft purple/blue pill background
            g2.setColor(new Color(0, 120, 215));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    class GraphPanel extends JPanel {
        public GraphPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            List<UserService.User> users = userService.getUsers();
            if (users == null || users.isEmpty()) {
                g2.setColor(new Color(180, 180, 180));
                g2.setFont(new Font("SansSerif", Font.ITALIC, 16));
                g2.drawString("Register users to generate the relationship graph.", 20, 50);
                return;
            }

            ArrayList<ArrayList<Integer>> adj = userService.getRawSimilarityGraph();
            int n = users.size();
            int width = getWidth() - 10;
            int height = getHeight() - 10;
            int cx = width / 2;
            int cy = height / 2;
            int radius = Math.min(width, height) / 2 - 40;

            Point[] positions = new Point[n];
            for (int i = 0; i < n; i++) {
                double angle = 2 * Math.PI * i / n;
                positions[i] = new Point(cx + (int) (radius * Math.cos(angle)), cy + (int) (radius * Math.sin(angle)));
            }

            // Draw Web-style Smooth Edges
            g2.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int i = 0; i < n; i++) {
                for (int target : adj.get(i)) {
                    if (target > i) {
                        g2.setColor(new Color(52, 152, 219, 140)); // Semi-transparent Coral
                        g2.drawLine(positions[i].x, positions[i].y, positions[target].x, positions[target].y);
                    }
                }
            }

            // Draw Web-style Nodes
            int nodeRadius = 26;
            for (int i = 0; i < n; i++) {
                UserService.User u = users.get(i);
                String strength = userService.check(u.originalPassword, u.name);
                boolean hasConnections = !adj.get(i).isEmpty();

                // Shadow for node
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillOval(positions[i].x - nodeRadius + 3, positions[i].y - nodeRadius + 3, nodeRadius * 2,
                        nodeRadius * 2);

                if (strength.equals("Strong")) {
                    g2.setColor(SAFE_COLOR);
                } else if (strength.equals("Medium")) {
                    g2.setColor(WARN_COLOR);
                } else {
                    g2.setColor(DANGER_COLOR);
                }

                g2.fillOval(positions[i].x - nodeRadius, positions[i].y - nodeRadius, nodeRadius * 2, nodeRadius * 2);

                // Thick White Border
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(4.5f));
                g2.drawOval(positions[i].x - nodeRadius, positions[i].y - nodeRadius, nodeRadius * 2, nodeRadius * 2);

                // Label Background Pill
                g2.setFont(new Font("SansSerif", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                int tw = fm.stringWidth(u.name);

                int labelX = positions[i].x - tw / 2;
                int labelY = positions[i].y - nodeRadius - 12;

                g2.setColor(new Color(255, 255, 255, 230));
                g2.fillRoundRect(labelX - 10, labelY - fm.getAscent() - 3, tw + 20, fm.getHeight() + 6, 15, 15);

                g2.setColor(TEXT_DARK);
                g2.drawString(u.name, labelX, labelY);

                // Warning for isolated weak nodes
                if (strength.equals("Weak") && !hasConnections) {
                    g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                    FontMetrics warnFm = g2.getFontMetrics();
                    String warning = "Password Update Required";
                    int warnWidth = warnFm.stringWidth(warning);

                    // Draw a small pill background for the warning
                    int warnX = positions[i].x - warnWidth / 2;
                    int warnY = positions[i].y + nodeRadius + 18;

                    g2.setColor(new Color(255, 107, 107, 40)); // light red bg
                    g2.fillRoundRect(warnX - 6, warnY - warnFm.getAscent() - 2, warnWidth + 12, warnFm.getHeight() + 4,
                            10, 10);

                    g2.setColor(DANGER_COLOR);
                    g2.drawString(warning, warnX, warnY);
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
        }
        SwingUtilities.invokeLater(() -> new BreachAnalyzerUI().setVisible(true));
    }
}