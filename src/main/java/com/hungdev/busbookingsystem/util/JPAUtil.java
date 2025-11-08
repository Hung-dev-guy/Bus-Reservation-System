package com.hungdev.busbookingsystem.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for managing JPA EntityManager instances.
 * Implements Singleton pattern for EntityManagerFactory.
 */
public class JPAUtil {

    private static final Logger logger = LoggerFactory.getLogger(JPAUtil.class);
    private static final String PERSISTENCE_UNIT_NAME = "bus-booking-pu";
    private static EntityManagerFactory entityManagerFactory;

    // Private constructor to prevent instantiation
    private JPAUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Get EntityManagerFactory instance (Singleton).
     * Creates the factory on first call and reuses it.
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) {
            synchronized (JPAUtil.class) {
                if (entityManagerFactory == null) {
                    try {
                        logger.info("Creating EntityManagerFactory for persistence unit: {}", PERSISTENCE_UNIT_NAME);
                        entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
                        logger.info("EntityManagerFactory created successfully");
                    } catch (Exception e) {
                        logger.error("Failed to create EntityManagerFactory", e);
                        throw new RuntimeException("Failed to create EntityManagerFactory", e);
                    }
                }
            }
        }
        return entityManagerFactory;
    }

    /**
     * Create a new EntityManager instance.
     * Remember to close it after use!
     */
    public static EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    /**
     * Close the EntityManagerFactory.
     * Should be called when the application shuts down.
     */
    public static void shutdown() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            logger.info("Closing EntityManagerFactory");
            entityManagerFactory.close();
            logger.info("EntityManagerFactory closed successfully");
        }
    }

    /**
     * Execute a transactional operation.
     * Automatically handles EntityManager lifecycle and transaction management.
     *
     * @param operation The operation to execute within a transaction
     */
    public static void executeInTransaction(TransactionalOperation operation) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            operation.execute(em);
            em.getTransaction().commit();
            logger.debug("Transaction committed successfully");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
                logger.error("Transaction rolled back due to error", e);
            }
            throw new RuntimeException("Transaction failed", e);
        } finally {
            em.close();
        }
    }

    /**
     * Execute a read-only operation.
     * Automatically handles EntityManager lifecycle.
     *
     * @param operation The read operation to execute
     * @param <T> The return type
     * @return The result of the operation
     */
    public static <T> T executeInReadOnly(ReadOperation<T> operation) {
        EntityManager em = getEntityManager();
        try {
            return operation.execute(em);
        } catch (Exception e) {
            logger.error("Read operation failed", e);
            throw new RuntimeException("Read operation failed", e);
        } finally {
            em.close();
        }
    }

    @FunctionalInterface
    public interface TransactionalOperation {
        void execute(EntityManager em);
    }

    @FunctionalInterface
    public interface ReadOperation<T> {
        T execute(EntityManager em);
    }
}
