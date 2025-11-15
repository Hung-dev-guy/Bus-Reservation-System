package com.hungdev.busbookingsystem.gui;

import com.hungdev.busbookingsystem.model.User;
import com.hungdev.busbookingsystem.service.AdminService;

import javax.swing.*;
import java.awt.*;

/**
 * Admin Dashboard Frame
 */
public class AdminDashboardFrame extends JFrame {

    private User currentUser;
    private AdminService adminService;
    private JPanel contentPanel;

    public AdminDashboardFrame(User user) {
        this.currentUser = user;
        this.adminService = AdminService.getInstance();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Admin Dashboard");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));

        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Sidebar
        JPanel sidebarPanel = createSidebarPanel();
        mainPanel.add(sidebarPanel, BorderLayout.WEST);

        // Content area
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        showDashboardHome();
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(231, 76, 60));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel userLabel = new JLabel("Admin: " + currentUser.getUsername());
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(userLabel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(new Color(44, 62, 80));
        sidebarPanel.setPreferredSize(new Dimension(200, 0));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        addMenuButton(sidebarPanel, "🏠 Dashboard", () -> showDashboardHome());
        addMenuButton(sidebarPanel, "👥 Users", () -> openUserManagement());
        addMenuButton(sidebarPanel, "🚌 Trips", () -> openTripManagement());
        addMenuButton(sidebarPanel, "🛣️ Routes", () -> openRouteManagement());
        addMenuButton(sidebarPanel, "🚍 Buses", () -> openBusManagement());
        addMenuButton(sidebarPanel, "📊 Statistics", () -> showStatistics());

        return sidebarPanel;
    }

    private void addMenuButton(JPanel panel, String text, Runnable action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(44, 62, 80));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(200, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 73, 94));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(44, 62, 80));
            }
        });

        button.addActionListener(e -> action.run());
        panel.add(button);
    }

    private void showDashboardHome() {
        contentPanel.removeAll();
        
        JLabel titleLabel = new JLabel("Welcome to Admin Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(52, 73, 94));

        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Load statistics
        Object[] bookingStats = adminService.getBookingStats();
        Object[] userStats = adminService.getUserStats();

        statsPanel.add(createStatCard("Total Bookings", bookingStats[0].toString(), new Color(52, 152, 219)));
        statsPanel.add(createStatCard("Pending Bookings", bookingStats[1].toString(), new Color(241, 196, 15)));
        statsPanel.add(createStatCard("Total Users", userStats[0].toString(), new Color(46, 204, 113)));
        statsPanel.add(createStatCard("Active Users", userStats[1].toString(), new Color(155, 89, 182)));

        JPanel mainContent = new JPanel(new BorderLayout(0, 20));
        mainContent.setBackground(Color.WHITE);
        mainContent.add(titleLabel, BorderLayout.NORTH);
        mainContent.add(statsPanel, BorderLayout.CENTER);

        contentPanel.add(mainContent, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        titleLabel.setForeground(Color.WHITE);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 36));
        valueLabel.setForeground(Color.WHITE);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private void openUserManagement() {
        AdminUserManagementFrame userFrame = new AdminUserManagementFrame(currentUser, this);
        userFrame.setVisible(true);
    }

    private void openTripManagement() {
        AdminTripManagementFrame tripFrame = new AdminTripManagementFrame(currentUser, this);
        tripFrame.setVisible(true);
    }

    private void openRouteManagement() {
        AdminRouteManagementFrame routeFrame = new AdminRouteManagementFrame(currentUser, this);
        routeFrame.setVisible(true);
    }

    private void openBusManagement() {
        AdminBusManagementFrame busFrame = new AdminBusManagementFrame(currentUser, this);
        busFrame.setVisible(true);
    }

    private void showStatistics() {
        contentPanel.removeAll();
        
        JLabel titleLabel = new JLabel("Statistics & Reports");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(52, 73, 94));

        JTextArea statsArea = new JTextArea();
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        statsArea.setText("Detailed statistics will be displayed here...\n\n");
        
        Object[] bookingStats = adminService.getBookingStats();
        Object[] userStats = adminService.getUserStats();
        
        statsArea.append("=== Booking Statistics ===\n");
        statsArea.append("Total Bookings: " + bookingStats[0] + "\n");
        statsArea.append("Pending Bookings: " + bookingStats[1] + "\n");
        statsArea.append("Confirmed Bookings: " + bookingStats[2] + "\n\n");
        
        statsArea.append("=== User Statistics ===\n");
        statsArea.append("Total Users: " + userStats[0] + "\n");
        statsArea.append("Active Users: " + userStats[1] + "\n");

        JScrollPane scrollPane = new JScrollPane(statsArea);

        JPanel mainContent = new JPanel(new BorderLayout(0, 20));
        mainContent.setBackground(Color.WHITE);
        mainContent.add(titleLabel, BorderLayout.NORTH);
        mainContent.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(mainContent, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
}
