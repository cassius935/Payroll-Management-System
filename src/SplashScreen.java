package src;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * SplashScreen.java
 * Modern splash screen displayed during application startup
 * Shows loading progress and initialization status
 */
public class SplashScreen extends JWindow {
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private JLabel versionLabel;
    private int progress = 0;
    
    public SplashScreen() {
        // Set window properties
        setSize(500, 350);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        
        // Create main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, UITheme.PRIMARY_BLUE,
                    getWidth(), getHeight(), UITheme.SECONDARY_TEAL
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                // Add decorative border
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 10, 10);
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setOpaque(true);
        
        // Logo/Title area
        JPanel logoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Transparent background
                g2.setColor(new Color(0, 0, 0, 0));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 20, 20));
        
        // App title
        JLabel titleLabel = new JLabel("Payroll Management System");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Modern Payroll Processing Solution");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(200, 220, 240));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        logoPanel.add(Box.createVerticalGlue());
        logoPanel.add(titleLabel);
        logoPanel.add(Box.createVerticalStrut(5));
        logoPanel.add(subtitleLabel);
        logoPanel.add(Box.createVerticalGlue());
        
        mainPanel.add(logoPanel, BorderLayout.NORTH);
        
        // Content area with progress
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 30, 40));
        
        // Status label
        statusLabel = new JLabel("Initializing system...");
        statusLabel.setFont(UITheme.FONT_REGULAR);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setString("Loading...");
        progressBar.setPreferredSize(new Dimension(300, 12));
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 12));
        progressBar.setBackground(new Color(100, 100, 100));
        progressBar.setForeground(UITheme.SUCCESS_GREEN);
        progressBar.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        
        // Version label
        versionLabel = new JLabel("Version 2.0.0 - Enhanced Edition");
        versionLabel.setFont(UITheme.FONT_SMALL);
        versionLabel.setForeground(new Color(150, 180, 210));
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(statusLabel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(progressBar);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(versionLabel);
        contentPanel.add(Box.createVerticalGlue());
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        setContentPane(mainPanel);
    }
    
    /**
     * Update status message
     */
    public void updateStatus(String message) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(message);
            progress = Math.min(progress + 15, 90);
            progressBar.setValue(progress);
            progressBar.setString(progress + "%");
            repaint();
        });
    }
    
    /**
     * Complete loading
     */
    public void complete() {
        SwingUtilities.invokeLater(() -> {
            progressBar.setValue(100);
            progressBar.setString("100%");
            statusLabel.setText("Ready!");
            repaint();
        });
    }
}
