package com.hungdev.busbookingsystem.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "user_sessions",
    indexes = {
        @Index(name = "user_sessions_session_key_5c5616a8_like", columnList = "session_key"),
        @Index(name = "user_sessions_user_id_43ce9642", columnList = "user_id")
    }
)
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_key", unique = true, nullable = false, length = 255)
    private String sessionKey;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "last_activity", nullable = false)
    private OffsetDateTime lastActivity = OffsetDateTime.now();

    @Column(name = "ip_address", nullable = false, columnDefinition = "inet")
    private String ipAddress;

    @Column(name = "user_agent", nullable = false, length = 255)
    private String userAgent;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Constructors
    public UserSession() {
        this.createdAt = OffsetDateTime.now();
        this.lastActivity = OffsetDateTime.now();
        this.isActive = true;
    }

    public UserSession(String sessionKey, String ipAddress, String userAgent, User user) {
        this();
        this.sessionKey = sessionKey;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(OffsetDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "UserSession{" +
                "id=" + id +
                ", sessionKey='" + sessionKey + '\'' +
                ", createdAt=" + createdAt +
                ", lastActivity=" + lastActivity +
                ", ipAddress='" + ipAddress + '\'' +
                ", isActive=" + isActive +
                ", user=" + (user != null ? user.getUsername() : "null") +
                '}';
    }
}
