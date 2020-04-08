package com.stubhub.messaging.networkInvoke.async;

import com.stubhub.messaging.networkInvoke.brazeModel.BrazeMessagingResponse;
import com.stubhub.messaging.networkInvoke.brazeModel.BrazeMessagingResponseWrapper;
import com.stubhub.messaging.networkInvoke.exception.BrazeBusinessException;
import com.stubhub.messaging.networkInvoke.exception.BrazeClientException;
import com.stubhub.messaging.networkInvoke.util.BrazeMessagingResponseHelper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
public class BrazeHandleMsgResponseTask implements Runnable {

    private Future<BrazeMessagingResponseWrapper> responseFuture;

    private ConcurrentHashMap<String, BrazeMessagingResponseWrapper> asyncResponseMap;

    private BrazeHandleMsgResponseTask(){}

    public BrazeHandleMsgResponseTask(Future<BrazeMessagingResponseWrapper> responseFuture,
                                      ConcurrentHashMap<String, BrazeMessagingResponseWrapper> asyncResponseMap) {
        this.responseFuture = responseFuture;
        this.asyncResponseMap = asyncResponseMap;
    }

    @Override
    public void run() {
        // TODO universal log is needed

        BrazeMessagingResponseWrapper brazeMessagingResponseWrapper;
        try {
            brazeMessagingResponseWrapper = responseFuture.get();
        } catch (InterruptedException e) {
            log.warn("Thread Interrupted in BrazeHandleMsgResponseTask! Thread={}, Exception={}", Thread.currentThread(), e.toString());
            return;
        } catch (ExecutionException e) {
            //should not be ExecutionException occured, wrapped in class BrazeSendMsgTask
            Throwable cause = e.getCause();
            log.error("Exception in BrazeHandleMsgResponseTask! Thread={}, Exception={}", Thread.currentThread(), cause.toString());
            return;
        }

        if (brazeMessagingResponseWrapper.isError()){
            // should be
            Exception exception = brazeMessagingResponseWrapper.getException();
            if (exception instanceof BrazeClientException){
                BrazeClientException brazeClientException = (BrazeClientException) exception;
                log.error("Braze Client Exception handled in BrazeHandleMsgResponseTask! HTTPStatus={}, ExceptionBody={}, ExceptionMessage={}"
                        , brazeClientException.getStatusCode(), brazeClientException.getBody(), brazeClientException.getMessage());

                //TODO braze client error, resend or not?
                //TODO current not resend

                return;

            }
        }else {
            if (!BrazeMessagingResponseHelper.isSuccess(brazeMessagingResponseWrapper.getBrazeMessagingResponse())){
                // TODO braze business error, resend or not?
                //TODO CURRENT not resend
                BrazeMessagingResponse brazeMessagingResponse = brazeMessagingResponseWrapper.getBrazeMessagingResponse();

//                BrazeBusinessException brazeBusinessException = new BrazeBusinessException(brazeMessagingResponse.getMessage());
//                brazeMessagingResponseWrapper.setError(true);
//                brazeMessagingResponseWrapper.setException(brazeBusinessException);

                log.error("Braze Business Exception handled in BrazeHandleMsgResponseTask! BrazeMessage={}", brazeMessagingResponse.getMessage());
                return;
            }
        }

        log.info("Braze Request successfully end!");

        //TODO callback may be more acceptable
        //TODO put item here, need to take elsewhere, be aware of memory
//        asyncResponseMap.put(brazeMessagingResponseWrapper.getMessageId(), brazeMessagingResponseWrapper);


        //handle exception except BrazeClientException
//        BrazeBusinessException brazeBusinessException = new BrazeBusinessException();
//        brazeBusinessException.setException(e.getCause());
//        brazeBusinessException.setMessage(e.getMessage());
    }
}
