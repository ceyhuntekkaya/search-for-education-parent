package com.genixo.education.search.common.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class BaseErrorInfo {

    String errorKey;
    Long timestamp;

    public BaseErrorInfo(String errorKey){
        setErrorKey(errorKey);
        setTimestamp(new Date().getTime());
    }
}
