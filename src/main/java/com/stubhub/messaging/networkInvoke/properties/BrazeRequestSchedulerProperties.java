package com.stubhub.messaging.networkInvoke.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix="scheduler")
public class BrazeRequestSchedulerProperties {
    private int corePoolSize;
    private int maximumPoolSize;
    private long keepAliveTime;

}
