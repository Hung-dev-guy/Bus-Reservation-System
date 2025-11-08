package com.hungdev.busbookingsystem.gui;

import com.hungdev.busbookingsystem.model.User;
import com.hungdev.busbookingsystem.util.JPAUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window after successful login
 */
public class MainFrame extends JFrame {

    private User currentUser;
    private JLabel welcomeLabel;
    private JButton logoutButton;

    public MainFrame(User user) {
        this.currentUser = user;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Bus Booking System - Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));

        // Header panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content panel
        JPanel contentPanel = createContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);

        // Add window listener to cleanup on close
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                JPAUtil.shutdown();
                System.exit(0);
            }
        });
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Welcome message
        welcomeLabel = new JLabel("Welcome, " + currentUser.getFirstName() + " " + currentUser.getLastName());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);

        // User info
        JLabel userInfoLabel = new JLabel("Role: " + currentUser.getRole() + " | " + currentUser.getEmail());
        userInfoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        userInfoLabel.setForeground(new Color(236, 240, 241));

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.add(welcomeLabel, BorderLayout.NORTH);
        leftPanel.add(userInfoLabel, BorderLayout.SOUTH);

        // Logout button
        logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 13));
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> handleLogout());

        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(new Color(245, 245, 245));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Dashboard title
        JLabel dashboardTitle = new JLabel("Dashboard");
        dashboardTitle.setFont(new Font("Arial", Font.BOLD, 24));
        dashboardTitle.setForeground(new Color(52, 73, 94));

        // Main content area
        JPanel centerPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        centerPanel.setBackground(new Color(245, 245, 245));

        // Add dashboard cards
        centerPanel.add(createDashboardCard("Search Trips", "Find and book bus tickets",
                new Color(52, 152, 219), "🔍"));
        centerPanel.add(createDashboardCard("My Bookings", "View your ticket bookings",
                new Color(46, 204, 113), "📋"));
        centerPanel.add(createDashboardCard("Profile", "Manage your account",
                new Color(155, 89, 182), "👤"));
        centerPanel.add(createDashboardCard("Help & Support", "Get assistance",
                new Color(241, 196, 15), "❓"));

        contentPanel.add(dashboardTitle, BorderLayout.NORTH);
        contentPanel.add(centerPanel, BorderLayout.CENTER);

        return contentPanel;
    }

    private JPanel createDashboardCard(String title, String description, Color color, String emoji) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Emoji label
        JLabel emojiLabel = new JLabel(emoji);
        emojiLabel.setFont(new Font("Arial", Font.PLAIN, 48));
        emojiLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Text panel
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(color);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        descLabel.setForeground(Color.GRAY);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(descLabel);

        card.add(emojiLabel, BorderLayout.NORTH);
        card.add(textPanel, BorderLayout.CENTER);

        // Add hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(249, 249, 249));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(Color.WHITE);
            }

            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JOptionPane.showMessageDialog(MainFrame.this,
                        "Feature coming soon: " + title,
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        return card;
    }

    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            // Cleanup and return to login
            JPAUtil.shutdown();
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            this.dispose();
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }
}
