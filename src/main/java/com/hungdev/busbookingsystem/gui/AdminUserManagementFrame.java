package com.hungdev.busbookingsystem.gui;

import com.hungdev.busbookingsystem.model.User;
import com.hungdev.busbookingsystem.service.AdminService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Admin User Management Frame
 */
public class AdminUserManagementFrame extends JFrame {

    private User currentUser;
    private AdminDashboardFrame parentFrame;
    private AdminService adminService;
    private JTable usersTable;
    private DefaultTableModel tableModel;
    private List<User> users;

    public AdminUserManagementFrame(User user, AdminDashboardFrame parentFrame) {
        this.currentUser = user;
        this.parentFrame = parentFrame;
        this.adminService = AdminService.getInstance();
        initializeUI();
        loadUsers();
    }

    private void initializeUI() {
        setTitle("User Management");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parentFrame);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 245, 245));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 245, 245));
        
        JLabel titleLabel = new JLabel("User Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(46, 204, 113));

        JButton addButton = createStyledButton("+ Add User", new Color(46, 204, 113));
        addButton.addActionListener(e -> showAddUserDialog());

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(addButton, BorderLayout.EAST);

        // Table
        String[] columnNames = {"ID", "Username", "Email", "First Name", "Last Name", "Role", "Active"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        usersTable = new JTable(tableModel);
        usersTable.setFont(new Font("Arial", Font.PLAIN, 13));
        usersTable.setRowHeight(30);
        usersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        usersTable.getTableHeader().setBackground(new Color(46, 204, 113));
        usersTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(usersTable);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(245, 245, 245));

        JButton editButton = createStyledButton("Edit", new Color(52, 152, 219));
        JButton deleteButton = createStyledButton("Delete", new Color(231, 76, 60));
        JButton refreshButton = createStyledButton("Refresh", new Color(149, 165, 166));

        editButton.addActionListener(e -> editSelectedUser());
        deleteButton.addActionListener(e -> deleteSelectedUser());
        refreshButton.addActionListener(e -> loadUsers());

        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadUsers() {
        SwingWorker<List<User>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<User> doInBackground() {
                return adminService.getAllUsers();
            }

            @Override
            protected void done() {
                try {
                    users = get();
                    displayUsers();
                } catch (Exception e) {
                    showError("Failed to load users: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void displayUsers() {
        tableModel.setRowCount(0);
        for (User user : users) {
            Object[] row = {
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.getIsActive() ? "Yes" : "No"
            };
            tableModel.addRow(row);
        }
    }

    private void showAddUserDialog() {
        JDialog dialog = new JDialog(this, "Add New User", true);
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField usernameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"USER", "ADMIN"});

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("First Name:"));
        panel.add(firstNameField);
        panel.add(new JLabel("Last Name:"));
        panel.add(lastNameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Role:"));
        panel.add(roleCombo);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = createStyledButton("Save", new Color(46, 204, 113));
        JButton cancelButton = createStyledButton("Cancel", new Color(149, 165, 166));

        saveButton.addActionListener(e -> {
            try {
                String username = usernameField.getText().trim();
                String email = emailField.getText().trim();
                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                String password = new String(passwordField.getPassword());
                String role = (String) roleCombo.getSelectedItem();

                if (username.isEmpty() || email.isEmpty() || firstName.isEmpty() || 
                    lastName.isEmpty() || password.isEmpty()) {
                    showError("All fields are required!");
                    return;
                }

                adminService.createUser(username, email, firstName, lastName, password, role);
                showInfo("User created successfully!");
                dialog.dispose();
                loadUsers();
            } catch (Exception ex) {
                showError("Failed to create user: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void editSelectedUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a user to edit!");
            return;
        }

        User user = users.get(selectedRow);
        
        JDialog dialog = new JDialog(this, "Edit User", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField emailField = new JTextField(user.getEmail());
        JTextField firstNameField = new JTextField(user.getFirstName());
        JTextField lastNameField = new JTextField(user.getLastName());
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"USER", "ADMIN"});
        roleCombo.setSelectedItem(user.getRole());
        JCheckBox activeCheckbox = new JCheckBox("Active", user.getIsActive());

        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("First Name:"));
        panel.add(firstNameField);
        panel.add(new JLabel("Last Name:"));
        panel.add(lastNameField);
        panel.add(new JLabel("Role:"));
        panel.add(roleCombo);
        panel.add(new JLabel("Status:"));
        panel.add(activeCheckbox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = createStyledButton("Save", new Color(46, 204, 113));
        JButton cancelButton = createStyledButton("Cancel", new Color(149, 165, 166));

        saveButton.addActionListener(e -> {
            try {
                adminService.updateUser(
                    user.getId(),
                    emailField.getText().trim(),
                    firstNameField.getText().trim(),
                    lastNameField.getText().trim(),
                    (String) roleCombo.getSelectedItem(),
                    activeCheckbox.isSelected()
                );
                showInfo("User updated successfully!");
                dialog.dispose();
                loadUsers();
            } catch (Exception ex) {
                showError("Failed to update user: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void deleteSelectedUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a user to delete!");
            return;
        }

        User user = users.get(selectedRow);
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete user: " + user.getUsername() + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                adminService.deleteUser(user.getId());
                showInfo("User deleted successfully!");
                loadUsers();
            } catch (Exception e) {
                showError("Failed to delete user: " + e.getMessage());
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
