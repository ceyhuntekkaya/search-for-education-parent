package com.genixo.education.search.common.exception;

public class FileSizeExceededException extends ExamServiceException {
    public FileSizeExceededException(String message) {
        super(message);
    }

    public FileSizeExceededException(long fileSize, long maxSize) {
        super(String.format("File size %d bytes exceeds maximum allowed size %d bytes", fileSize, maxSize));
    }
}