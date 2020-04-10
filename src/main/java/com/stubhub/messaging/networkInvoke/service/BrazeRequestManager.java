package com.stubhub.messaging.networkInvoke.service;

import com.stubhub.messaging.networkInvoke.async.BrazeTaskManager;
import com.stubhub.messaging.networkInvoke.brazeMetadata.BrazeMetadataManager;
import com.stubhub.messaging.networkInvoke.brazeModel.*;
import com.stubhub.messaging.networkInvoke.exception.BrazeBusinessException;
import com.stubhub.messaging.networkInvoke.exception.BrazeClientException;
import com.stubhub.messaging.networkInvoke.model.BrazeUnifiedRequest;
import com.stubhub.messaging.networkInvoke.model.BrazeUnifiedResponse;
import com.stubhub.messaging.networkInvoke.properties.BrazeClientProperties;
import com.stubhub.messaging.networkInvoke.repository.BrazeClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class BrazeRequestManager {
    @Autowired
    private BrazeClient brazeClient;
    @Autowired
    private BrazeTaskManager brazeTaskManager;
    @Autowired
    private BrazeClientProperties brazeClientProperties;
    @Autowired
    private BrazeMetadataManager brazeMetadataManager;

    public BrazeUnifiedResponse sendBrazeMessageAsync(String messageId, BrazeUnifiedRequest brazeUnifiedRequest){
        BrazeUnifiedResponse.BrazeUnifiedResponseBuilder brazeResponseBuilder = BrazeUnifiedResponse.builder().messageId(messageId);

        BrazeMessagingRequest brazeMessagingRequest;

        try {
            brazeMessagingRequest = buildBrazeMessagingRequest(brazeUnifiedRequest);
        } catch (BrazeBusinessException e) {
            brazeResponseBuilder.returnMessage(e.getMessage());
            return brazeResponseBuilder.build();
        }

        // send request async
        brazeTaskManager.submit(messageId, brazeMessagingRequest);

        brazeResponseBuilder.returnMessage("submitted");

        return brazeResponseBuilder.build();
    }

    public BrazeUnifiedResponse sendBrazeMessageSync(String messageId, BrazeUnifiedRequest brazeUnifiedRequest){
        BrazeUnifiedResponse.BrazeUnifiedResponseBuilder brazeResponseBuilder = BrazeUnifiedResponse.builder().messageId(messageId);

        BrazeMessagingRequest brazeMessagingRequest;

        try {
            brazeMessagingRequest = buildBrazeMessagingRequest(brazeUnifiedRequest);
        } catch (BrazeBusinessException e) {
            brazeResponseBuilder.returnMessage(e.getMessage());
            return brazeResponseBuilder.build();
        }

        if ("SMS".equalsIgnoreCase(brazeUnifiedRequest.getMode())){
            BrazeSubscriptionGroupRequest brazeSubscriptionGroupRequest = buildBrazeSubscriptionGroupRequest(brazeUnifiedRequest);
            try {
                brazeClient.setUserSubscriptionGroup(brazeSubscriptionGroupRequest);
            }catch (BrazeClientException e){
                // TODO unified error handling needed!!
                log.error("user Subscription failed!!! sms not send!!!");
                brazeResponseBuilder.returnMessage(e.getMessage());
                return brazeResponseBuilder.build();
            }

        }

        try {
            BrazeMessagingResponse brazeMessagingResponse = brazeClient.sendCampaignMsg(brazeMessagingRequest);
            brazeResponseBuilder.returnMessage(brazeMessagingResponse.getMessage());
        }catch (BrazeClientException e){
            brazeResponseBuilder.returnMessage(e.getMessage());
        }


        return brazeResponseBuilder.build();
    }

    public BrazeUnifiedResponse getMessageResponseAsync(String messageId){
        ConcurrentHashMap<String, BrazeResponseWrapper> asyncResponseMap = brazeTaskManager.getAsyncResponseMap();
        BrazeResponseWrapper brazeResponseWrapper = asyncResponseMap.get(messageId);
        BrazeUnifiedResponse.BrazeUnifiedResponseBuilder brazeResponseBuilder = BrazeUnifiedResponse.builder().messageId(messageId);
        if (brazeResponseWrapper.isError()){
            brazeResponseBuilder.returnMessage(brazeResponseWrapper.getException().toString());
        }else{
            brazeResponseBuilder.returnMessage(brazeResponseWrapper.getBrazeResponse().getMessage());
        }
        return brazeResponseBuilder.build();

    }


    private BrazeMessagingRequest buildBrazeMessagingRequest(BrazeUnifiedRequest request) throws BrazeBusinessException {

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

    private BrazeSubscriptionGroupRequest buildBrazeSubscriptionGroupRequest(BrazeUnifiedRequest request){
        BrazeSubscriptionGroupRequest.BrazeSubscriptionGroupRequestBuilder builder = BrazeSubscriptionGroupRequest.builder();

        // 1. set api_key
        builder.apiKey(brazeClientProperties.getApiKey());

        // 2. set target user
        String guid = request.getUserGuid();
        if (guid == null || guid.isEmpty()){
            //TODO raise exception
            guid = "alex_qiu_test";
        }
        builder.externalId(guid);

        // 3. set subscription group
        builder.subscriptionGroupId(brazeClientProperties.getSubscriptionGroupId());

        // 4. set to subscribed
        builder.subscriptionState("subscribed");

        // 5. set phone number
        String phone = request.getPhone();
        if (phone == null || phone.isEmpty()){
            // TODO raise exception
            phone = "+86 13817933899";
        }
        builder.phone(phone);

        return builder.build();
    }
}
