package com.genixo.education.search.repository.hr;

import com.genixo.education.search.entity.hr.ApplicationNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationNoteRepository extends JpaRepository<ApplicationNote, Long> {

    List<ApplicationNote> findByApplicationIdOrderByCreatedAtDesc(Long applicationId);
}
