package com.stubhub.messaging.networkInvoke.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="braze-client")
@Data
public class BrazeClientProperties {

    private String endpoint;
    private String apiKey;
    private String campaignId;
    private String subscriptionGroupId;
    private String sendCampaignMessagesEndpoint;
    private String getCampaignsListEndpoint;
    private String setSubscriptionGroupEndpoint;
    private int retryTimes;

}
