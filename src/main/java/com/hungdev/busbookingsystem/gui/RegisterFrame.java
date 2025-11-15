package com.hungdev.busbookingsystem.gui;

import com.hungdev.busbookingsystem.model.User;
import com.hungdev.busbookingsystem.service.UserService;

import javax.swing.*;
import java.awt.*;

/**
 * Registration/Sign Up window for new users
 */
public class RegisterFrame extends JFrame {

    private JTextField usernameField;
    private JTextField emailField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton backButton;
    private UserService userService;
    private LoginFrame loginFrame;

    public RegisterFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
        this.userService = UserService.getInstance();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Bus Booking System - Sign Up");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        mainPanel.setBackground(new Color(245, 245, 245));

        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(245, 245, 245));
        JLabel titleLabel = new JLabel("Create New Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(46, 204, 113));
        titlePanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Sign up to book bus tickets");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitleLabel);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        // First Name
        addFormField(formPanel, gbc, "First Name:", 0);
        firstNameField = new JTextField(20);
        styleTextField(firstNameField);
        gbc.gridy = 1;
        formPanel.add(firstNameField, gbc);

        // Last Name
        addFormField(formPanel, gbc, "Last Name:", 2);
        lastNameField = new JTextField(20);
        styleTextField(lastNameField);
        gbc.gridy = 3;
        formPanel.add(lastNameField, gbc);

        // Username
        addFormField(formPanel, gbc, "Username:", 4);
        usernameField = new JTextField(20);
        styleTextField(usernameField);
        gbc.gridy = 5;
        formPanel.add(usernameField, gbc);

        // Email
        addFormField(formPanel, gbc, "Email:", 6);
        emailField = new JTextField(20);
        styleTextField(emailField);
        gbc.gridy = 7;
        formPanel.add(emailField, gbc);

        // Password
        addFormField(formPanel, gbc, "Password (min 8 characters):", 8);
        passwordField = new JPasswordField(20);
        styleTextField(passwordField);
        gbc.gridy = 9;
        formPanel.add(passwordField, gbc);

        // Confirm Password
        addFormField(formPanel, gbc, "Confirm Password:", 10);
        confirmPasswordField = new JPasswordField(20);
        styleTextField(confirmPasswordField);
        gbc.gridy = 11;
        formPanel.add(confirmPasswordField, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        gbc.gridy = 12;
        gbc.insets = new Insets(20, 5, 5, 5);

        registerButton = new JButton("Sign Up");
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setBackground(new Color(46, 204, 113));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(e -> handleRegister());

        backButton = new JButton("Back to Login");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.setBackground(new Color(149, 165, 166));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> backToLogin());

        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);
        formPanel.add(buttonPanel, gbc);

        // Add panels to main panel
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, int yPosition) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 13));
        gbc.gridy = yPosition;
        if (yPosition > 0) {
            gbc.insets = new Insets(12, 5, 5, 5);
        }
        panel.add(label, gbc);
        gbc.insets = new Insets(5, 5, 5, 5);
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }

    private void handleRegister() {
        // Get and validate input
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Validation
        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() ||
                email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }

        if (username.length() < 3) {
            showError("Username must be at least 3 characters");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Please enter a valid email address");
            return;
        }

        if (!userService.isPasswordValid(password)) {
            showError("Password must be at least 8 characters long");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        // Show loading cursor
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        registerButton.setEnabled(false);

        // Perform registration in background thread
        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() {
                try {
                    return userService.register(username, email, firstName, lastName,
                            password, "CUSTOMER");
                } catch (IllegalArgumentException e) {
                    throw e;
                } catch (Exception e) {
                    throw new RuntimeException("Registration failed: " + e.getMessage());
                }
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                registerButton.setEnabled(true);

                try {
                    User user = get();
                    if (user != null) {
                        showSuccess("Registration successful! Please login.");
                        backToLogin();
                    }
                } catch (Exception e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof IllegalArgumentException) {
                        showError(cause.getMessage());
                    } else {
                        showError("Registration failed. Please try again.");
                    }
                }
            }
        };
        worker.execute();
    }

    private void backToLogin() {
        loginFrame.setVisible(true);
        this.dispose();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
