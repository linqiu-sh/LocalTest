package com.stubhub.messaging.networkInvoke.util;

import com.stubhub.messaging.networkInvoke.brazeModel.BrazeMessagingResponse;

public class BrazeMessagingResponseHelper {

    public static boolean isSuccess(BrazeMessagingResponse response){
        return "success".equalsIgnoreCase(response.getMessage());
    }
}
