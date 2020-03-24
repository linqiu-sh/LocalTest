package com.stubhub.messaging.networkInvoke.brazeModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Recipient {
    @JsonProperty("external_user_id")
    private String externalUserId;
    @JsonProperty("send_to_existing_only")
    private Boolean sendToExistingOnly;
    @JsonProperty("trigger_properties")
    private Object triggerProperties;
    @JsonProperty("attributes")
    private Attributes attributes;
}
