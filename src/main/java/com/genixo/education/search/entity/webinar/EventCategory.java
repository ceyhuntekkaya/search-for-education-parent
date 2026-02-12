package com.genixo.education.search.entity.webinar;

import com.genixo.education.search.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "event_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventCategory extends BaseEntity {


    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name; // Kategori adı (örn: "Mesleki Gelişim")

    @Column(name = "slug", nullable = false, unique = true, length = 100)
    private String slug; // URL-friendly isim (örn: "mesleki-gelisim")

    @Column(name = "description", columnDefinition = "TEXT")
    private String description; // Kategori açıklaması

    @Column(name = "icon", length = 50)
    private String icon; // İkon adı (frontend için)

    @Column(name = "display_order")
    private Integer displayOrder; // Sıralama için

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Event> events = new ArrayList<>();

}