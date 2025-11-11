package com.genixo.education.search.repository;

import com.genixo.education.search.entity.FileUpload;
import com.genixo.education.search.enumaration.MediaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileUploadRepository extends JpaRepository<FileUpload, Long> {
    List<FileUpload> findByCreatedBy(Long userId);
    List<FileUpload> findByMediaType(MediaType mediaType);
}