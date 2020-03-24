package com.stubhub.messaging.networkInvoke.async;

import com.stubhub.messaging.networkInvoke.brazeModel.BrazeMessagingRequest;
import com.stubhub.messaging.networkInvoke.brazeModel.BrazeMessagingResponse;
import com.stubhub.messaging.networkInvoke.repository.BrazeClient;

import java.util.concurrent.Callable;

public class BrazeSendMsgTask implements Callable<BrazeMessagingResponse> {

    private BrazeMessagingRequest brazeMessagingRequest;
    private BrazeClient brazeClient;

    private BrazeSendMsgTask(){}

    public BrazeSendMsgTask(BrazeMessagingRequest brazeMessagingRequest, BrazeClient brazeClient) {
        this.brazeMessagingRequest = brazeMessagingRequest;
        this.brazeClient = brazeClient;
    }

    @Override
    public BrazeMessagingResponse call() {
        BrazeMessagingResponse brazeMessagingResponse = brazeClient.sendCampaignMsg(brazeMessagingRequest);
        System.out.println(Thread.currentThread() + ":");
        System.out.println(brazeMessagingResponse.toString());
        return brazeMessagingResponse;
    }


//    @Override
//    public void run() {
//        BrazeMessagingResponse brazeMessagingResponse = brazeClient.sendCampaignMsg(brazeMessagingRequest);
//        System.out.println(Thread.currentThread() + ":");
//        System.out.println(brazeMessagingResponse.toString());
//    }
}
