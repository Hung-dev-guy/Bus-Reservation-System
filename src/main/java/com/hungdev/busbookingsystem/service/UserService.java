package com.hungdev.busbookingsystem.service;

import com.hungdev.busbookingsystem.model.User;
import com.hungdev.busbookingsystem.util.JPAUtil;
import com.hungdev.busbookingsystem.util.PasswordUtil;
import jakarta.persistence.NoResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;

/**
 * Service class for user authentication and management.
 */
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private static UserService instance;

    // Private constructor for Singleton pattern
    private UserService() {}

    /**
     * Get singleton instance of UserService
     */
    public static UserService getInstance() {
        if (instance == null) {
            synchronized (UserService.class) {
                if (instance == null) {
                    instance = new UserService();
                }
            }
        }
        return instance;
    }

    /**
     * Authenticate user with username and password
     * @param username Username or email
     * @param password Plain text password
     * @return User object if authentication successful, null otherwise
     */
    public User login(String username, String password) {
        try {
            return JPAUtil.executeInReadOnly(em -> {
                User user;
                try {
                    // Try to find by username first
                    user = em.createQuery(
                            "SELECT u FROM User u WHERE u.username = :username AND u.isActive = true",
                            User.class)
                            .setParameter("username", username)
                            .getSingleResult();
                } catch (NoResultException e) {
                    // Try to find by email
                    try {
                        user = em.createQuery(
                                "SELECT u FROM User u WHERE u.email = :email AND u.isActive = true",
                                User.class)
                                .setParameter("email", username)
                                .getSingleResult();
                    } catch (NoResultException ex) {
                        logger.warn("User not found: {}", username);
                        return null;
                    }
                }

                // Verify password (supports both Django PBKDF2 and BCrypt formats)
                if (PasswordUtil.verifyPassword(password, user.getPassword())) {
                    logger.info("User logged in successfully: {}", user.getUsername());

                    // Update last login time
                    updateLastLogin(user.getId());

                    return user;
                } else {
                    logger.warn("Invalid password for user: {}", username);
                    return null;
                }
            });
        } catch (Exception e) {
            logger.error("Login error", e);
            return null;
        }
    }

    /**
     * Register a new user
     * @param username Unique username
     * @param email Unique email
     * @param firstName First name
     * @param lastName Last name
     * @param password Plain text password (will be hashed)
     * @param role User role (CUSTOMER, ADMIN, STAFF)
     * @return Created user object, or null if registration failed
     */
    public User register(String username, String email, String firstName, String lastName,
                        String password, String role) {
        try {
            // Check if username already exists
            if (usernameExists(username)) {
                logger.warn("Username already exists: {}", username);
                throw new IllegalArgumentException("Username already exists");
            }

            // Check if email already exists
            if (emailExists(email)) {
                logger.warn("Email already exists: {}", email);
                throw new IllegalArgumentException("Email already exists");
            }

            // Hash password using BCrypt for new users
            String hashedPassword = PasswordUtil.hashPassword(password);

            // Create user
            User[] createdUser = new User[1];
            JPAUtil.executeInTransaction(em -> {
                User user = new User(username, email, firstName, lastName, hashedPassword, role);
                user.setIsActive(true);
                user.setIsVerified(false);
                user.setIsStaff(false);
                user.setIsSuperuser(false);
                user.setPermissions("{}"); // Empty JSON permissions
                em.persist(user);
                createdUser[0] = user;
                logger.info("New user registered: {}", username);
            });

            return createdUser[0];
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Registration error", e);
            return null;
        }
    }

    /**
     * Check if username exists
     */
    public boolean usernameExists(String username) {
        return JPAUtil.executeInReadOnly(em -> {
            Long count = em.createQuery(
                    "SELECT COUNT(u) FROM User u WHERE u.username = :username",
                    Long.class)
                    .setParameter("username", username)
                    .getSingleResult();
            return count > 0;
        });
    }

    /**
     * Check if email exists
     */
    public boolean emailExists(String email) {
        return JPAUtil.executeInReadOnly(em -> {
            Long count = em.createQuery(
                    "SELECT COUNT(u) FROM User u WHERE u.email = :email",
                    Long.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return count > 0;
        });
    }

    /**
     * Update user's last login time
     */
    private void updateLastLogin(Long userId) {
        try {
            JPAUtil.executeInTransaction(em -> {
                User user = em.find(User.class, userId);
                if (user != null) {
                    user.setLastLogin(OffsetDateTime.now());
                    em.merge(user);
                }
            });
        } catch (Exception e) {
            logger.error("Failed to update last login time", e);
        }
    }

    /**
     * Get user by ID
     */
    public User getUserById(Long userId) {
        return JPAUtil.executeInReadOnly(em -> em.find(User.class, userId));
    }

    /**
     * Validate password strength
     */
    public boolean isPasswordValid(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        // Add more password strength requirements as needed
        return true;
    }
}
