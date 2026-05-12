package src;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * UITheme.java
 * Provides modern, consistent UI styling throughout the application
 * Inspired by modern fintech applications like Konek2Card
 */
public class UITheme {
    
    // Primary Colors (Modern fintech palette)
    public static final Color PRIMARY_BLUE = new Color(24, 109, 201);      // #186DC9
    public static final Color SECONDARY_TEAL = new Color(0, 167, 158);     // #00A79E
    public static final Color ACCENT_ORANGE = new Color(255, 140, 66);    // #FF8C42
    public static final Color SUCCESS_GREEN = new Color(46, 196, 111);    // #2EC46F
    public static final Color WARNING_YELLOW = new Color(255, 193, 7);    // #FFC107
    public static final Color DANGER_RED = new Color(229, 57, 53);        // #E53935
    
    // Neutral Colors
    public static final Color DARK_GRAY = new Color(33, 33, 33);           // #212121
    public static final Color LIGHT_GRAY = new Color(245, 245, 245);       // #F5F5F5
    public static final Color BORDER_GRAY = new Color(224, 224, 224);      // #E0E0E0
    public static final Color TEXT_PRIMARY = new Color(33, 33, 33);        // #212121
    public static final Color TEXT_SECONDARY = new Color(117, 117, 117);   // #757575
    public static final Color TEXT_LIGHT = new Color(189, 189, 189);       // #BDBDBD
    
