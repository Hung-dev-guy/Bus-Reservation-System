package com.hungdev.busbookingsystem.gui;

import com.hungdev.busbookingsystem.model.*;
import com.hungdev.busbookingsystem.service.AdminService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Admin Bus Management Frame
 */
public class AdminBusManagementFrame extends JFrame {

    private AdminDashboardFrame parentFrame;
    private AdminService adminService;
    private JTable busesTable;
    private DefaultTableModel tableModel;
    private List<Bus> buses;

    public AdminBusManagementFrame(User user, AdminDashboardFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.adminService = AdminService.getInstance();
        initializeUI();
        loadBuses();
    }

    private void initializeUI() {
        setTitle("Bus Management");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parentFrame);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 245, 245));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 245, 245));
        
        JLabel titleLabel = new JLabel("Bus Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(155, 89, 182));

        JButton addButton = createStyledButton("+ Add Bus", new Color(46, 204, 113));
        addButton.addActionListener(e -> showAddBusDialog());

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(addButton, BorderLayout.EAST);

        // Table
        String[] columnNames = {"ID", "License Plate", "Model", "Total Seats", "Manufacture Year"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        busesTable = new JTable(tableModel);
        busesTable.setFont(new Font("Arial", Font.PLAIN, 13));
        busesTable.setRowHeight(30);
        busesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        busesTable.getTableHeader().setBackground(new Color(155, 89, 182));
        busesTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(busesTable);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(245, 245, 245));

        JButton refreshButton = createStyledButton("Refresh", new Color(149, 165, 166));
        refreshButton.addActionListener(e -> loadBuses());

        buttonPanel.add(refreshButton);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadBuses() {
        SwingWorker<List<Bus>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Bus> doInBackground() {
                return adminService.getAllBuses();
            }

            @Override
            protected void done() {
                try {
                    buses = get();
                    displayBuses();
                } catch (Exception e) {
                    showError("Failed to load buses: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void displayBuses() {
        tableModel.setRowCount(0);
        for (Bus bus : buses) {
            Object[] row = {
                bus.getId(),
                bus.getLicensePlate(),
                bus.getModel(),
                bus.getTotalSeats(),
                bus.getManufactureYear()
            };
            tableModel.addRow(row);
        }
    }

    private void showAddBusDialog() {
        JDialog dialog = new JDialog(this, "Add New Bus", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField licensePlateField = new JTextField();
        JTextField modelField = new JTextField();
        JTextField totalSeatsField = new JTextField();
        JTextField manufactureYearField = new JTextField();

        panel.add(new JLabel("License Plate:"));
        panel.add(licensePlateField);
        panel.add(new JLabel("Model:"));
        panel.add(modelField);
        panel.add(new JLabel("Total Seats:"));
        panel.add(totalSeatsField);
        panel.add(new JLabel("Manufacture Year:"));
        panel.add(manufactureYearField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = createStyledButton("Save", new Color(46, 204, 113));
        JButton cancelButton = createStyledButton("Cancel", new Color(149, 165, 166));

        saveButton.addActionListener(e -> {
            try {
                String licensePlate = licensePlateField.getText().trim();
                String model = modelField.getText().trim();
                String totalSeatsStr = totalSeatsField.getText().trim();
                String manufactureYearStr = manufactureYearField.getText().trim();

                if (licensePlate.isEmpty() || model.isEmpty() || 
                    totalSeatsStr.isEmpty() || manufactureYearStr.isEmpty()) {
                    showError("All fields are required!");
                    return;
                }

                Integer totalSeats = Integer.parseInt(totalSeatsStr);
                Integer manufactureYear = Integer.parseInt(manufactureYearStr);

                if (totalSeats <= 0) {
                    showError("Total seats must be greater than 0!");
                    return;
                }

                if (manufactureYear < 1900 || manufactureYear > 2100) {
                    showError("Invalid manufacture year!");
                    return;
                }

                adminService.createBus(licensePlate, model, totalSeats, manufactureYear);
                showInfo("Bus created successfully!");
                dialog.dispose();
                loadBuses();
            } catch (NumberFormatException ex) {
                showError("Invalid number format!");
            } catch (Exception ex) {
                showError("Failed to create bus: " + ex.getMessage());
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
