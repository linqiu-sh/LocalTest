package com.stubhub.messaging.networkInvoke.controller;

import com.stubhub.messaging.networkInvoke.BrazeAPI;
import com.stubhub.messaging.networkInvoke.async.BrazeTriggerMode;
import com.stubhub.messaging.networkInvoke.model.MessageRequest;
import com.stubhub.messaging.networkInvoke.model.MessageResponse;
import com.stubhub.messaging.networkInvoke.properties.BrazeClientProperties;
import com.stubhub.messaging.networkInvoke.repository.BrazeClient;
import com.stubhub.messaging.networkInvoke.service.BrazeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.sql.Timestamp;

@RestController
public class BrazeController implements BrazeAPI {

    @Autowired
    private BrazeService brazeService;
    @Autowired
    private BrazeClientProperties brazeClientProperties;
    @Autowired
    private BrazeClient brazeClient;

    @Override
    public String home(){
        return brazeClient.listAllCampaigns().toString();
    }

    @Override
    public MessageResponse sendByBraze(@Valid MessageRequest request) {
        //TODO add message id logic here
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String messageId = timestamp.toString() +"_"+ request.getUserId() + "_" + request.getTemplateName();

        BrazeTriggerMode brazeMode;
        switch (request.getMode()){
            case "sync": brazeMode = BrazeTriggerMode.SYNC; break;
            case "async": brazeMode = BrazeTriggerMode.ASYNC; break;
            default: brazeMode = BrazeTriggerMode.ASYNC;

        }

        MessageResponse messageResponse = brazeService.sendByBraze(messageId, request, brazeMode);

        return messageResponse;
    }
}
