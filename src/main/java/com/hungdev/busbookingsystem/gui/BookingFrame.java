package com.hungdev.busbookingsystem.gui;

import com.hungdev.busbookingsystem.model.Booking;
import com.hungdev.busbookingsystem.model.Trip;
import com.hungdev.busbookingsystem.model.User;
import com.hungdev.busbookingsystem.service.BookingService;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Seat selection and booking creation
 */
public class BookingFrame extends JFrame {

    private User currentUser;
    private Trip trip;
    private JFrame parentFrame;
    private BookingService bookingService;

    private JPanel seatsPanel;
    private JLabel selectedSeatsLabel;
    private JLabel totalPriceLabel;
    private JButton confirmButton;
    private JButton cancelButton;

    private List<String> selectedSeats;
    private List<String> bookedSeats;

    public BookingFrame(User user, Trip trip, JFrame parentFrame) {
        this.currentUser = user;
        this.trip = trip;
        this.parentFrame = parentFrame;
        this.bookingService = BookingService.getInstance();
        this.selectedSeats = new ArrayList<>();
        initializeUI();
        loadBookedSeats();
    }

    private void initializeUI() {
        setTitle("Book Your Seats - Trip #" + trip.getId());
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parentFrame);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 245, 245));

        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Seats
        JScrollPane seatsScroll = new JScrollPane(createSeatsPanel());
        seatsScroll.setBorder(null);
        mainPanel.add(seatsScroll, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

        JLabel titleLabel = new JLabel("Select Your Seats");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(46, 204, 113));

        JLabel routeLabel = new JLabel(
                trip.getRoute().getStartLocation().getName() + " → " + trip.getRoute().getEndLocation().getName()
        );
        routeLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel detailsLabel = new JLabel(String.format(
                "<html>Departure: %s<br>Bus: %s | Available: %d seats | Price: $%s per seat</html>",
                trip.getDepartureTime().toLocalDateTime().format(formatter),
                trip.getBus().getLicensePlate(),
                trip.getAvailableSeats(),
                trip.getPricePerSeat()
        ));
        detailsLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        detailsLabel.setForeground(Color.GRAY);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        leftPanel.add(titleLabel);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(routeLabel);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(detailsLabel);

        headerPanel.add(leftPanel, BorderLayout.WEST);

        return headerPanel;
    }

    private JPanel createSeatsPanel() {
        seatsPanel = new JPanel(new GridBagLayout());
        seatsPanel.setBackground(Color.WHITE);
        seatsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Legend
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        legendPanel.setOpaque(false);
        legendPanel.add(createLegendItem("Available", new Color(46, 204, 113)));
        legendPanel.add(createLegendItem("Selected", new Color(52, 152, 219)));
        legendPanel.add(createLegendItem("Booked", new Color(149, 165, 166)));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 6;
        seatsPanel.add(legendPanel, gbc);

        // Driver seat
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        JLabel driverLabel = new JLabel("🚗 Driver");
        driverLabel.setFont(new Font("Arial", Font.BOLD, 14));
        seatsPanel.add(driverLabel, gbc);

        // Generate seat buttons (4 columns, multiple rows)
        int totalSeats = trip.getBus().getTotalSeats();
        int seatsPerRow = 4;
        int rows = (int) Math.ceil(totalSeats / (double) seatsPerRow);

        int seatNumber = 1;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < seatsPerRow; col++) {
                if (seatNumber > totalSeats) break;

                String seatNum = String.format("%02d", seatNumber);
                JToggleButton seatButton = createSeatButton(seatNum);

                gbc.gridx = col + 1; // +1 for aisle
                gbc.gridy = row + 2; // +2 for legend and driver

                if (col == 2) {
                    gbc.gridx++; // Aisle space
                }

                seatsPanel.add(seatButton, gbc);
                seatNumber++;
            }
        }

        return seatsPanel;
    }

    private JToggleButton createSeatButton(String seatNumber) {
        JToggleButton button = new JToggleButton(seatNumber);
        button.setPreferredSize(new Dimension(60, 60));
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(new Color(46, 204, 113));
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        button.addActionListener(e -> {
            if (button.isSelected()) {
                selectedSeats.add(seatNumber);
                button.setBackground(new Color(52, 152, 219));
            } else {
                selectedSeats.remove(seatNumber);
                button.setBackground(new Color(46, 204, 113));
            }
            updateSelectionInfo();
        });

        return button;
    }

    private JPanel createLegendItem(String text, Color color) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setOpaque(false);

        JPanel colorBox = new JPanel();
        colorBox.setPreferredSize(new Dimension(20, 20));
        colorBox.setBackground(color);
        colorBox.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 12));

        panel.add(colorBox);
        panel.add(label);

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout(10, 10));
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        // Selection info
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        infoPanel.setOpaque(false);

        selectedSeatsLabel = new JLabel("Selected Seats: None");
        selectedSeatsLabel.setFont(new Font("Arial", Font.BOLD, 14));

        totalPriceLabel = new JLabel("Total Price: $0.00");
        totalPriceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalPriceLabel.setForeground(new Color(46, 204, 113));

        infoPanel.add(selectedSeatsLabel);
        infoPanel.add(totalPriceLabel);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        confirmButton = createStyledButton("Confirm Booking", new Color(46, 204, 113));
        confirmButton.setEnabled(false);
        confirmButton.addActionListener(e -> handleConfirmBooking());

        cancelButton = createStyledButton("Cancel", new Color(231, 76, 60));
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);

        footerPanel.add(infoPanel, BorderLayout.WEST);
        footerPanel.add(buttonPanel, BorderLayout.EAST);

        return footerPanel;
    }

    private void loadBookedSeats() {
        SwingWorker<List<String>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<String> doInBackground() {
                return bookingService.getBookedSeats(trip.getId());
            }

            @Override
            protected void done() {
                try {
                    bookedSeats = get();
                    markBookedSeats();
                } catch (Exception e) {
                    showError("Failed to load booked seats: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void markBookedSeats() {
        Component[] components = seatsPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JToggleButton) {
                JToggleButton button = (JToggleButton) comp;
                if (bookedSeats.contains(button.getText())) {
                    button.setEnabled(false);
                    button.setBackground(new Color(149, 165, 166));
                    button.setText(button.getText() + "\n✗");
                }
            }
        }
    }

    private void updateSelectionInfo() {
        if (selectedSeats.isEmpty()) {
            selectedSeatsLabel.setText("Selected Seats: None");
            totalPriceLabel.setText("Total Price: $0.00");
            confirmButton.setEnabled(false);
        } else {
            selectedSeatsLabel.setText("Selected Seats: " + String.join(", ", selectedSeats));
            BigDecimal total = trip.getPricePerSeat().multiply(BigDecimal.valueOf(selectedSeats.size()));
            totalPriceLabel.setText("Total Price: $" + total);
            confirmButton.setEnabled(true);
        }
    }

    private void handleConfirmBooking() {
        int confirm = JOptionPane.showConfirmDialog(this,
                String.format("Confirm booking for %d seat(s)?\nTotal: $%s",
                        selectedSeats.size(),
                        trip.getPricePerSeat().multiply(BigDecimal.valueOf(selectedSeats.size()))),
                "Confirm Booking",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        confirmButton.setEnabled(false);

        SwingWorker<Booking, Void> worker = new SwingWorker<>() {
            @Override
            protected Booking doInBackground() {
                return bookingService.createBooking(currentUser, trip, selectedSeats);
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                confirmButton.setEnabled(true);

                try {
                    Booking booking = get();
                    if (booking != null) {
                        // Open payment frame
                        PaymentFrame paymentFrame = new PaymentFrame(currentUser, booking, BookingFrame.this);
                        paymentFrame.setVisible(true);
                        dispose();
                    } else {
                        showError("Failed to create booking. Please try again.");
                    }
                } catch (Exception e) {
                    showError("Booking error: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(160, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
