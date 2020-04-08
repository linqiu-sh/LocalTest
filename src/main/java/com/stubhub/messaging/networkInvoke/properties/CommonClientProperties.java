package com.stubhub.messaging.networkInvoke.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="common-client")
@Data
public class CommonClientProperties {
    private String userCustomerEndpoint;
    private int retryTimes;
}
