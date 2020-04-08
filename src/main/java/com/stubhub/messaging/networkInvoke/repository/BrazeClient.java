package com.stubhub.messaging.networkInvoke.repository;

import com.stubhub.messaging.networkInvoke.brazeModel.*;
import com.stubhub.messaging.networkInvoke.exception.BrazeClientException;
import com.stubhub.messaging.networkInvoke.properties.BrazeClientProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class BrazeClient {

    @Autowired
    @Qualifier("brazeRestTemplate")
    private RestTemplate brazeRestTemplate;

    @Autowired
    private BrazeClientProperties brazeClientProperties;


    public BrazeMessagingResponse sendCampaignMsg(BrazeMessagingRequest request) throws RestClientException {
        String uri = brazeClientProperties.getEndpoint() + brazeClientProperties.getSendCampaignMessagesEndpoint();

        BrazeMessagingResponse brazeMessagingResponse = null;
        int retryTimes = brazeClientProperties.getRetryTimes();
        boolean isError = false;
        do {
            try {
                brazeMessagingResponse = brazeRestTemplate.postForObject(uri, request, BrazeMessagingResponse.class);
            }catch (BrazeClientException e){
                isError = e.getStatusCode().isError();
                retryTimes--;
                if (isError){
                    log.error("Exception in BrazeClient.sendCampaignMsg(), retryTimes={}, exception={}", retryTimes, e.toString());
                    if (retryTimes == 0){
                        throw e;
                    }
                }
            }
        }while (isError && retryTimes > 0);

        return brazeMessagingResponse;

    }

    public BrazeListCampaignsResponse listAllCampaigns() throws RestClientException{
        String baseUri = brazeClientProperties.getEndpoint() + brazeClientProperties.getGetCampaignsListEndpoint();
        String uri = baseUri + "?api_key=" + brazeClientProperties.getApiKey();

        BrazeListCampaignsResponse brazeListCampaignsResponse = null;
        int retryTimes = brazeClientProperties.getRetryTimes();
        boolean isError = false;
        do {
            try {
                brazeListCampaignsResponse = brazeRestTemplate.getForObject(uri, BrazeListCampaignsResponse.class);
            } catch (BrazeClientException e){
                isError = e.getStatusCode().isError();
                retryTimes--;
                if (isError){
                    log.error("Exception in BrazeClient.listAllCampaigns(), retryTimes={}, exception={}", retryTimes, e.toString());
                    if (retryTimes == 0){
                        throw e;
                    }
                }
            }
        } while ( isError && retryTimes > 0 );

        return brazeListCampaignsResponse;
    }

//    public BrazeMessagingResponse sendCampaignMsgWithRetry(BrazeMessagingRequest request){
//        String uri = brazeClientProperties.getEndpoint() + brazeClientProperties.getSendCampaignMessagesEndpoint();
//        HttpEntity<BrazeMessagingRequest> entity = new RequestEntity<BrazeMessagingRequest>(request);
//        restTemplate.exchange()
//    }

    public String testSend(){

        String uri = brazeClientProperties.getEndpoint() + brazeClientProperties.getSendCampaignMessagesEndpoint();
        Map<String, Object> payload = new HashMap<>();
        payload.put("api_key", brazeClientProperties.getApiKey());
        payload.put("campaign_id", brazeClientProperties.getCampaignId());

        Recipient recipient = new Recipient();
        recipient.setExternalUserId("linqiu_test");
        recipient.setSendToExistingOnly(false);
        TestObj testObj = new TestObj();
        recipient.setTriggerProperties(testObj);
        Attributes attributes = new Attributes();
        attributes.setEmail("linqiu@stubhub.com");
        recipient.setAttributes(attributes);

        List<Recipient> recipientList = new ArrayList<>();
        recipientList.add(recipient);

        payload.put("recipients", recipientList);

        BrazeMessagingResponse brazeMessagingResponse = brazeRestTemplate.postForObject(uri, payload, BrazeMessagingResponse.class);
        return brazeMessagingResponse.toString();
    }

}
