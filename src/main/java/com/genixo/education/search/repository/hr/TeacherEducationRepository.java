package com.genixo.education.search.repository.hr;

import com.genixo.education.search.entity.hr.TeacherEducation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherEducationRepository extends JpaRepository<TeacherEducation, Long> {

    List<TeacherEducation> findByTeacherProfileIdOrderByDisplayOrderAscEndYearDesc(Long teacherProfileId);
}
