package com.stubhub.messaging.networkInvoke.brazeModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class BrazeSubscriptionGroupRequest {
    @JsonProperty(required = true, value = "api_key")
    private String apiKey;
    @JsonProperty("subscription_group_id")
    private String subscriptionGroupId;
    @JsonProperty("subscription_state")
    private String subscriptionState;
    @JsonProperty("external_id")
    private String externalId;
    @JsonProperty("phone")
    private String phone;
}
