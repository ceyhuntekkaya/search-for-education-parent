package com.genixo.education.search.entity.hr;

import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "application_notes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationNote extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User noteCreatedBy; // Notu ekleyen okul yöneticisi

    @Column(name = "note_text", columnDefinition = "TEXT", nullable = false)
    private String noteText;

}