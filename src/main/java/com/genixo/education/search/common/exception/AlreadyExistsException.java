package com.genixo.education.search.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AlreadyExistsException extends Exception{

    private BaseErrorInfo baseErrorInfo;

    public AlreadyExistsException(String name) {
        setBaseErrorInfo(new AlreadyExistsErrorInfo(name));
    }

    public AlreadyExistsErrorInfo getBaseErrorInfo() {
        return (AlreadyExistsErrorInfo) baseErrorInfo;
    }
}
