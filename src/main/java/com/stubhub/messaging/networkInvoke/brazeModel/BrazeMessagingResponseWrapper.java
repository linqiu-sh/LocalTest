package com.stubhub.messaging.networkInvoke.brazeModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrazeMessagingResponseWrapper {

    private String messageId;
    private BrazeMessagingResponse brazeMessagingResponse;
    private Exception exception;
    private boolean error;
}