    // Background
    public static final Color BG_WHITE = Color.WHITE;
    public static final Color BG_LIGHT = new Color(250, 250, 250);
    
    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_SUBHEADING = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 10);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    
    // Insets and Spacing
    public static final Insets PADDING_LARGE = new Insets(20, 20, 20, 20);
    public static final Insets PADDING_MEDIUM = new Insets(15, 15, 15, 15);
    public static final Insets PADDING_SMALL = new Insets(10, 10, 10, 10);
    
    /**
     * Create a modern styled button
     */
    public static JButton createModernButton(String text, Color backgroundColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isArmed()) {
                    g2.setColor(new Color(
                        Math.max(backgroundColor.getRed() - 20, 0),
                        Math.max(backgroundColor.getGreen() - 20, 0),
                        Math.max(backgroundColor.getBlue() - 20, 0)
                    ));
                } else {
                    g2.setColor(backgroundColor);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }
        };
        
        button.setForeground(Color.WHITE);
        button.setFont(FONT_BOLD);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 40));
        
        return button;
    }
    
    /**
     * Create a modern styled text field
     */
    public static JTextField createModernTextField(int columns) {
        JTextField textField = new JTextField(columns) {
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isFocusOwner() ? PRIMARY_BLUE : BORDER_GRAY);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
            }
        };
        
        textField.setFont(FONT_REGULAR);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_GRAY, 2, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        textField.setBackground(BG_WHITE);
        textField.setForeground(TEXT_PRIMARY);
        textField.setCaretColor(PRIMARY_BLUE);
        
        return textField;
    }
    
    /**
     * Create a modern styled password field
     */
    public static JPasswordField createModernPasswordField(int columns) {
        JPasswordField passwordField = new JPasswordField(columns) {
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isFocusOwner() ? PRIMARY_BLUE : BORDER_GRAY);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
            }
        };
        
        passwordField.setFont(FONT_REGULAR);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_GRAY, 2, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        passwordField.setBackground(BG_WHITE);
        passwordField.setForeground(TEXT_PRIMARY);
        passwordField.setCaretColor(PRIMARY_BLUE);
        
        return passwordField;
    }
    
    /**
     * Create a modern card panel (like Konek2Card)
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Draw subtle shadow
                g2.setColor(new Color(0, 0, 0, 15));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            }
        };
        
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_GRAY, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setBackground(BG_WHITE);
        
        return panel;
    }
    
    /**
     * Create a balance display card
     */
    public static JPanel createBalanceCard(String title, String amount, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, accentColor,
                    getWidth(), getHeight(), new Color(
                        Math.min(accentColor.getRed() + 30, 255),
                        Math.min(accentColor.getGreen() + 30, 255),
                        Math.min(accentColor.getBlue() + 30, 255)
                    )
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_REGULAR);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel amountLabel = new JLabel(amount);
        amountLabel.setFont(FONT_TITLE);
        amountLabel.setForeground(Color.WHITE);
        amountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(amountLabel);
        
        return card;
    }
    
    /**
     * Create a rounded label (badge style)
     */
    public static JLabel createBadge(String text, Color backgroundColor) {
        JLabel label = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(backgroundColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
            }
        };
        
        label.setFont(FONT_SMALL);
        label.setForeground(Color.WHITE);
        label.setOpaque(false);
        label.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        label.setPreferredSize(new Dimension(80, 24));
        
        return label;
    }
    
    /**
     * Create a modern separator
     */
    public static JSeparator createModernSeparator() {
        JSeparator separator = new JSeparator(JSeparator.HORIZONTAL) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(BORDER_GRAY);
                g2.fillRect(0, getHeight() / 2 - 1, getWidth(), 2);
            }
        };
        
        separator.setForeground(BORDER_GRAY);
        return separator;
    }
    
    /**
     * Apply modern theme to a JFrame
     */
    public static void applyTheme(JFrame frame) {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
        
        // Set window background
        JPanel contentPane = (JPanel) frame.getContentPane();
        contentPane.setBackground(BG_LIGHT);
    }

    /**
     * Create a modern header panel with title and icon
     */
    public static JPanel createHeaderPanel(String title, String icon) {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(BORDER_GRAY);
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            }
        };
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Dialog", Font.PLAIN, 20));
        headerPanel.add(iconLabel, BorderLayout.WEST);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_HEADING);
        titleLabel.setForeground(TEXT_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        return headerPanel;
    }

    /**
     * Create a styled info/stats card
     */
    public static JPanel createInfoCard(String title, String value, String subtitle, Color borderColor) {
        JPanel card = createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_SMALL);
        titleLabel.setForeground(TEXT_SECONDARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(FONT_TITLE);
        valueLabel.setForeground(borderColor);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(subtitle != null ? subtitle : "");
        subtitleLabel.setFont(FONT_SMALL);
        subtitleLabel.setForeground(TEXT_LIGHT);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(valueLabel);
        if (subtitle != null && !subtitle.isEmpty()) {
            card.add(Box.createVerticalStrut(5));
            card.add(subtitleLabel);
        }

        return card;
    }

    /**
     * Create a section header with divider
     */
    public static JPanel createSectionHeader(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_SUBHEADING);
        titleLabel.setForeground(PRIMARY_BLUE);

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(createModernSeparator(), BorderLayout.CENTER);

        return panel;
    }

    /**
     * Create a button with secondary style
     */
    public static JButton createSecondaryButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isArmed()) {
                    g2.setColor(new Color(240, 240, 240));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(245, 245, 245));
                } else {
                    g2.setColor(BG_WHITE);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                // Border
                g2.setColor(BORDER_GRAY);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                
                super.paintComponent(g);
            }
        };

        button.setForeground(TEXT_PRIMARY);
        button.setFont(FONT_BOLD);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 40));

        return button;
    }

    /**
     * Create a danger button (red)
     */
    public static JButton createDangerButton(String text) {
        return createModernButton(text, DANGER_RED);
    }

    /**
     * Create a success button (green)
     */
    public static JButton createSuccessButton(String text) {
        return createModernButton(text, SUCCESS_GREEN);
    }

    /**
     * Create a form panel with better styling
     */
    public static JPanel createFormPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setOpaque(true);
        panel.setBackground(BG_WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_GRAY, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        if (title != null && !title.isEmpty()) {
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(FONT_SUBHEADING);
            titleLabel.setForeground(TEXT_PRIMARY);
            panel.add(titleLabel, BorderLayout.NORTH);
        }

        return panel;
    }

    /**
     * Create a two-column form layout
     */
    public static JPanel createFormFieldPanel(String label, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setOpaque(false);

        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(FONT_BOLD);
        labelComponent.setForeground(TEXT_PRIMARY);

        panel.add(labelComponent, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Create a table header with modern styling
     */
    public static JPanel createTableHeader(String[] columnNames) {
        JPanel headerPanel = new JPanel(new GridLayout(1, columnNames.length));
        headerPanel.setBackground(PRIMARY_BLUE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (String columnName : columnNames) {
            JLabel label = new JLabel(columnName);
            label.setFont(FONT_BOLD);
            label.setForeground(Color.WHITE);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            headerPanel.add(label);
        }

        return headerPanel;
    }

    /**
     * Create a modern dialog panel
     */
    public static JPanel createDialogPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(BG_WHITE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(TEXT_PRIMARY);
        panel.add(titleLabel, BorderLayout.NORTH);

        return panel;
    }

    /**
     * Create a loading spinner (simple animated label)
     */
    public static JLabel createLoadingSpinner(String text) {
        JLabel label = new JLabel(text + " ⟳");
        label.setFont(FONT_REGULAR);
        label.setForeground(PRIMARY_BLUE);
        return label;
    }

    /**
     * Create a horizontal button panel
     */
    public static JPanel createButtonPanel(JButton... buttons) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setOpaque(false);

        for (JButton button : buttons) {
            panel.add(button);
        }

        return panel;
    }

    /**
     * Create a stat box with icon and value
     */
    public static JPanel createStatBox(String icon, String value, String label, Color backgroundColor) {
        JPanel box = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, backgroundColor,
                    getWidth(), getHeight(), new Color(
                        Math.min(backgroundColor.getRed() + 20, 255),
                        Math.min(backgroundColor.getGreen() + 20, 255),
                        Math.min(backgroundColor.getBlue() + 20, 255)
                    )
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            }
        };

        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        box.setOpaque(false);
        box.setPreferredSize(new Dimension(150, 120));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Dialog", Font.PLAIN, 28));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(FONT_SMALL);
        labelComponent.setForeground(new Color(200, 200, 200));
        labelComponent.setAlignmentX(Component.CENTER_ALIGNMENT);

        box.add(iconLabel);
        box.add(Box.createVerticalStrut(5));
        box.add(valueLabel);
        box.add(Box.createVerticalStrut(3));
        box.add(labelComponent);

        return box;
    }

    /**
     * Create a notification badge with count
     */
    public static JLabel createNotificationBadge(int count) {
        JLabel badge = new JLabel(String.valueOf(count)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(DANGER_RED);
                g2.fillOval(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };

        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setForeground(Color.WHITE);
        badge.setOpaque(false);
        badge.setHorizontalAlignment(SwingConstants.CENTER);
        badge.setVerticalAlignment(SwingConstants.CENTER);
        badge.setPreferredSize(new Dimension(20, 20));

        return badge;
    }

    /**
     * Create a modern text area with rounded corners
     */
    public static JTextArea createModernTextArea(int rows, int columns) {
        JTextArea textArea = new JTextArea(rows, columns) {
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isFocusOwner() ? PRIMARY_BLUE : BORDER_GRAY);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
            }
        };

        textArea.setFont(FONT_REGULAR);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_GRAY, 2, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        textArea.setBackground(BG_WHITE);
        textArea.setForeground(TEXT_PRIMARY);
        textArea.setCaretColor(PRIMARY_BLUE);

        return textArea;
    }

    /**
     * Create a status indicator
     */
    public static JPanel createStatusIndicator(String status, Color color) {
        JPanel indicator = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        indicator.setOpaque(false);

        JPanel dot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(0, 0, getWidth(), getHeight());
            }
        };
        dot.setOpaque(false);
        dot.setPreferredSize(new Dimension(10, 10));

        JLabel label = new JLabel(status);
        label.setFont(FONT_SMALL);
        label.setForeground(TEXT_SECONDARY);

        indicator.add(dot);
        indicator.add(label);

        return indicator;
    }

    /**
     * Create an animated gradient button
     */
    public static JButton createGradientButton(String text, Color color1, Color color2) {
        JButton button = new JButton(text) {
            private boolean isPressed = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color startColor = isPressed || getModel().isArmed() ? 
                    new Color(Math.max(color1.getRed() - 20, 0),
                              Math.max(color1.getGreen() - 20, 0),
                              Math.max(color1.getBlue() - 20, 0)) : color1;

                Color endColor = isPressed || getModel().isArmed() ?
                    new Color(Math.max(color2.getRed() - 20, 0),
                              Math.max(color2.getGreen() - 20, 0),
                              Math.max(color2.getBlue() - 20, 0)) : color2;

                GradientPaint gradient = new GradientPaint(0, 0, startColor, getWidth(), getHeight(), endColor);
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                super.paintComponent(g);
            }
        };

        button.setForeground(Color.WHITE);
        button.setFont(FONT_BOLD);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 40));

        return button;
    }
}
