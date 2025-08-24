package com.genixo.education.search.common.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper customObjectMapper() {  // isim değiştirildi
        ObjectMapper objectMapper = new ObjectMapper();

        // Hibernate proxy ve lazy loading işlemlerini daha iyi yönetme
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        // Dairesel referansları ID ile değiştirmek için
        objectMapper.enable(SerializationFeature.WRITE_SELF_REFERENCES_AS_NULL);

        return objectMapper;
    }
}