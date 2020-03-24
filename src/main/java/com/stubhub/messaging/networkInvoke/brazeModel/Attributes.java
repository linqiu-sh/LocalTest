package com.stubhub.messaging.networkInvoke.brazeModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Attributes {
    @JsonProperty("email")
    private String email;
}
