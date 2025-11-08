package com.hungdev.busbookingsystem.util;

import org.mindrot.jbcrypt.BCrypt;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * Utility class for password hashing and verification.
 * Supports both Django PBKDF2 format and BCrypt format.
 */
public class PasswordUtil {

    /**
     * Verify a password against a hash.
     * Automatically detects if the hash is Django PBKDF2 or BCrypt format.
     *
     * @param plainPassword Plain text password
     * @param hashedPassword Hashed password from database
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (hashedPassword == null || plainPassword == null) {
            return false;
        }

        // Check if it's Django PBKDF2 format
        if (hashedPassword.startsWith("pbkdf2_sha256$")) {
            return verifyDjangoPBKDF2(plainPassword, hashedPassword);
        }

        // Check if it's BCrypt format
        if (hashedPassword.startsWith("$2a$") || hashedPassword.startsWith("$2b$") ||
            hashedPassword.startsWith("$2y$")) {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        }

        return false;
    }

    /**
     * Verify a password against Django's PBKDF2 hash format.
     * Django format: pbkdf2_sha256$iterations$salt$hash
     *
     * @param plainPassword Plain text password
     * @param djangoHash Django PBKDF2 hash
     * @return true if password matches, false otherwise
     */
    private static boolean verifyDjangoPBKDF2(String plainPassword, String djangoHash) {
        try {
            // Parse Django hash format: pbkdf2_sha256$iterations$salt$hash
            String[] parts = djangoHash.split("\\$");

            if (parts.length != 4) {
                return false;
            }

            String algorithm = parts[0]; // pbkdf2_sha256
            int iterations = Integer.parseInt(parts[1]);
            String salt = parts[2];
            String storedHash = parts[3];

            // Generate hash from plain password
            String computedHash = generatePBKDF2Hash(plainPassword, salt, iterations);

            // Compare hashes
            return storedHash.equals(computedHash);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Generate PBKDF2 hash using the same algorithm as Django.
     *
     * @param password Plain text password
     * @param salt Salt value
     * @param iterations Number of iterations
     * @return Base64 encoded hash
     */
    private static String generatePBKDF2Hash(String password, String salt, int iterations)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        // Django uses PBKDF2 with HMAC-SHA256
        char[] passwordChars = password.toCharArray();
        byte[] saltBytes = salt.getBytes();

        PBEKeySpec spec = new PBEKeySpec(passwordChars, saltBytes, iterations, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = factory.generateSecret(spec).getEncoded();

        // Django uses Base64 encoding
        return Base64.getEncoder().encodeToString(hash);
    }

    /**
     * Hash a password using BCrypt.
     * Use this for new users and password updates.
     *
     * @param plainPassword Plain text password
     * @return BCrypt hashed password
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    /**
     * Check if a hash is in Django PBKDF2 format.
     *
     * @param hash Password hash
     * @return true if Django format, false otherwise
     */
    public static boolean isDjangoPBKDF2Format(String hash) {
        return hash != null && hash.startsWith("pbkdf2_sha256$");
    }

    /**
     * Check if a hash is in BCrypt format.
     *
     * @param hash Password hash
     * @return true if BCrypt format, false otherwise
     */
    public static boolean isBCryptFormat(String hash) {
        return hash != null && (hash.startsWith("$2a$") || hash.startsWith("$2b$") ||
                                hash.startsWith("$2y$"));
    }
}
