package com.genixo.education.search.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class IdNotAvailableErrorInfo extends BaseErrorInfo{

    public IdNotAvailableErrorInfo(){
        super("idNotAvailableError");
    }
}
