package com.genixo.education.search.repository.content;

import com.genixo.education.search.entity.content.GalleryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GalleryItemRepository extends JpaRepository<GalleryItem, Long> {
}
