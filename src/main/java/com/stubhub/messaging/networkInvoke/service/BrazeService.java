package com.stubhub.messaging.networkInvoke.service;

import com.stubhub.messaging.networkInvoke.async.BrazeRequestManager;
import com.stubhub.messaging.networkInvoke.async.BrazeSendMsgTask;
import com.stubhub.messaging.networkInvoke.async.BrazeTriggerMode;
import com.stubhub.messaging.networkInvoke.brazeMetadata.BrazeMetadata;
import com.stubhub.messaging.networkInvoke.brazeMetadata.BrazeMetadataManager;
import com.stubhub.messaging.networkInvoke.brazeMetadata.CampaignMetadata;
import com.stubhub.messaging.networkInvoke.brazeModel.*;
import com.stubhub.messaging.networkInvoke.model.MessageRequest;
import com.stubhub.messaging.networkInvoke.model.MessageResponse;
import com.stubhub.messaging.networkInvoke.properties.BrazeClientProperties;
import com.stubhub.messaging.networkInvoke.repository.BrazeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

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

    public MessageResponse sendByBraze(String messageId, MessageRequest request, BrazeTriggerMode mode){

        BrazeMessagingRequest.BrazeMessagingRequestBuilder builder = BrazeMessagingRequest.builder();

        // 1. set api_key
        builder.apiKey(brazeClientProperties.getApiKey());

        // 2. set template id or campaign name or ...
        String templateName = request.getTemplateName();
        String campaignId = getCampaignIdbyTemplateName(templateName);
        if (campaignId == null){
            //TODO log can not get templateName -> campaignId ERROR
            // throw exception
            return null;
        }
        builder.campaignId(campaignId);

        // 3. set recipient
        Recipient recipient = new Recipient();
        // 1) set target user
        String guid = request.getUserGuid();
        if (guid == null || guid.isEmpty()){
            //TODO get user guid by user id from user service
            guid = "alex_qiu_test";
        }
        recipient.setExternalUserId(guid);
        recipient.setSendToExistingOnly(false);
        // 2) set user attribute
        Attributes attributes = new Attributes();
        String email = request.getEmail();
        if (email == null || email.isEmpty()){
            //TODO get user email by user id from user service
            email = "linqiu@stubhub.com";
        }
        attributes.setEmail(email);
        recipient.setAttributes(attributes);
        // 3) set payload
        recipient.setTriggerProperties(request.getPayload());

        builder.recipients(Arrays.asList(recipient));

        // build request
        BrazeMessagingRequest brazeMessagingRequest = builder.build();

        if ( mode == null || mode.equals(BrazeTriggerMode.ASYNC)){
            // send request async
            brazeRequestManager.submit(messageId, brazeMessagingRequest);
        }else{
            // send request sync
            BrazeMessagingResponse brazeMessagingResponse = brazeClient.sendCampaignMsg(brazeMessagingRequest);
        }

        return null;

        // TODO messageId logic
//        MessageResponse messageResponse = MessageResponse.builder()
//                .messageId(brazeMessagingResponse.getDispatchId())
//                .returnMessage(brazeMessagingResponse.getMessage())
//                .build();

//        return messageResponse;
    }


    private String getCampaignIdbyTemplateName(String templateName){
        Timestamp now = new Timestamp(System.currentTimeMillis());
        BrazeMetadata brazeMetadata = brazeMetadataManager.getBrazeMetadata();
        if (brazeMetadata == null) {
            brazeMetadataManager.requestUpdateMetadata();
            brazeMetadata = brazeMetadataManager.getBrazeMetadataSync();
        }
        CampaignMetadata campaignMetadata = brazeMetadata.getCampaignMetadataByName(templateName);
        if (campaignMetadata == null || campaignMetadata.getId() == null || campaignMetadata.getId().isEmpty()) {
            if (brazeMetadataManager.getLastUpdateTime().after(now)){
                return null;
            }else{
                //try to update
                brazeMetadataManager.requestUpdateMetadata();
                campaignMetadata = brazeMetadataManager.getBrazeMetadataSync().getCampaignMetadataByName(templateName);
            }
        }
        return campaignMetadata.getId();
    }
}
