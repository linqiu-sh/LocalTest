package com.stubhub.messaging.networkInvoke.brazeModel;

import lombok.Data;

import java.sql.Timestamp;

@Data(staticConstructor = "messageId")
public class BrazeRequestStatus {
    private final String messageId;
    private final Timestamp creationTime = new Timestamp(System.currentTimeMillis());
    private int retryTimes;
}
