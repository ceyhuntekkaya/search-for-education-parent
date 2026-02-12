package com.genixo.education.search.entity.hr;


import com.genixo.education.search.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "application_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDocument extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Column(name = "document_name", nullable = false, length = 200)
    private String documentName; // Belge adı (örn: "diploma.pdf")

    @Column(name = "document_url", nullable = false, length = 500)
    private String documentUrl; // Dosya yolu/URL

    @Column(name = "document_type", length = 50)
    private String documentType; // Belge türü (diploma, sertifika, CV vb.)

    @Column(name = "file_size")
    private Long fileSize; // Dosya boyutu (bytes)

}