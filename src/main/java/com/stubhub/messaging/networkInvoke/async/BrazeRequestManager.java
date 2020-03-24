package com.stubhub.messaging.networkInvoke.async;

import com.stubhub.messaging.networkInvoke.brazeModel.BrazeMessagingRequest;
import com.stubhub.messaging.networkInvoke.brazeModel.BrazeMessagingResponse;
import com.stubhub.messaging.networkInvoke.repository.BrazeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
public class BrazeRequestManager {

    @Autowired
    private ExecutorService sendingRequestsTaskPool;
    @Autowired
    private ExecutorService sendingResponsesHandlingTaskPool;
    @Autowired
    private BlockingDeque<Runnable> sendingRequests;
    @Autowired
    private BlockingQueue<Runnable> sendingResponses;
    @Autowired
    private BrazeClient brazeClient;


    private class QueueingFuture extends FutureTask<BrazeMessagingResponse> {
        QueueingFuture(RunnableFuture<BrazeMessagingResponse> task) {
            super(task, null);
            this.task = task;
        }
        private final Future<BrazeMessagingResponse> task;

        protected void done() {
            sendingResponsesHandlingTaskPool.submit(new BrazeHandleMsgResponseTask(task));
        }
    }

    public void submit(String messageId, BrazeMessagingRequest request) {
        if (request == null){
            return;
        }
        sendingRequestsTaskPool.submit(new QueueingFuture(new FutureTask<>(new BrazeSendMsgTask(request, brazeClient))));
        return;
    }

}
