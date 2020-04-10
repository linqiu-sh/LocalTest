package com.stubhub.messaging.networkInvoke.util;

import com.stubhub.messaging.networkInvoke.brazeModel.BrazeResponse;

public class BrazeResponseHelper {

    public static boolean isSuccess(BrazeResponse response){
        return "success".equalsIgnoreCase(response.getMessage());
    }
}
