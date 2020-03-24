package com.stubhub.messaging.networkInvoke.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="brazeClient")
@Data
public class BrazeClientProperties {

    private String endpoint;
    private String apiKey;
    private String campaignId;
    private String sendCampaignMessagesEndpoint;
    private String getCampaignsListEndpoint;
    private int retryTimes;

}
