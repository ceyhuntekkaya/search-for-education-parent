package com.genixo.education.search.repository.webinar;

import com.genixo.education.search.entity.webinar.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventCategoryRepository extends JpaRepository<EventCategory, Long> {

    List<EventCategory> findByIsActiveTrueOrderByDisplayOrderAsc();
}
