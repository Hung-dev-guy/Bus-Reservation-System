package com.hungdev.busbookingsystem.gui;

import com.hungdev.busbookingsystem.model.*;
import com.hungdev.busbookingsystem.service.AdminService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Admin Route Management Frame
 */
public class AdminRouteManagementFrame extends JFrame {

    private AdminDashboardFrame parentFrame;
    private AdminService adminService;
    private JTable routesTable;
    private DefaultTableModel tableModel;
    private List<Route> routes;

    public AdminRouteManagementFrame(User user, AdminDashboardFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.adminService = AdminService.getInstance();
        initializeUI();
        loadRoutes();
    }

    private void initializeUI() {
        setTitle("Route Management");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parentFrame);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 245, 245));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 245, 245));
        
        JLabel titleLabel = new JLabel("Route Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(230, 126, 34));

        JButton addButton = createStyledButton("+ Add Route", new Color(46, 204, 113));
        addButton.addActionListener(e -> showAddRouteDialog());

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(addButton, BorderLayout.EAST);

        // Table
        String[] columnNames = {"ID", "Start Location", "End Location", "Distance (km)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        routesTable = new JTable(tableModel);
        routesTable.setFont(new Font("Arial", Font.PLAIN, 13));
        routesTable.setRowHeight(30);
        routesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        routesTable.getTableHeader().setBackground(new Color(230, 126, 34));
        routesTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(routesTable);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(245, 245, 245));

        JButton refreshButton = createStyledButton("Refresh", new Color(149, 165, 166));
        refreshButton.addActionListener(e -> loadRoutes());

        buttonPanel.add(refreshButton);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadRoutes() {
        SwingWorker<List<Route>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Route> doInBackground() {
                return adminService.getAllRoutes();
            }

            @Override
            protected void done() {
                try {
                    routes = get();
                    displayRoutes();
                } catch (Exception e) {
                    showError("Failed to load routes: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void displayRoutes() {
        tableModel.setRowCount(0);
        for (Route route : routes) {
            Object[] row = {
                route.getId(),
                route.getStartLocation().getName(),
                route.getEndLocation().getName(),
                route.getDistanceKm()
            };
            tableModel.addRow(row);
        }
    }

    private void showAddRouteDialog() {
        JDialog dialog = new JDialog(this, "Add New Route", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Load locations
        List<Location> locations = adminService.getAllLocations();

        JComboBox<String> startLocationCombo = new JComboBox<>();
        JComboBox<String> endLocationCombo = new JComboBox<>();
        
        for (Location location : locations) {
            startLocationCombo.addItem(location.getName());
            endLocationCombo.addItem(location.getName());
        }

        JTextField distanceField = new JTextField();

        panel.add(new JLabel("Start Location:"));
        panel.add(startLocationCombo);
        panel.add(new JLabel("End Location:"));
        panel.add(endLocationCombo);
        panel.add(new JLabel("Distance (km):"));
        panel.add(distanceField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = createStyledButton("Save", new Color(46, 204, 113));
        JButton cancelButton = createStyledButton("Cancel", new Color(149, 165, 166));

        saveButton.addActionListener(e -> {
            try {
                int startIndex = startLocationCombo.getSelectedIndex();
                int endIndex = endLocationCombo.getSelectedIndex();
                
                if (startIndex == -1 || endIndex == -1) {
                    showError("Please select both locations!");
                    return;
                }

                if (startIndex == endIndex) {
                    showError("Start and end locations must be different!");
                    return;
                }

                Long startLocationId = locations.get(startIndex).getId();
                Long endLocationId = locations.get(endIndex).getId();
                Double distance = Double.parseDouble(distanceField.getText().trim());

                if (distance <= 0) {
                    showError("Distance must be greater than 0!");
                    return;
                }

                adminService.createRoute(startLocationId, endLocationId, distance);
                showInfo("Route created successfully!");
                dialog.dispose();
                loadRoutes();
            } catch (NumberFormatException ex) {
                showError("Invalid distance value!");
            } catch (Exception ex) {
                showError("Failed to create route: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return button;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}
