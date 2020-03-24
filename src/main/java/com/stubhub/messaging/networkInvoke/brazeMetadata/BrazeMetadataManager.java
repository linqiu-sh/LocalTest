package com.stubhub.messaging.networkInvoke.brazeMetadata;

import com.stubhub.messaging.networkInvoke.async.BrazeRequestManager;
import com.stubhub.messaging.networkInvoke.repository.BrazeClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Data
public class BrazeMetadataManager {

    private volatile BrazeMetadata brazeMetadata;
    private volatile boolean isUpdating;
    private volatile Timestamp lastUpdateTime;
    private volatile Future<BrazeMetadata> future;

    @Autowired
    private ExecutorService brazeMetadataTaskPool;
    @Autowired
    private BrazeClient brazeClient;

    @PostConstruct
    public void init(){
        requestUpdateMetadata();
    }

    private class QueueingFuture extends FutureTask<BrazeMetadata> {
        QueueingFuture(RunnableFuture<BrazeMetadata> task) {
            super(task, null);
            this.task = task;
        }

        private final Future<BrazeMetadata> task;

        protected void done() {
            try {
                brazeMetadata = task.get();
            } catch (InterruptedException e) {
                //TODO Log interrupt
                e.printStackTrace();
            } catch (ExecutionException e) {
                //TODO Log interrupt
                //TODO ERROR Handling
                e.printStackTrace();
            }
            isUpdating = false;
            lastUpdateTime = new Timestamp(System.currentTimeMillis());
            notifyAll();
        }
    }

    public synchronized void requestUpdateMetadata() {
        if (isUpdating){
            return;
        }
        isUpdating = true;
        future = (Future<BrazeMetadata>) brazeMetadataTaskPool.submit(new QueueingFuture(new FutureTask<>(new BrazeMetadataUpdateTask(brazeClient))));
    }

    public BrazeMetadata getBrazeMetadataSync() {
        if (future == null){
            requestUpdateMetadata();
        }

        try {
            future.get();   //just to wait
        } catch (ExecutionException e) {
        } catch (InterruptedException e) {
        }

        return brazeMetadata;
    }
}
