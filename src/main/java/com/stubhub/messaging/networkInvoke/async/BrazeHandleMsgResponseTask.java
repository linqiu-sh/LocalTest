package com.stubhub.messaging.networkInvoke.async;

import com.stubhub.messaging.networkInvoke.brazeModel.BrazeMessagingResponse;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class BrazeHandleMsgResponseTask implements Runnable {

    private Future<BrazeMessagingResponse> responseFuture;

    private BrazeHandleMsgResponseTask(){}

    public BrazeHandleMsgResponseTask(Future<BrazeMessagingResponse> responseFuture) {
        this.responseFuture = responseFuture;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread()+":");
        System.out.println("Start to check msg response ....");
        BrazeMessagingResponse brazeMessagingResponse;
        try {
            brazeMessagingResponse = responseFuture.get();
        } catch (InterruptedException e) {
            //TODO log THE INTERRUPT
            e.printStackTrace();
            return;
        } catch (ExecutionException e) {
            //TODO log THE Execution ERROR

            Throwable cause = e.getCause();
            if (cause instanceof HttpClientErrorException){
                System.out.println("client error!!! \n" + cause.getMessage());
            }else if (cause instanceof HttpServerErrorException){
                System.out.println("server error!!! \n" + cause.getMessage());
            }else {
                System.out.println("other error !!!");
                e.printStackTrace();
            }
            return;
        }
    }
}
