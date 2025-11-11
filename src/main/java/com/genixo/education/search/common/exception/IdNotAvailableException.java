package com.genixo.education.search.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class IdNotAvailableException extends Exception{

    private BaseErrorInfo baseErrorInfo;
    public IdNotAvailableException(){
        setBaseErrorInfo(new IdNotAvailableErrorInfo());
    }
    public IdNotAvailableErrorInfo getBaseErrorInfo() {
        return (IdNotAvailableErrorInfo) baseErrorInfo;
    }
}
