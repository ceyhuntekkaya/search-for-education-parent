package com.genixo.education.search.common.exception;

public class FileTypeNotAllowedException extends ExamServiceException {
    public FileTypeNotAllowedException(String message) {
        super(message);
    }

    public FileTypeNotAllowedException(String fileType, String allowedTypes) {
        super(String.format("File type '%s' is not allowed. Allowed types: %s", fileType, allowedTypes));
    }
}