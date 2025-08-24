package com.genixo.education.search.repository.survey;

import com.genixo.education.search.entity.survey.SurveyQuestionResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyQuestionResponseRepository extends JpaRepository<SurveyQuestionResponse, Long> {
}
