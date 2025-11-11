package com.genixo.education.search.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ValidationException extends Exception{

    private BaseErrorInfo baseErrorInfo;
    public ValidationException(String validationValue){
        setBaseErrorInfo(new ValidationErrorInfo(validationValue));
    }
    public ValidationErrorInfo getBaseErrorInfo() {
        return (ValidationErrorInfo) baseErrorInfo;
    }
}
