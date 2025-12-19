package com.genixo.education.search.entity.supply;


import com.genixo.education.search.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "rfq_invitations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RFQInvitation extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "rfq_id", nullable = false)
    private RFQ rfq;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(nullable = false)
    private LocalDateTime invitedAt;

    @PrePersist
    protected void onCreate() {
        invitedAt = LocalDateTime.now();
    }
}
