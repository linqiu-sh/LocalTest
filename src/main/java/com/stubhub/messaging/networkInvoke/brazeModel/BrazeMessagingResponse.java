package com.stubhub.messaging.networkInvoke.brazeModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BrazeMessagingResponse {

    @JsonProperty(required = true, value = "message")
    private String message;
    @JsonProperty("dispatch_id")
    private String dispatchId;
    @JsonProperty("send_id")
    private String sendId;
}
