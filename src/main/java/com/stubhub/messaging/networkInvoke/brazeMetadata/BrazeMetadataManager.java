package com.stubhub.messaging.networkInvoke.brazeMetadata;

import com.stubhub.messaging.networkInvoke.exception.BrazeBusinessException;
import com.stubhub.messaging.networkInvoke.repository.BrazeClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.concurrent.*;

@Slf4j
@Data
@Component
public class BrazeMetadataManager {

    private volatile BrazeMetadata brazeMetadata;   //snapshot
    private volatile boolean isUpdating;
    private volatile Timestamp lastUpdateTime;

    private CountDownLatch latch;

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

                isUpdating = false;
                lastUpdateTime = new Timestamp(System.currentTimeMillis());
                latch.countDown();
            } catch (InterruptedException e) {
                log.error("BrazeMetadata Updating is interrupted! Exception={}", e.toString());
            } catch (ExecutionException e) {
                log.error("BrazeMetadata Updating contains ExecutionException, reupdating! Exception={}", e.toString());
                brazeMetadataTaskPool.submit(new QueueingFuture(new FutureTask<>(new BrazeMetadataUpdateTask(brazeClient))));
            }

        }
    }

    public synchronized void requestUpdateMetadata() {
        if (isUpdating){
            return;
        }
        isUpdating = true;
        latch = new CountDownLatch(1);
        brazeMetadataTaskPool.submit(new QueueingFuture(new FutureTask<>(new BrazeMetadataUpdateTask(brazeClient))));
    }


    public String getCampaignIdbyTemplateName(String templateName) throws BrazeBusinessException {
        if (brazeMetadata != null){
            CampaignMetadata campaignMetadata = brazeMetadata.getCampaignMetadataByName(templateName);
            if (campaignMetadata != null){
                String id = campaignMetadata.getId();
                if (id != null && !id.isEmpty()){
                    return id;  //success
                }
            }
        }

        // metadata == null or not contains the campaign (may be newly added)
        // update metadata
        requestUpdateMetadata();

        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error("Wait for Compaign Id is interrupted! TemplateName={}, exception={}", templateName, e.toString());
            throw new BrazeBusinessException("Wait for Compaign Id is interrupted!");
        }

        return getCampaignIdbyTemplateName(templateName);

    }

}
