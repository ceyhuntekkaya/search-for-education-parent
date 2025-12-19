package com.genixo.education.search.entity.supply;

import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "supply_notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    private String notificationType; // RFQ_PUBLISHED, QUOTATION_SUBMITTED, ORDER_CONFIRMED, MESSAGE_RECEIVED, etc.

    @Column(nullable = false)
    private Boolean isRead = false;

    // Reference to related entity (optional)
    private Long referenceId; // ID of RFQ, Order, Quotation, etc.
    private String referenceType; // RFQ, ORDER, QUOTATION, MESSAGE, etc.

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime readAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

