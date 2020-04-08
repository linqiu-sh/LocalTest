package com.stubhub.messaging.networkInvoke.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class BrazeResponse {
    private final String messageId;
    private final String returnMessage;
}
