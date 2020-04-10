package com.stubhub.messaging.networkInvoke.service;

import com.stubhub.messaging.networkInvoke.async.BrazeTriggerMode;
import com.stubhub.messaging.networkInvoke.model.BrazeUnifiedRequest;
import com.stubhub.messaging.networkInvoke.model.BrazeUnifiedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BrazeService {

    @Autowired
    private BrazeRequestManager brazeRequestManager;

    public BrazeUnifiedResponse sendByBraze(String messageId, BrazeUnifiedRequest request, BrazeTriggerMode mode){


        BrazeUnifiedResponse brazeUnifiedResponse;

        if ( mode == null || mode.equals(BrazeTriggerMode.ASYNC)){
            // send request async
            brazeUnifiedResponse = brazeRequestManager.sendBrazeMessageAsync(messageId, request);
        }else{
            // send request sync
            brazeUnifiedResponse = brazeRequestManager.sendBrazeMessageSync(messageId, request);
        }

        return brazeUnifiedResponse;
    }



}
