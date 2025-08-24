package com.genixo.education.search.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class NotFoundErrorInfo extends BaseErrorInfo{
    public NotFoundErrorInfo(){
        super("notFoundErrorInfo");
    }
}
