package com.hungdev.busbookingsystem;

import com.hungdev.busbookingsystem.gui.LoginFrame;
import com.hungdev.busbookingsystem.util.JPAUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * Main application entry point for Bus Booking System GUI
 */
public class BusBookingApp {

    private static final Logger logger = LoggerFactory.getLogger(BusBookingApp.class);

    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            logger.warn("Failed to set system look and feel", e);
        }

        // Test database connection on startup
        SwingUtilities.invokeLater(() -> {
            try {
                // Test connection by getting EntityManagerFactory
                logger.info("Testing database connection...");
                JPAUtil.getEntityManagerFactory();
                logger.info("Database connection successful");

                // Show login frame
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);

            } catch (Exception e) {
                logger.error("Failed to connect to database", e);
                JOptionPane.showMessageDialog(null,
                        "Failed to connect to database.\n" +
                        "Please check your database configuration in persistence.xml\n\n" +
                        "Error: " + e.getMessage(),
                        "Database Connection Error",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });

        // Add shutdown hook to cleanup resources
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Application shutting down...");
            JPAUtil.shutdown();
            logger.info("Resources cleaned up successfully");
        }));
    }
}
