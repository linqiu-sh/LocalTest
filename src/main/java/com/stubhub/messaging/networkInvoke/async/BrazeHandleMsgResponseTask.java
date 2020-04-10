package com.stubhub.messaging.networkInvoke.async;

import com.stubhub.messaging.networkInvoke.brazeModel.BrazeMessagingResponse;
import com.stubhub.messaging.networkInvoke.brazeModel.BrazeResponse;
import com.stubhub.messaging.networkInvoke.brazeModel.BrazeResponseWrapper;
import com.stubhub.messaging.networkInvoke.exception.BrazeClientException;
import com.stubhub.messaging.networkInvoke.util.BrazeResponseHelper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
public class BrazeHandleMsgResponseTask implements Runnable {

    private Future<BrazeResponseWrapper> responseFuture;

    private ConcurrentHashMap<String, BrazeResponseWrapper> asyncResponseMap;

    private BrazeHandleMsgResponseTask(){}

    public BrazeHandleMsgResponseTask(Future<BrazeResponseWrapper> responseFuture,
                                      ConcurrentHashMap<String, BrazeResponseWrapper> asyncResponseMap) {
        this.responseFuture = responseFuture;
        this.asyncResponseMap = asyncResponseMap;
    }

    @Override
    public void run() {
        // TODO universal log is needed

        BrazeResponseWrapper brazeResponseWrapper;
        try {
            brazeResponseWrapper = responseFuture.get();
        } catch (InterruptedException e) {
            log.warn("Thread Interrupted in BrazeHandleMsgResponseTask! Thread={}, Exception={}", Thread.currentThread(), e.toString());
            return;
        } catch (ExecutionException e) {
            //should not be ExecutionException occured, wrapped in class BrazeSendMsgTask
            Throwable cause = e.getCause();
            log.error("Exception in BrazeHandleMsgResponseTask! Thread={}, Exception={}", Thread.currentThread(), cause.toString());
            return;
        }

        if (brazeResponseWrapper.isError()){
            // should be
            Exception exception = brazeResponseWrapper.getException();
            if (exception instanceof BrazeClientException){
                BrazeClientException brazeClientException = (BrazeClientException) exception;
                log.error("Braze Client Exception handled in BrazeHandleMsgResponseTask! HTTPStatus={}, ExceptionBody={}, ExceptionMessage={}"
                        , brazeClientException.getStatusCode(), brazeClientException.getBody(), brazeClientException.getMessage());

                //TODO braze client error, resend or not?
                //TODO current not resend

                return;

            }
        }else {
            if (!BrazeResponseHelper.isSuccess(brazeResponseWrapper.getBrazeResponse())){
                // TODO braze business error, resend or not?
                //TODO CURRENT not resend
                BrazeResponse brazeResponse = brazeResponseWrapper.getBrazeResponse();

//                BrazeBusinessException brazeBusinessException = new BrazeBusinessException(brazeResponse.getMessage());
//                brazeResponseWrapper.setError(true);
//                brazeResponseWrapper.setException(brazeBusinessException);

                log.error("Braze Business Exception handled in BrazeHandleMsgResponseTask! BrazeMessage={}", brazeResponse.getMessage());
                return;
            }
        }

        log.info("Braze Request successfully end!");

        //TODO callback may be more acceptable
        //TODO put item here, need to take elsewhere, be aware of memory
//        asyncResponseMap.put(brazeResponseWrapper.getMessageId(), brazeResponseWrapper);


        //handle exception except BrazeClientException
//        BrazeBusinessException brazeBusinessException = new BrazeBusinessException();
//        brazeBusinessException.setException(e.getCause());
//        brazeBusinessException.setMessage(e.getMessage());
    }
}
