package com.stubhub.messaging.networkInvoke.service;

import com.stubhub.messaging.networkInvoke.async.BrazeRequestManager;
import com.stubhub.messaging.networkInvoke.async.BrazeSendMsgTask;
import com.stubhub.messaging.networkInvoke.async.BrazeTriggerMode;
import com.stubhub.messaging.networkInvoke.brazeMetadata.BrazeMetadata;
import com.stubhub.messaging.networkInvoke.brazeMetadata.BrazeMetadataManager;
import com.stubhub.messaging.networkInvoke.brazeMetadata.CampaignMetadata;
import com.stubhub.messaging.networkInvoke.brazeModel.*;
import com.stubhub.messaging.networkInvoke.exception.BrazeBusinessException;
import com.stubhub.messaging.networkInvoke.exception.BrazeClientException;
import com.stubhub.messaging.networkInvoke.model.BrazeRequest;
import com.stubhub.messaging.networkInvoke.model.BrazeResponse;
import com.stubhub.messaging.networkInvoke.properties.BrazeClientProperties;
import com.stubhub.messaging.networkInvoke.repository.BrazeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BrazeService {

    @Autowired
    private BrazeClientProperties brazeClientProperties;
    @Autowired
    private BrazeClient brazeClient;
    @Autowired
    private BrazeRequestManager brazeRequestManager;
    @Autowired
    private BrazeMetadataManager brazeMetadataManager;

    public BrazeResponse sendByBraze(String messageId, BrazeRequest request, BrazeTriggerMode mode){

        BrazeResponse.BrazeResponseBuilder brazeResponseBuilder = BrazeResponse.builder().messageId(messageId);

        BrazeMessagingRequest brazeMessagingRequest;

        try {
            brazeMessagingRequest = buildBrazeMessagingRequest(request);
        } catch (BrazeBusinessException e) {
            brazeResponseBuilder.returnMessage(e.getMessage());
            return brazeResponseBuilder.build();
        }


        if ( mode == null || mode.equals(BrazeTriggerMode.ASYNC)){
            // send request async
            brazeRequestManager.submit(messageId, brazeMessagingRequest);
            brazeResponseBuilder.returnMessage("submitted");
        }else{
            // send request sync
            try {
                BrazeMessagingResponse brazeMessagingResponse = brazeClient.sendCampaignMsg(brazeMessagingRequest);
                brazeResponseBuilder.returnMessage(brazeMessagingResponse.getMessage());
            }catch (BrazeClientException e){
                brazeResponseBuilder.returnMessage(e.getMessage());
            }


        }

        return brazeResponseBuilder.build();
    }

    private BrazeMessagingRequest buildBrazeMessagingRequest(BrazeRequest request) throws BrazeBusinessException {

        BrazeMessagingRequest.BrazeMessagingRequestBuilder builder = BrazeMessagingRequest.builder();

        // 1. set api_key
        builder.apiKey(brazeClientProperties.getApiKey());

        // 2. set template id or campaign name or ...
        String templateName = request.getTemplateName();

        String campaignId = brazeMetadataManager.getCampaignIdbyTemplateName(templateName);
        builder.campaignId(campaignId);

        // 3. set recipient
        Recipient recipient = new Recipient();
        // 1) set target user
        String guid = request.getUserGuid();
        if (guid == null || guid.isEmpty()){
            //TODO raise exception
            guid = "alex_qiu_test";
        }
        recipient.setExternalUserId(guid);
        recipient.setSendToExistingOnly(false);
        // 2) set user attribute
        Attributes attributes = new Attributes();
        String email = request.getEmail();
        if (email == null || email.isEmpty()){
            //TODO raise exception
            email = "linqiu@stubhub.com";
        }
        attributes.setEmail(email);
        recipient.setAttributes(attributes);
        // 3) set payload
        recipient.setTriggerProperties(request.getPayload());

        builder.recipients(Arrays.asList(recipient));

        // build request
        return builder.build();
    }


//    private String getCampaignIdbyTemplateName(String templateName){
//        Timestamp now = new Timestamp(System.currentTimeMillis());
//        BrazeMetadata brazeMetadata = brazeMetadataManager.getBrazeMetadata();
//        if (brazeMetadata == null) {
//            brazeMetadataManager.requestUpdateMetadata();
//            brazeMetadata = brazeMetadataManager.getBrazeMetadataSync();
//        }
//        CampaignMetadata campaignMetadata = brazeMetadata.getCampaignMetadataByName(templateName);
//        if (campaignMetadata == null || campaignMetadata.getId() == null || campaignMetadata.getId().isEmpty()) {
//            if (brazeMetadataManager.getLastUpdateTime().after(now)){
//                return null;
//            }else{
//                //try to update
//                brazeMetadataManager.requestUpdateMetadata();
//                campaignMetadata = brazeMetadataManager.getBrazeMetadataSync().getCampaignMetadataByName(templateName);
//            }
//        }
//        return campaignMetadata.getId();
//    }

    public BrazeResponse getMessageResponseAsync(String messageId){
        ConcurrentHashMap<String, BrazeMessagingResponseWrapper> asyncResponseMap = brazeRequestManager.getAsyncResponseMap();
        BrazeMessagingResponseWrapper brazeMessagingResponseWrapper = asyncResponseMap.get(messageId);
        BrazeResponse.BrazeResponseBuilder brazeResponseBuilder = BrazeResponse.builder().messageId(messageId);
        if (brazeMessagingResponseWrapper.isError()){
            brazeResponseBuilder.returnMessage(brazeMessagingResponseWrapper.getException().toString());
        }else{
            brazeResponseBuilder.returnMessage(brazeMessagingResponseWrapper.getBrazeMessagingResponse().getMessage());
        }
        return brazeResponseBuilder.build();

    }
}
