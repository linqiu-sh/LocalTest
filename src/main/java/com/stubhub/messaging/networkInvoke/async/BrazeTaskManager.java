package com.stubhub.messaging.networkInvoke.async;

import com.stubhub.messaging.networkInvoke.brazeModel.BrazeMessagingRequest;
import com.stubhub.messaging.networkInvoke.brazeModel.BrazeResponseWrapper;
import com.stubhub.messaging.networkInvoke.repository.BrazeClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
@Data
public class BrazeTaskManager {

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

    private final ConcurrentHashMap<String, BrazeResponseWrapper> asyncResponseMap = new ConcurrentHashMap<>();


    private class OneStepFutureTask extends FutureTask<BrazeResponseWrapper> {
        OneStepFutureTask(RunnableFuture<BrazeResponseWrapper> task) {
            super(task, null);
            this.task = task;
        }
        private final Future<BrazeResponseWrapper> task;

        protected void done() {
            sendingResponsesHandlingTaskPool.submit(new BrazeHandleMsgResponseTask(task, asyncResponseMap));
        }
    }

//    private class LoopbackFutureTask extends FutureTask<BrazeResponseWrapper> {
//        LoopbackFutureTask(RunnableFuture<BrazeResponseWrapper> task) {
//            super(task, null);
//            this.task = task;
//        }
//        private final Future<BrazeResponseWrapper> task;
//
//        protected void done() {
//            sendingRequestsTaskPool.submit(new BrazeHandleMsgResponseTask(task, asyncResponseMap));
//        }
//    }

    public void submit(String messageId, BrazeMessagingRequest request) {
        if (request == null){
            return;
        }
        sendingRequestsTaskPool.submit(new OneStepFutureTask(new FutureTask<>(new BrazeSendMsgTask(request, brazeClient, messageId))));
        return;
    }


    public ConcurrentHashMap<String, BrazeResponseWrapper> getAsyncResponseMap() {
        return asyncResponseMap;
    }
}
