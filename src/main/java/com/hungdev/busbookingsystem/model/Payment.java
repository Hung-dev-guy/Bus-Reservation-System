package com.hungdev.busbookingsystem.model;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments",
    indexes = {
        @Index(name = "idx_payments_booking_id", columnList = "booking_id"),
        @Index(name = "idx_payments_status", columnList = "status")
    }
)
public class Payment {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_method", nullable = false, length = 20)
    private String paymentMethod;

    @Column(name = "transaction_code", nullable = false, length = 255)
    private String transactionCode;

    @Column(name = "payment_time", nullable = false)
    private OffsetDateTime paymentTime = OffsetDateTime.now();

    @Column(nullable = false, length = 10)
    private String status; // Succeeded, Failed, Pending
    
    
    // Constructors
    public Payment() {
        this.paymentTime = OffsetDateTime.now();
        this.status = "Pending";
    }

    public Payment(Booking booking, BigDecimal amount, String paymentMethod) {
        this();
        this.booking = booking;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
    
    public Booking getBooking() {
        return booking;
    }
    
    public void setBooking(Booking booking) {
        this.booking = booking;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getTransactionCode() {
        return transactionCode;
    }
    
    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }
    
    public OffsetDateTime getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(OffsetDateTime paymentTime) {
        this.paymentTime = paymentTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", amount=" + amount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", transactionCode='" + transactionCode + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
