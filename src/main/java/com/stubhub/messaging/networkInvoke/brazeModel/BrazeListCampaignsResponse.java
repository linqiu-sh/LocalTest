package com.stubhub.messaging.networkInvoke.brazeModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.stubhub.messaging.networkInvoke.brazeMetadata.CampaignMetadata;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BrazeListCampaignsResponse {

    @JsonProperty("campaigns")
    private List<CampaignMetadata> campaigns;
    @JsonProperty("message")
    private String message;
}
