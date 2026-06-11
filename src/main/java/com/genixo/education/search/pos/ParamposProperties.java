package com.genixo.education.search.pos;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "parampos")
@Getter
@Setter
public class ParamposProperties {
    private String baseUrl;
    private String iframeBaseUrl;
    private String clientCode;
    private String clientUsername;
    private String clientPassword;
    private String guid;
    private String terminalId;
    private String callbackUrl;
}