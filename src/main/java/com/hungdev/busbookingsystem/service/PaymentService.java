package com.hungdev.busbookingsystem.service;

import com.hungdev.busbookingsystem.model.Booking;
import com.hungdev.busbookingsystem.model.Payment;
import com.hungdev.busbookingsystem.util.JPAUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Service class for payment processing
 */
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    private static PaymentService instance;

    private PaymentService() {}

    public static PaymentService getInstance() {
        if (instance == null) {
            synchronized (PaymentService.class) {
                if (instance == null) {
                    instance = new PaymentService();
                }
            }
        }
        return instance;
    }

    /**
     * Process payment for a booking
     */
    public Payment processPayment(Booking booking, String paymentMethod, String cardNumber) {
        try {
            Payment[] createdPayment = new Payment[1];

            JPAUtil.executeInTransaction(em -> {
                Booking managedBooking = em.find(Booking.class, booking.getId());

                // Create payment
                Payment payment = new Payment();
                payment.setBooking(managedBooking);
                payment.setAmount(managedBooking.getTotalAmount());
                payment.setPaymentMethod(paymentMethod);
                payment.setPaymentTime(OffsetDateTime.now());
                payment.setTransactionCode(generateTransactionId());

                // Simulate payment processing
                boolean success = simulatePaymentProcessing(paymentMethod, cardNumber, managedBooking.getTotalAmount());

                if (success) {
                    payment.setStatus("Succeeded");
                    managedBooking.setStatus("Confirmed");
                    logger.info("Payment successful for booking: {}", booking.getId());
                } else {
                    payment.setStatus("Failed");
                    logger.warn("Payment failed for booking: {}", booking.getId());
                }

                em.persist(payment);
                em.merge(managedBooking);

                createdPayment[0] = payment;
            });

            return createdPayment[0];
        } catch (Exception e) {
            logger.error("Payment processing error", e);
            return null;
        }
    }

    /**
     * Get payment by booking ID
     */
    public Payment getPaymentByBookingId(Long bookingId) {
        return JPAUtil.executeInReadOnly(em -> {
            try {
                return em.createQuery(
                        "SELECT p FROM Payment p WHERE p.booking.id = :bookingId ORDER BY p.paymentTime DESC",
                        Payment.class)
                        .setParameter("bookingId", bookingId)
                        .setMaxResults(1)
                        .getSingleResult();
            } catch (Exception e) {
                return null;
            }
        });
    }

    /**
     * Simulate payment processing (for demo purposes)
     */
    private boolean simulatePaymentProcessing(String paymentMethod, String cardNumber, BigDecimal amount) {
        // Simulate payment gateway delay
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // For demo: payments always succeed
        // In production, this would integrate with actual payment gateway
        return true;
    }

    /**
     * Generate unique transaction ID
     */
    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Refund payment (for cancellations)
     */
    public boolean refundPayment(Long bookingId) {
        try {
            JPAUtil.executeInTransaction(em -> {
                Payment payment = em.createQuery(
                        "SELECT p FROM Payment p WHERE p.booking.id = :bookingId AND p.status = 'Succeeded'",
                        Payment.class)
                        .setParameter("bookingId", bookingId)
                        .setMaxResults(1)
                        .getSingleResult();

                if (payment != null) {
                    payment.setStatus("Refunded");
                    em.merge(payment);
                    logger.info("Payment refunded for booking: {}", bookingId);
                }
            });
            return true;
        } catch (Exception e) {
            logger.error("Refund failed", e);
            return false;
        }
    }
}
