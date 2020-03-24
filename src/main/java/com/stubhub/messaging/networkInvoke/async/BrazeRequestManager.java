package com.stubhub.messaging.networkInvoke.async;

import com.stubhub.messaging.networkInvoke.brazeModel.BrazeMessagingRequest;
import com.stubhub.messaging.networkInvoke.brazeModel.BrazeMessagingResponse;
import com.stubhub.messaging.networkInvoke.brazeModel.BrazeMessagingResponseWrapper;
import com.stubhub.messaging.networkInvoke.repository.BrazeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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

    private final ConcurrentHashMap<String, BrazeMessagingResponseWrapper> asyncResponseMap = new ConcurrentHashMap<>();


    private class QueueingFuture extends FutureTask<BrazeMessagingResponseWrapper> {
        QueueingFuture(RunnableFuture<BrazeMessagingResponseWrapper> task) {
            super(task, null);
            this.task = task;
        }
        private final Future<BrazeMessagingResponseWrapper> task;

        protected void done() {
            sendingResponsesHandlingTaskPool.submit(new BrazeHandleMsgResponseTask(task, asyncResponseMap));
        }
    }

    public void submit(String messageId, BrazeMessagingRequest request) {
        if (request == null){
            return;
        }
        sendingRequestsTaskPool.submit(new QueueingFuture(new FutureTask<>(new BrazeSendMsgTask(request, brazeClient, messageId))));
        return;
    }


    public ConcurrentHashMap<String, BrazeMessagingResponseWrapper> getAsyncResponseMap() {
        return asyncResponseMap;
    }
}
