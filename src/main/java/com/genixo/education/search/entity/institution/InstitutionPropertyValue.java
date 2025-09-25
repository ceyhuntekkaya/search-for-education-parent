package com.genixo.education.search.entity.institution;

import com.genixo.education.search.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "institution_property_values")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionPropertyValue extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "property_id", nullable = false)
    //@ToString.Exclude
    private InstitutionProperty property;

    // Either campus or school can have property values
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campus_id")
    @ToString.Exclude
    private Campus campus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    @ToString.Exclude
    private School school;

    @Column(name = "text_value", columnDefinition = "TEXT")
    private String textValue;

    @Column(name = "number_value")
    private Double numberValue;

    @Column(name = "boolean_value")
    private Boolean booleanValue;

    @Column(name = "date_value")
    private java.time.LocalDate dateValue;

    @Column(name = "datetime_value")
    private java.time.LocalDateTime datetimeValue;

    @Column(name = "json_value", columnDefinition = "JSON")
    private String jsonValue;

    // For file/image properties
    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "mime_type")
    private String mimeType;


}