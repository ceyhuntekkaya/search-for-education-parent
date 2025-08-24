package com.genixo.education.search.entity.content;

import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.enumaration.GalleryType;
import com.genixo.education.search.enumaration.GalleryVisibility;
import com.genixo.education.search.entity.institution.Brand;
import com.genixo.education.search.entity.institution.Campus;
import com.genixo.education.search.entity.institution.School;
import com.genixo.education.search.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "galleries")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Gallery extends BaseEntity {

    // Gallery can belong to Brand, Campus, or School
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campus_id")
    private Campus campus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdByUser;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "slug", unique = true)
    private String slug;

    @Enumerated(EnumType.STRING)
    @Column(name = "gallery_type", nullable = false)
    private GalleryType galleryType;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    private GalleryVisibility visibility = GalleryVisibility.PUBLIC;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "is_featured")
    private Boolean isFeatured = false;

    @Column(name = "allow_comments")
    private Boolean allowComments = true;

    @Column(name = "allow_downloads")
    private Boolean allowDownloads = false;

    // SEO
    @Column(name = "meta_title")
    private String metaTitle;

    @Column(name = "meta_description")
    private String metaDescription;

    @Column(name = "tags")
    private String tags; // Comma separated

    // Stats
    @Column(name = "item_count")
    private Long itemCount = 0L;

    @Column(name = "view_count")
    private Long viewCount = 0L;

    @Column(name = "download_count")
    private Long downloadCount = 0L;

    @Column(name = "total_size_bytes")
    private Long totalSizeBytes = 0L;

    // Relationships
    @OneToMany(mappedBy = "gallery", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC, createdAt DESC")
    private Set<GalleryItem> items = new HashSet<>();
}