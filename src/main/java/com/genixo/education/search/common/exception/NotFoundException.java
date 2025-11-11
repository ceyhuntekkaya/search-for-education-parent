package com.genixo.education.search.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class NotFoundException extends Exception {

    private BaseErrorInfo baseErrorInfo;
    public NotFoundException(){
        setBaseErrorInfo(new NotFoundErrorInfo());
    }
    public NotFoundErrorInfo getBaseErrorInfo() {
        return (NotFoundErrorInfo) baseErrorInfo;
    }
}