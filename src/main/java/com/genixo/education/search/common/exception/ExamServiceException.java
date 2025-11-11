package com.genixo.education.search.common.exception;

public class ExamServiceException extends RuntimeException {
    public ExamServiceException(String message) {
        super(message);
    }

    public ExamServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
