package com.hungdev.busbookingsystem.gui;

import com.hungdev.busbookingsystem.model.Booking;
import com.hungdev.busbookingsystem.model.Payment;
import com.hungdev.busbookingsystem.model.User;
import com.hungdev.busbookingsystem.service.PaymentService;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/**
 * Payment processing frame
 */
public class PaymentFrame extends JFrame {

    private User currentUser;
    private Booking booking;
    private JFrame parentFrame;
    private PaymentService paymentService;

    private JComboBox<String> paymentMethodComboBox;
    private JTextField cardNumberField;
    private JTextField cardHolderField;
    private JTextField expiryDateField;
    private JTextField cvvField;
    private JButton payButton;
    private JButton cancelButton;

    public PaymentFrame(User user, Booking booking, JFrame parentFrame) {
        this.currentUser = user;
        this.booking = booking;
        this.parentFrame = parentFrame;
        this.paymentService = PaymentService.getInstance();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Payment - Booking #" + booking.getId());
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parentFrame);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        mainPanel.setBackground(new Color(245, 245, 245));

        // Header
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Booking summary
        mainPanel.add(createSummaryPanel(), BorderLayout.CENTER);

        // Payment form
        mainPanel.add(createPaymentPanel(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(new Color(245, 245, 245));

        JLabel titleLabel = new JLabel("Complete Your Payment");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(46, 204, 113));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(10));

        return headerPanel;
    }

    private JPanel createSummaryPanel() {
        JPanel summaryPanel = new JPanel(new GridBagLayout());
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridx = 0;

        JLabel summaryTitle = new JLabel("Booking Summary");
        summaryTitle.setFont(new Font("Arial", Font.BOLD, 18));
        summaryTitle.setForeground(new Color(52, 152, 219));
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        summaryPanel.add(summaryTitle, gbc);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

        // Details
        gbc.gridwidth = 1;
        int row = 1;

        addSummaryRow(summaryPanel, gbc, "Route:",
                booking.getTrip().getRoute().getStartLocation().getName() + " → " +
                booking.getTrip().getRoute().getEndLocation().getName(), row++);

        addSummaryRow(summaryPanel, gbc, "Departure:",
                booking.getTrip().getDepartureTime().toLocalDateTime().format(formatter), row++);

        addSummaryRow(summaryPanel, gbc, "Bus:",
                booking.getTrip().getBus().getLicensePlate(), row++);

        addSummaryRow(summaryPanel, gbc, "Number of Seats:",
                String.valueOf(booking.getNumberOfSeats()), row++);

        addSummaryRow(summaryPanel, gbc, "Total Amount:",
                "$" + booking.getTotalAmount(), row++);

        return summaryPanel;
    }

    private void addSummaryRow(JPanel panel, GridBagConstraints gbc, String label, String value, int row) {
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(labelComp, gbc);

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Arial", Font.PLAIN, 13));
        gbc.gridx = 1;
        panel.add(valueComp, gbc);
    }

    private JPanel createPaymentPanel() {
        JPanel paymentPanel = new JPanel(new GridBagLayout());
        paymentPanel.setBackground(Color.WHITE);
        paymentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        JLabel paymentTitle = new JLabel("Payment Details");
        paymentTitle.setFont(new Font("Arial", Font.BOLD, 18));
        paymentTitle.setForeground(new Color(52, 152, 219));
        gbc.gridy = 0;
        paymentPanel.add(paymentTitle, gbc);

        gbc.gridwidth = 1;

        // Payment Method
        addFormLabel(paymentPanel, gbc, "Payment Method:", 1);
        paymentMethodComboBox = new JComboBox<>(new String[]{"Credit Card", "Debit Card", "PayPal"});
        paymentMethodComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 1;
        paymentPanel.add(paymentMethodComboBox, gbc);

        // Card Holder
        addFormLabel(paymentPanel, gbc, "Card Holder Name:", 2);
        cardHolderField = new JTextField(currentUser.getFirstName() + " " + currentUser.getLastName());
        styleTextField(cardHolderField);
        gbc.gridx = 1;
        gbc.gridy = 2;
        paymentPanel.add(cardHolderField, gbc);

        // Card Number
        addFormLabel(paymentPanel, gbc, "Card Number:", 3);
        cardNumberField = new JTextField();
        styleTextField(cardNumberField);
        gbc.gridx = 1;
        gbc.gridy = 3;
        paymentPanel.add(cardNumberField, gbc);

        // Expiry Date
        addFormLabel(paymentPanel, gbc, "Expiry Date (MM/YY):", 4);
        expiryDateField = new JTextField();
        styleTextField(expiryDateField);
        gbc.gridx = 1;
        gbc.gridy = 4;
        paymentPanel.add(expiryDateField, gbc);

        // CVV
        addFormLabel(paymentPanel, gbc, "CVV:", 5);
        cvvField = new JTextField();
        styleTextField(cvvField);
        gbc.gridx = 1;
        gbc.gridy = 5;
        paymentPanel.add(cvvField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        cancelButton = createStyledButton("Cancel", new Color(231, 76, 60));
        cancelButton.addActionListener(e -> handleCancel());

        payButton = createStyledButton("Pay $" + booking.getTotalAmount(), new Color(46, 204, 113));
        payButton.addActionListener(e -> handlePayment());

        buttonPanel.add(cancelButton);
        buttonPanel.add(payButton);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 8, 8, 8);
        paymentPanel.add(buttonPanel, gbc);

        return paymentPanel;
    }

    private void addFormLabel(JPanel panel, GridBagConstraints gbc, String text, int row) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(label, gbc);
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
    }

    private void handlePayment() {
        // Validation
        if (cardHolderField.getText().trim().isEmpty() ||
            cardNumberField.getText().trim().isEmpty() ||
            expiryDateField.getText().trim().isEmpty() ||
            cvvField.getText().trim().isEmpty()) {
            showError("Please fill in all payment details");
            return;
        }

        if (!cardNumberField.getText().matches("\\d{16}")) {
            showError("Please enter a valid 16-digit card number");
            return;
        }

        if (!cvvField.getText().matches("\\d{3,4}")) {
            showError("Please enter a valid CVV");
            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        payButton.setEnabled(false);

        SwingWorker<Payment, Void> worker = new SwingWorker<>() {
            @Override
            protected Payment doInBackground() {
                String paymentMethod = (String) paymentMethodComboBox.getSelectedItem();
                String cardNumber = cardNumberField.getText().trim();
                return paymentService.processPayment(booking, paymentMethod, cardNumber);
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                payButton.setEnabled(true);

                try {
                    Payment payment = get();
                    if (payment != null && "Succeeded".equals(payment.getStatus())) {
                        showSuccess("Payment successful!\nBooking confirmed.\nTransaction Code: " +
                                payment.getTransactionCode());
                        dispose();
                        if (parentFrame != null) {
                            parentFrame.dispose();
                        }
                    } else {
                        showError("Payment failed. Please try again.");
                    }
                } catch (Exception e) {
                    showError("Payment error: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void handleCancel() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Cancel payment and booking?",
                "Cancel",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
        }
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

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}
