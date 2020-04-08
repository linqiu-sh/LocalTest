package com.stubhub.messaging.networkInvoke.model;

import com.fasterxml.jackson.annotation.JsonProperty;

@lombok.Data
public class Data {
    @JsonProperty("key")
    private String key;
    @JsonProperty("value")
    private Object value;
}
