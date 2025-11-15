package com.hungdev.busbookingsystem.gui;

import com.hungdev.busbookingsystem.model.*;
import com.hungdev.busbookingsystem.service.AdminService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Date;

/**
 * Admin Trip Management Frame  
 */
public class AdminTripManagementFrame extends JFrame {

    private AdminDashboardFrame parentFrame;
    private AdminService adminService;
    private JTable tripsTable;
    private DefaultTableModel tableModel;
    private List<Trip> trips;

    public AdminTripManagementFrame(User user, AdminDashboardFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.adminService = AdminService.getInstance();
        initializeUI();
        loadTrips();
    }

    private void initializeUI() {
        setTitle("Trip Management");
        setSize(1400, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parentFrame);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 245, 245));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 245, 245));
        
        JLabel titleLabel = new JLabel("Trip Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(52, 152, 219));

        JButton addButton = createStyledButton("+ Add Trip", new Color(46, 204, 113));
        addButton.addActionListener(e -> showAddTripDialog());

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(addButton, BorderLayout.EAST);

        // Table
        String[] columnNames = {"ID", "Route", "Bus", "Departure", "Arrival", "Price", "Available Seats"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tripsTable = new JTable(tableModel);
        tripsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        tripsTable.setRowHeight(30);
        tripsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tripsTable.getTableHeader().setBackground(new Color(52, 152, 219));
        tripsTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(tripsTable);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(245, 245, 245));

        JButton editButton = createStyledButton("Edit", new Color(52, 152, 219));
        JButton deleteButton = createStyledButton("Delete", new Color(231, 76, 60));
        JButton refreshButton = createStyledButton("Refresh", new Color(149, 165, 166));

        editButton.addActionListener(e -> editSelectedTrip());
        deleteButton.addActionListener(e -> deleteSelectedTrip());
        refreshButton.addActionListener(e -> loadTrips());

        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadTrips() {
        SwingWorker<List<Trip>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Trip> doInBackground() {
                return adminService.getAllTrips();
            }

            @Override
            protected void done() {
                try {
                    trips = get();
                    displayTrips();
                } catch (Exception e) {
                    showError("Failed to load trips: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void displayTrips() {
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (Trip trip : trips) {
            Object[] row = {
                trip.getId(),
                trip.getRoute().getStartLocation().getName() + " → " + trip.getRoute().getEndLocation().getName(),
                trip.getBus().getLicensePlate(),
                trip.getDepartureTime().format(formatter),
                trip.getArrivalTime().format(formatter),
                "$" + trip.getPricePerSeat(),
                trip.getAvailableSeats()
            };
            tableModel.addRow(row);
        }
    }

    private void showAddTripDialog() {
        JDialog dialog = new JDialog(this, "Add New Trip", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Load routes and buses
        List<Route> routes = adminService.getAllRoutes();
        List<Bus> buses = adminService.getAllBuses();

        JComboBox<String> routeCombo = new JComboBox<>();
        for (Route route : routes) {
            routeCombo.addItem(route.getStartLocation().getName() + " → " + route.getEndLocation().getName());
        }

        JComboBox<String> busCombo = new JComboBox<>();
        for (Bus bus : buses) {
            busCombo.addItem(bus.getLicensePlate() + " (" + bus.getTotalSeats() + " seats)");
        }

        JSpinner departureSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor departureEditor = new JSpinner.DateEditor(departureSpinner, "dd/MM/yyyy HH:mm");
        departureSpinner.setEditor(departureEditor);

        JSpinner arrivalSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor arrivalEditor = new JSpinner.DateEditor(arrivalSpinner, "dd/MM/yyyy HH:mm");
        arrivalSpinner.setEditor(arrivalEditor);

        JTextField priceField = new JTextField();

        panel.add(new JLabel("Route:"));
        panel.add(routeCombo);
        panel.add(new JLabel("Bus:"));
        panel.add(busCombo);
        panel.add(new JLabel("Departure Time:"));
        panel.add(departureSpinner);
        panel.add(new JLabel("Arrival Time:"));
        panel.add(arrivalSpinner);
        panel.add(new JLabel("Price per Seat:"));
        panel.add(priceField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = createStyledButton("Save", new Color(46, 204, 113));
        JButton cancelButton = createStyledButton("Cancel", new Color(149, 165, 166));

        saveButton.addActionListener(e -> {
            try {
                int routeIndex = routeCombo.getSelectedIndex();
                int busIndex = busCombo.getSelectedIndex();
                
                if (routeIndex == -1 || busIndex == -1) {
                    showError("Please select route and bus!");
                    return;
                }

                Long routeId = routes.get(routeIndex).getId();
                Long busId = buses.get(busIndex).getId();
                
                Date departureDate = (Date) departureSpinner.getValue();
                Date arrivalDate = (Date) arrivalSpinner.getValue();
                
                OffsetDateTime departureTime = OffsetDateTime.ofInstant(
                    departureDate.toInstant(), ZoneId.systemDefault());
                OffsetDateTime arrivalTime = OffsetDateTime.ofInstant(
                    arrivalDate.toInstant(), ZoneId.systemDefault());
                
                BigDecimal price = new BigDecimal(priceField.getText().trim());

                adminService.createTrip(routeId, busId, departureTime, arrivalTime, price);
                showInfo("Trip created successfully!");
                dialog.dispose();
                loadTrips();
            } catch (Exception ex) {
                showError("Failed to create trip: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void editSelectedTrip() {
        int selectedRow = tripsTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a trip to edit!");
            return;
        }

        Trip trip = trips.get(selectedRow);
        
        JDialog dialog = new JDialog(this, "Edit Trip", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JSpinner departureSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor departureEditor = new JSpinner.DateEditor(departureSpinner, "dd/MM/yyyy HH:mm");
        departureSpinner.setEditor(departureEditor);
        departureSpinner.setValue(Date.from(trip.getDepartureTime().toInstant()));

        JSpinner arrivalSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor arrivalEditor = new JSpinner.DateEditor(arrivalSpinner, "dd/MM/yyyy HH:mm");
        arrivalSpinner.setEditor(arrivalEditor);
        arrivalSpinner.setValue(Date.from(trip.getArrivalTime().toInstant()));

        JTextField priceField = new JTextField(trip.getPricePerSeat().toString());

        panel.add(new JLabel("Departure Time:"));
        panel.add(departureSpinner);
        panel.add(new JLabel("Arrival Time:"));
        panel.add(arrivalSpinner);
        panel.add(new JLabel("Price per Seat:"));
        panel.add(priceField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = createStyledButton("Save", new Color(46, 204, 113));
        JButton cancelButton = createStyledButton("Cancel", new Color(149, 165, 166));

        saveButton.addActionListener(e -> {
            try {
                Date departureDate = (Date) departureSpinner.getValue();
                Date arrivalDate = (Date) arrivalSpinner.getValue();
                
                OffsetDateTime departureTime = OffsetDateTime.ofInstant(
                    departureDate.toInstant(), ZoneId.systemDefault());
                OffsetDateTime arrivalTime = OffsetDateTime.ofInstant(
                    arrivalDate.toInstant(), ZoneId.systemDefault());
                
                BigDecimal price = new BigDecimal(priceField.getText().trim());

                adminService.updateTrip(trip.getId(), departureTime, arrivalTime, price);
                showInfo("Trip updated successfully!");
                dialog.dispose();
                loadTrips();
            } catch (Exception ex) {
                showError("Failed to update trip: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void deleteSelectedTrip() {
        int selectedRow = tripsTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a trip to delete!");
            return;
        }

        Trip trip = trips.get(selectedRow);
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this trip?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                adminService.deleteTrip(trip.getId());
                showInfo("Trip deleted successfully!");
                loadTrips();
            } catch (Exception e) {
                showError("Failed to delete trip: " + e.getMessage());
            }
        }
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
