package com.hungdev.busbookingsystem.gui;

import com.hungdev.busbookingsystem.model.User;
import com.hungdev.busbookingsystem.service.UserService;

import javax.swing.*;
import java.awt.*;

/**
 * User Profile management window - view, edit, and delete profile
 */
public class UserProfileFrame extends JFrame {

    private User currentUser;
    private UserService userService;
    private MainFrame parentFrame;

    // Form fields
    private JTextField usernameField;
    private JTextField emailField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JLabel roleLabel;
    private JLabel dateJoinedLabel;
    private JLabel lastLoginLabel;

    // Buttons
    private JButton editButton;
    private JButton saveButton;
    private JButton cancelButton;
    private JButton changePasswordButton;
    private JButton deleteAccountButton;
    private JButton backButton;

    private boolean isEditMode = false;

    public UserProfileFrame(User user, MainFrame parentFrame) {
        this.currentUser = user;
        this.parentFrame = parentFrame;
        this.userService = UserService.getInstance();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("My Profile - " + currentUser.getUsername());
        setSize(700, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parentFrame);
        setResizable(true);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        mainPanel.setBackground(new Color(245, 245, 245));

        // Header panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Profile form panel
        JPanel formPanel = createFormPanel();
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(new Color(245, 245, 245));

        JLabel titleLabel = new JLabel("My Profile");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(155, 89, 182));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("View and manage your account information");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitleLabel);
        headerPanel.add(Box.createVerticalStrut(15));

        return headerPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        // Account Information Section
        addSectionHeader(formPanel, gbc, "Account Information", 0);

        // Username (read-only)
        addFormField(formPanel, gbc, "Username:", 1);
        usernameField = new JTextField(currentUser.getUsername());
        usernameField.setEditable(false);
        usernameField.setBackground(new Color(240, 240, 240));
        styleTextField(usernameField);
        gbc.gridy = 2;
        formPanel.add(usernameField, gbc);

        // Email
        addFormField(formPanel, gbc, "Email:", 3);
        emailField = new JTextField(currentUser.getEmail());
        emailField.setEditable(false);
        styleTextField(emailField);
        gbc.gridy = 4;
        formPanel.add(emailField, gbc);

        // Personal Information Section
        addSectionHeader(formPanel, gbc, "Personal Information", 5);

        // First Name
        addFormField(formPanel, gbc, "First Name:", 6);
        firstNameField = new JTextField(currentUser.getFirstName());
        firstNameField.setEditable(false);
        styleTextField(firstNameField);
        gbc.gridy = 7;
        formPanel.add(firstNameField, gbc);

        // Last Name
        addFormField(formPanel, gbc, "Last Name:", 8);
        lastNameField = new JTextField(currentUser.getLastName());
        lastNameField.setEditable(false);
        styleTextField(lastNameField);
        gbc.gridy = 9;
        formPanel.add(lastNameField, gbc);

        // Account Details Section
        addSectionHeader(formPanel, gbc, "Account Details", 10);

        // Role
        addFormField(formPanel, gbc, "Role:", 11);
        roleLabel = new JLabel(currentUser.getRole());
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridy = 12;
        formPanel.add(roleLabel, gbc);

        // Date Joined
        addFormField(formPanel, gbc, "Member Since:", 13);
        dateJoinedLabel = new JLabel(currentUser.getDateJoined() != null ?
            currentUser.getDateJoined().toLocalDate().toString() : "N/A");
        dateJoinedLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridy = 14;
        formPanel.add(dateJoinedLabel, gbc);

        // Last Login
        addFormField(formPanel, gbc, "Last Login:", 15);
        lastLoginLabel = new JLabel(currentUser.getLastLogin() != null ?
            currentUser.getLastLogin().toLocalDateTime().toString() : "Never");
        lastLoginLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridy = 16;
        formPanel.add(lastLoginLabel, gbc);

        // Password Change Section (initially hidden)
        addSectionHeader(formPanel, gbc, "Change Password", 17);

        // Current Password
        addFormField(formPanel, gbc, "Current Password:", 18);
        currentPasswordField = new JPasswordField();
        currentPasswordField.setVisible(false);
        styleTextField(currentPasswordField);
        gbc.gridy = 19;
        formPanel.add(currentPasswordField, gbc);

        // New Password
        addFormField(formPanel, gbc, "New Password:", 20);
        newPasswordField = new JPasswordField();
        newPasswordField.setVisible(false);
        styleTextField(newPasswordField);
        gbc.gridy = 21;
        formPanel.add(newPasswordField, gbc);

        // Confirm Password
        addFormField(formPanel, gbc, "Confirm New Password:", 22);
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setVisible(false);
        styleTextField(confirmPasswordField);
        gbc.gridy = 23;
        formPanel.add(confirmPasswordField, gbc);

        return formPanel;
    }

    private void addSectionHeader(JPanel panel, GridBagConstraints gbc, String text, int yPosition) {
        JLabel sectionLabel = new JLabel(text);
        sectionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        sectionLabel.setForeground(new Color(155, 89, 182));
        gbc.gridy = yPosition;
        gbc.insets = new Insets(20, 8, 8, 8);
        panel.add(sectionLabel, gbc);
        gbc.insets = new Insets(8, 8, 8, 8);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, int yPosition) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 13));
        gbc.gridy = yPosition;
        panel.add(label, gbc);
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));

        // Edit button
        editButton = createStyledButton("Edit Profile", new Color(52, 152, 219));
        editButton.addActionListener(e -> enableEditMode());

        // Save button (initially hidden)
        saveButton = createStyledButton("Save Changes", new Color(46, 204, 113));
        saveButton.setVisible(false);
        saveButton.addActionListener(e -> handleSaveProfile());

        // Cancel button (initially hidden)
        cancelButton = createStyledButton("Cancel", new Color(149, 165, 166));
        cancelButton.setVisible(false);
        cancelButton.addActionListener(e -> disableEditMode());

        // Change Password button
        changePasswordButton = createStyledButton("Change Password", new Color(241, 196, 15));
        changePasswordButton.addActionListener(e -> handleChangePassword());

        // Delete Account button
        deleteAccountButton = createStyledButton("Delete Account", new Color(231, 76, 60));
        deleteAccountButton.addActionListener(e -> handleDeleteAccount());

        // Back button
        backButton = createStyledButton("Back to Dashboard", new Color(149, 165, 166));
        backButton.addActionListener(e -> dispose());

        buttonPanel.add(editButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(changePasswordButton);
        buttonPanel.add(deleteAccountButton);
        buttonPanel.add(backButton);

        return buttonPanel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setPreferredSize(new Dimension(160, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void enableEditMode() {
        isEditMode = true;
        emailField.setEditable(true);
        emailField.setBackground(Color.WHITE);
        firstNameField.setEditable(true);
        firstNameField.setBackground(Color.WHITE);
        lastNameField.setEditable(true);
        lastNameField.setBackground(Color.WHITE);

        editButton.setVisible(false);
        saveButton.setVisible(true);
        cancelButton.setVisible(true);
        changePasswordButton.setVisible(false);
        deleteAccountButton.setVisible(false);
    }

    private void disableEditMode() {
        isEditMode = false;

        // Restore original values
        emailField.setText(currentUser.getEmail());
        emailField.setEditable(false);
        emailField.setBackground(Color.WHITE);
        firstNameField.setText(currentUser.getFirstName());
        firstNameField.setEditable(false);
        firstNameField.setBackground(Color.WHITE);
        lastNameField.setText(currentUser.getLastName());
        lastNameField.setEditable(false);
        lastNameField.setBackground(Color.WHITE);

        editButton.setVisible(true);
        saveButton.setVisible(false);
        cancelButton.setVisible(false);
        changePasswordButton.setVisible(true);
        deleteAccountButton.setVisible(true);
    }

    private void handleSaveProfile() {
        String email = emailField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();

        // Validation
        if (email.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Please enter a valid email address");
            return;
        }

        // Check if email changed and already exists
        if (!email.equals(currentUser.getEmail()) && userService.emailExists(email)) {
            showError("Email already exists");
            return;
        }

        // Update user
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        boolean success = userService.updateUser(currentUser.getId(), email, firstName, lastName);

        setCursor(Cursor.getDefaultCursor());

        if (success) {
            // Refresh current user
            currentUser = userService.getUserById(currentUser.getId());
            showSuccess("Profile updated successfully!");
            disableEditMode();

            // Update parent frame if needed
            if (parentFrame != null) {
                parentFrame.dispose();
                MainFrame newMainFrame = new MainFrame(currentUser);
                newMainFrame.setVisible(true);
                this.dispose();
            }
        } else {
            showError("Failed to update profile. Please try again.");
        }
    }

    private void handleChangePassword() {
        // Toggle password fields visibility
        boolean visible = !currentPasswordField.isVisible();

        JLabel[] labels = getAllLabels();
        for (JLabel label : labels) {
            if (label.getText().contains("Password")) {
                label.setVisible(visible);
            }
        }

        currentPasswordField.setVisible(visible);
        newPasswordField.setVisible(visible);
        confirmPasswordField.setVisible(visible);

        if (visible) {
            changePasswordButton.setText("Save Password");
            changePasswordButton.setBackground(new Color(46, 204, 113));
        } else {
            // Actually change password
            String currentPassword = new String(currentPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (!currentPassword.isEmpty() && !newPassword.isEmpty()) {
                if (!userService.isPasswordValid(newPassword)) {
                    showError("New password must be at least 8 characters long");
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    showError("New passwords do not match");
                    return;
                }

                boolean success = userService.changePassword(currentUser.getId(), currentPassword, newPassword);

                if (success) {
                    showSuccess("Password changed successfully!");
                    currentPasswordField.setText("");
                    newPasswordField.setText("");
                    confirmPasswordField.setText("");
                } else {
                    showError("Current password is incorrect or update failed");
                    return;
                }
            }

            changePasswordButton.setText("Change Password");
            changePasswordButton.setBackground(new Color(241, 196, 15));
        }

        revalidate();
        repaint();
    }

    private JLabel[] getAllLabels() {
        Container container = ((JScrollPane) getContentPane().getComponent(0).getComponentAt(1, 1)).getViewport();
        JPanel formPanel = (JPanel) container.getComponent(0);
        return java.util.Arrays.stream(formPanel.getComponents())
                .filter(c -> c instanceof JLabel)
                .toArray(JLabel[]::new);
    }

    private void handleDeleteAccount() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete your account?\n" +
                "This action cannot be undone!\n\n" +
                "All your bookings and data will be permanently deleted.",
                "Confirm Account Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // Second confirmation
            String input = JOptionPane.showInputDialog(this,
                    "Type 'DELETE' to confirm account deletion:",
                    "Final Confirmation",
                    JOptionPane.WARNING_MESSAGE);

            if ("DELETE".equals(input)) {
                boolean success = userService.deleteUser(currentUser.getId());

                if (success) {
                    showSuccess("Account deleted successfully. Goodbye!");
                    // Return to login screen
                    if (parentFrame != null) {
                        parentFrame.dispose();
                    }
                    LoginFrame loginFrame = new LoginFrame();
                    loginFrame.setVisible(true);
                    this.dispose();
                } else {
                    showError("Failed to delete account. Please try again or contact support.");
                }
            }
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}
