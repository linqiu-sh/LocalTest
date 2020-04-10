package com.stubhub.messaging.networkInvoke.async;

import com.stubhub.messaging.networkInvoke.brazeModel.BrazeMessagingRequest;
import com.stubhub.messaging.networkInvoke.brazeModel.BrazeMessagingResponse;
import com.stubhub.messaging.networkInvoke.brazeModel.BrazeResponseWrapper;
import com.stubhub.messaging.networkInvoke.repository.BrazeClient;

import java.util.concurrent.Callable;

public class BrazeSendMsgTask implements Callable<BrazeResponseWrapper> {

    private BrazeMessagingRequest brazeMessagingRequest;
    private BrazeClient brazeClient;
    private String messageId;

    private BrazeSendMsgTask(){}

    public BrazeSendMsgTask(BrazeMessagingRequest brazeMessagingRequest, BrazeClient brazeClient, String messageId) {
        this.brazeMessagingRequest = brazeMessagingRequest;
        this.brazeClient = brazeClient;
        this.messageId = messageId;
    }

    @Override
    public BrazeResponseWrapper call() {
        BrazeMessagingResponse brazeMessagingResponse = null;
        BrazeResponseWrapper brazeResponseWrapper = null;
        try {
            brazeMessagingResponse = brazeClient.sendCampaignMsg(brazeMessagingRequest);
            brazeResponseWrapper = new BrazeResponseWrapper(messageId, brazeMessagingResponse, null, false);
        }catch (Exception e){
            brazeResponseWrapper = new BrazeResponseWrapper(messageId, null, e, true);
        }
        // TODO remove sout
        System.out.println(Thread.currentThread() + ":");
        System.out.println("messageId:"+messageId);
        System.out.println("response:"+brazeMessagingResponse.toString());
        System.out.println("exception:"+ brazeResponseWrapper.getException());
        return brazeResponseWrapper;
    }

}
