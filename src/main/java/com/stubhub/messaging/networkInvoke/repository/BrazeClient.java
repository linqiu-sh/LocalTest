package com.stubhub.messaging.networkInvoke.repository;

import com.stubhub.messaging.networkInvoke.brazeModel.*;
import com.stubhub.messaging.networkInvoke.properties.BrazeClientProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class BrazeClient {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private BrazeClientProperties brazeClientProperties;


    public BrazeMessagingResponse sendCampaignMsg(BrazeMessagingRequest request) throws RestClientException {
        String uri = brazeClientProperties.getEndpoint() + brazeClientProperties.getSendCampaignMessagesEndpoint();
        return restTemplate.postForObject(uri, request, BrazeMessagingResponse.class);

    }

    public BrazeListCampaignsResponse listAllCampaigns() throws RestClientException{
        String baseUri = brazeClientProperties.getEndpoint() + brazeClientProperties.getGetCampaignsListEndpoint();
        String uri = baseUri + "?api_key=" + brazeClientProperties.getApiKey();
        return restTemplate.getForObject(uri, BrazeListCampaignsResponse.class);
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

        BrazeMessagingResponse brazeMessagingResponse = restTemplate.postForObject(uri, payload, BrazeMessagingResponse.class);
        return brazeMessagingResponse.toString();
    }

}
