package com.hungdev.busbookingsystem.gui;

import com.hungdev.busbookingsystem.model.Location;
import com.hungdev.busbookingsystem.model.Trip;
import com.hungdev.busbookingsystem.model.User;
import com.hungdev.busbookingsystem.service.TripService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Search and view available bus trips
 */
public class SearchTripsFrame extends JFrame {

    private User currentUser;
    private MainFrame parentFrame;
    private TripService tripService;

    private JComboBox<LocationItem> originComboBox;
    private JComboBox<LocationItem> destinationComboBox;
    private JSpinner dateSpinner;
    private JButton searchButton;
    private JButton swapButton;
    private JTable tripsTable;
    private DefaultTableModel tableModel;
    private List<Trip> currentTrips;

    public SearchTripsFrame(User user, MainFrame parentFrame) {
        this.currentUser = user;
        this.parentFrame = parentFrame;
        this.tripService = TripService.getInstance();
        initializeUI();
        loadLocations();
    }

    private void initializeUI() {
        setTitle("Search Bus Trips");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parentFrame);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 245, 245));

        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Search panel
        JPanel searchPanel = createSearchPanel();
        mainPanel.add(searchPanel, BorderLayout.CENTER);

        // Results table
        JPanel resultsPanel = createResultsPanel();
        mainPanel.add(resultsPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(new Color(245, 245, 245));

        JLabel titleLabel = new JLabel("Search Bus Trips");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(52, 152, 219));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Find and book your next journey");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitleLabel);
        headerPanel.add(Box.createVerticalStrut(15));

        return headerPanel;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Origin
        JLabel originLabel = new JLabel("From:");
        originLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        searchPanel.add(originLabel, gbc);

        originComboBox = new JComboBox<>();
        originComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        originComboBox.setPreferredSize(new Dimension(250, 35));
        gbc.gridx = 1;
        gbc.gridy = 0;
        searchPanel.add(originComboBox, gbc);

        // Swap button
        swapButton = new JButton("⇄");
        swapButton.setFont(new Font("Arial", Font.BOLD, 20));
        swapButton.setPreferredSize(new Dimension(60, 35));
        swapButton.setToolTipText("Swap origin and destination");
        swapButton.addActionListener(e -> swapLocations());
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        searchPanel.add(swapButton, gbc);

        // Destination
        JLabel destinationLabel = new JLabel("To:");
        destinationLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        searchPanel.add(destinationLabel, gbc);

        destinationComboBox = new JComboBox<>();
        destinationComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        destinationComboBox.setPreferredSize(new Dimension(250, 35));
        gbc.gridx = 4;
        gbc.gridy = 0;
        searchPanel.add(destinationComboBox, gbc);

        // Date
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        searchPanel.add(dateLabel, gbc);

        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setFont(new Font("Arial", Font.PLAIN, 14));
        dateSpinner.setPreferredSize(new Dimension(250, 35));
        gbc.gridx = 1;
        gbc.gridy = 1;
        searchPanel.add(dateSpinner, gbc);

        // Search button
        searchButton = createStyledButton("Search Trips", new Color(52, 152, 219));
        searchButton.addActionListener(e -> searchTrips());
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        searchPanel.add(searchButton, gbc);

        return searchPanel;
    }

    private JPanel createResultsPanel() {
        JPanel resultsPanel = new JPanel(new BorderLayout(10, 10));
        resultsPanel.setBackground(new Color(245, 245, 245));
        resultsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JLabel resultsLabel = new JLabel("Available Trips");
        resultsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        resultsLabel.setForeground(new Color(52, 73, 94));

        // Table
        String[] columnNames = {"Trip ID", "Route", "Departure", "Arrival", "Bus", "Available Seats", "Price", "Action"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Only action column editable
            }
        };

        tripsTable = new JTable(tableModel);
        tripsTable.setFont(new Font("Arial", Font.PLAIN, 13));
        tripsTable.setRowHeight(40);
        tripsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tripsTable.getTableHeader().setBackground(new Color(52, 152, 219));
        tripsTable.getTableHeader().setForeground(Color.WHITE);

        // Add button column
        tripsTable.getColumn("Action").setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JButton button = new JButton("Book Now");
            button.setBackground(new Color(46, 204, 113));
            button.setForeground(Color.WHITE);
            button.setOpaque(true);
            button.setBorderPainted(false);
            return button;
        });

        tripsTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(tripsTable);
        scrollPane.setPreferredSize(new Dimension(950, 300));

        resultsPanel.add(resultsLabel, BorderLayout.NORTH);
        resultsPanel.add(scrollPane, BorderLayout.CENTER);

        return resultsPanel;
    }

    private void loadLocations() {
        SwingWorker<List<Location>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Location> doInBackground() {
                return tripService.getAllLocations();
            }

            @Override
            protected void done() {
                try {
                    List<Location> locations = get();
                    originComboBox.removeAllItems();
                    destinationComboBox.removeAllItems();

                    for (Location location : locations) {
                        LocationItem item = new LocationItem(location);
                        originComboBox.addItem(item);
                        destinationComboBox.addItem(item);
                    }
                } catch (Exception e) {
                    showError("Failed to load locations: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void swapLocations() {
        Object temp = originComboBox.getSelectedItem();
        originComboBox.setSelectedItem(destinationComboBox.getSelectedItem());
        destinationComboBox.setSelectedItem(temp);
    }

    private void searchTrips() {
        LocationItem origin = (LocationItem) originComboBox.getSelectedItem();
        LocationItem destination = (LocationItem) destinationComboBox.getSelectedItem();

        if (origin == null || destination == null) {
            showError("Please select origin and destination");
            return;
        }

        if (origin.getLocation().getId().equals(destination.getLocation().getId())) {
            showError("Origin and destination cannot be the same");
            return;
        }

        java.util.Date selectedDate = (java.util.Date) dateSpinner.getValue();
        LocalDate searchDate = LocalDate.ofInstant(selectedDate.toInstant(), java.time.ZoneId.systemDefault());

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        searchButton.setEnabled(false);

        SwingWorker<List<Trip>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Trip> doInBackground() {
                return tripService.searchTrips(origin.getLocation().getId(),
                        destination.getLocation().getId(), searchDate);
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                searchButton.setEnabled(true);

                try {
                    currentTrips = get();
                    displayTrips(currentTrips);
                } catch (Exception e) {
                    showError("Search failed: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void displayTrips(List<Trip> trips) {
        tableModel.setRowCount(0);

        if (trips.isEmpty()) {
            showInfo("No trips found for the selected route and date");
            return;
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Trip trip : trips) {
            Object[] row = {
                trip.getId(),
                trip.getRoute().getStartLocation().getName() + " → " + trip.getRoute().getEndLocation().getName(),
                trip.getDepartureTime().toLocalDateTime().format(dateFormatter),
                trip.getArrivalTime().toLocalDateTime().format(dateFormatter),
                trip.getBus().getLicensePlate(),
                trip.getAvailableSeats() + " / " + trip.getBus().getTotalSeats(),
                "$" + trip.getPricePerSeat(),
                "Book"
            };
            tableModel.addRow(row);
        }
    }

    private void openBookingFrame(Trip trip) {
        BookingFrame bookingFrame = new BookingFrame(currentUser, trip, this);
        bookingFrame.setVisible(true);
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(180, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    // Helper class for Location combo box
    private static class LocationItem {
        private Location location;

        public LocationItem(Location location) {
            this.location = location;
        }

        public Location getLocation() {
            return location;
        }

        @Override
        public String toString() {
            return location.getName() + " (" + location.getCity() + ")";
        }
    }

    // Button editor for table
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private int row;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.row = row;
            label = (value == null) ? "Book" : value.toString();
            button.setText(label);
            button.setBackground(new Color(46, 204, 113));
            button.setForeground(Color.WHITE);
            button.setBorderPainted(false);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed && currentTrips != null && row < currentTrips.size()) {
                Trip selectedTrip = currentTrips.get(row);
                SwingUtilities.invokeLater(() -> openBookingFrame(selectedTrip));
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}
