package com.genixo.education.search.repository.hr;

import com.genixo.education.search.entity.hr.TeacherExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherExperienceRepository extends JpaRepository<TeacherExperience, Long> {

    List<TeacherExperience> findByTeacherProfileIdOrderByDisplayOrderAscEndDateDesc(Long teacherProfileId);
}
