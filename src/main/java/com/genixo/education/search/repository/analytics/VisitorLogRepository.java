package com.genixo.education.search.repository.analytics;

import com.genixo.education.search.entity.analytics.VisitorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitorLogRepository extends JpaRepository<VisitorLog, Long> {
}
