package com.stubhub.messaging.networkInvoke.brazeModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class BrazeMessagingRequest {

    @JsonProperty(required = true, value = "api_key")
    private String apiKey;
    @JsonProperty("campaign_id")
    private String campaignId;
    @JsonProperty("send_id")
    private String sendId;
    @JsonProperty("recipients")
    private List<Recipient> recipients;

}
