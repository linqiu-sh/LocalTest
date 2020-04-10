package com.stubhub.messaging.networkInvoke.brazeModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrazeResponseWrapper {

    private String messageId;
    private BrazeResponse brazeResponse;
    private Exception exception;
    private boolean error;
}
