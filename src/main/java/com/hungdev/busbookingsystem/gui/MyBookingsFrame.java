package com.hungdev.busbookingsystem.gui;

import com.hungdev.busbookingsystem.model.Booking;
import com.hungdev.busbookingsystem.model.User;
import com.hungdev.busbookingsystem.service.BookingService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * View user's bookings
 */
public class MyBookingsFrame extends JFrame {

    private User currentUser;
    private MainFrame parentFrame;
    private BookingService bookingService;
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private List<Booking> bookings;

    public MyBookingsFrame(User user, MainFrame parentFrame) {
        this.currentUser = user;
        this.parentFrame = parentFrame;
        this.bookingService = BookingService.getInstance();
        initializeUI();
        loadBookings();
    }

    private void initializeUI() {
        setTitle("My Bookings");
        setSize(1100, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parentFrame);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 245, 245));

        // Header
        JLabel titleLabel = new JLabel("My Bookings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(46, 204, 113));

        // Table
        String[] columnNames = {"ID", "Route", "Departure", "Bus", "Seats", "Amount", "Status", "Booking Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookingsTable = new JTable(tableModel);
        bookingsTable.setFont(new Font("Arial", Font.PLAIN, 13));
        bookingsTable.setRowHeight(35);
        bookingsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        bookingsTable.getTableHeader().setBackground(new Color(46, 204, 113));
        bookingsTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(bookingsTable);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void loadBookings() {
        SwingWorker<List<Booking>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Booking> doInBackground() {
                return bookingService.getUserBookings(currentUser.getId());
            }

            @Override
            protected void done() {
                try {
                    bookings = get();
                    displayBookings();
                } catch (Exception e) {
                    showError("Failed to load bookings: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void displayBookings() {
        tableModel.setRowCount(0);

        if (bookings.isEmpty()) {
            showInfo("You have no bookings yet");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Booking booking : bookings) {
            Object[] row = {
                booking.getId(),
                booking.getTrip().getRoute().getStartLocation().getName() + " → " +
                        booking.getTrip().getRoute().getEndLocation().getName(),
                booking.getTrip().getDepartureTime().toLocalDateTime().format(formatter),
                booking.getTrip().getBus().getLicensePlate(),
                booking.getNumberOfSeats(),
                "$" + booking.getTotalAmount(),
                booking.getStatus(),
                booking.getBookingTime().toLocalDateTime().format(formatter)
            };
            tableModel.addRow(row);
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
}
