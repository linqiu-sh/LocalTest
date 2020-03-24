package com.stubhub.messaging.networkInvoke.async;

import com.stubhub.messaging.networkInvoke.brazeModel.BrazeMessagingRequest;
import com.stubhub.messaging.networkInvoke.brazeModel.BrazeMessagingResponse;
import com.stubhub.messaging.networkInvoke.brazeModel.BrazeMessagingResponseWrapper;
import com.stubhub.messaging.networkInvoke.repository.BrazeClient;

import java.util.concurrent.Callable;

public class BrazeSendMsgTask implements Callable<BrazeMessagingResponseWrapper> {

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
    public BrazeMessagingResponseWrapper call() {
        BrazeMessagingResponse brazeMessagingResponse = null;
        BrazeMessagingResponseWrapper brazeMessagingResponseWrapper = null;
        try {
            brazeMessagingResponse = brazeClient.sendCampaignMsg(brazeMessagingRequest);
            brazeMessagingResponseWrapper = new BrazeMessagingResponseWrapper(messageId, brazeMessagingResponse, null, false);
        }catch (Exception e){
            brazeMessagingResponseWrapper = new BrazeMessagingResponseWrapper(messageId, null, e, true);
        }
        System.out.println(Thread.currentThread() + ":");
        System.out.println("messageId:"+messageId);
        System.out.println("response:"+brazeMessagingResponse.toString());
        System.out.println("exception:"+brazeMessagingResponseWrapper.getException());
        return brazeMessagingResponseWrapper;
    }

}
