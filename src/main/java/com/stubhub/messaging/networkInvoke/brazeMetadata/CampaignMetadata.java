package com.stubhub.messaging.networkInvoke.brazeMetadata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CampaignMetadata {
    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("is_api_campaign")
    private boolean isApiCampaign;
    @JsonProperty("tags")
    private List<String> tags;

}
