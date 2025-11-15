package com.hungdev.busbookingsystem.gui;

import com.hungdev.busbookingsystem.model.Trip;
import com.hungdev.busbookingsystem.model.User;
import com.hungdev.busbookingsystem.service.TripService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BookingFlowFrame extends JFrame {

    private User currentUser;
    private MainFrame parentFrame;
    private TripService tripService;
    private JTable tripsTable;
    private DefaultTableModel tableModel;
    private List<Trip> trips;

    public BookingFlowFrame(User user, MainFrame parentFrame) {
        this.currentUser = user;
        this.parentFrame = parentFrame;
        this.tripService = TripService.getInstance();
        initializeUI();
        loadTrips();
    }

    private void initializeUI() {
        setTitle("Available Trips");
        setSize(1100, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parentFrame);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 245, 245));

        // Header
        JLabel titleLabel = new JLabel("Available Trips");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(46, 204, 113));

        // Table
        String[] columnNames = {"ID", "Route", "Departure", "Bus", "Available Seats", "Price"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tripsTable = new JTable(tableModel);
        tripsTable.setFont(new Font("Arial", Font.PLAIN, 13));
        tripsTable.setRowHeight(35);
        tripsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tripsTable.getTableHeader().setBackground(new Color(46, 204, 113));
        tripsTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(tripsTable);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton nextButton = new JButton("Next");
        JButton closeButton = new JButton("Close");

        nextButton.addActionListener(e -> openBookingFrame());
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(nextButton);
        buttonPanel.add(closeButton);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadTrips() {
        SwingWorker<List<Trip>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Trip> doInBackground() {
                return tripService.getUpcomingTrips(100);
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

        if (trips.isEmpty()) {
            showInfo("No upcoming trips available.");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Trip trip : trips) {
            Object[] row = {
                trip.getId(),
                trip.getRoute().getStartLocation().getName() + " → " +
                        trip.getRoute().getEndLocation().getName(),
                trip.getDepartureTime().toLocalDateTime().format(formatter),
                trip.getBus().getLicensePlate(),
                trip.getAvailableSeats(),
                "$" + trip.getPricePerSeat()
            };
            tableModel.addRow(row);
        }
    }

    private void openBookingFrame() {
        int selectedRow = tripsTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a trip to book.");
            return;
        }

        Trip selectedTrip = trips.get(selectedRow);
        BookingFrame bookingFrame = new BookingFrame(currentUser, selectedTrip, null);
        bookingFrame.setVisible(true);
        dispose();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
}
