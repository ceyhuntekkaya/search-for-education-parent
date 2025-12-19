package com.genixo.education.search.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Async & Scheduling Configuration
 * Materialized view refresh için gerekli
 */
@Configuration
@EnableAsync
@EnableScheduling
public class AsyncSchedulingConfig {

    // Spring Boot otomatik olarak application.yml'deki
    // spring.task.execution ve spring.task.scheduling
    // ayarlarını kullanacak
}