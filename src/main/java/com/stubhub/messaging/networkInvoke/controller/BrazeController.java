package com.stubhub.messaging.networkInvoke.controller;

import com.stubhub.messaging.networkInvoke.BrazeAPI;
import com.stubhub.messaging.networkInvoke.async.BrazeTriggerMode;
import com.stubhub.messaging.networkInvoke.model.*;
import com.stubhub.messaging.networkInvoke.properties.BrazeClientProperties;
import com.stubhub.messaging.networkInvoke.repository.BrazeClient;
import com.stubhub.messaging.networkInvoke.repository.CommonClient;
import com.stubhub.messaging.networkInvoke.service.BrazeService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class BrazeController implements BrazeAPI {

    @Autowired
    private BrazeService brazeService;
    @Autowired
    private BrazeClientProperties brazeClientProperties;
    @Autowired
    private BrazeClient brazeClient;
    @Autowired
    private CommonClient commonClient;

    /***
     * only used for test!!!
     * @return
     */
    @Override
    public String home(){
        return brazeClient.listAllCampaigns().toString();
    }

    /**
     *    Type=Strong
     *    Type=V2
     *    Type=V1                  Type=V3(now V1)                Type=BrazeRequest(from V1 to Braze)
     * -----------> req transform -----------------> v3 platform ------------------------------------> BrazeChannel
     * @param request
     * @return
     */
    @Override
    public MessageResponse sendByBraze(@Valid MessageRequest request) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String messageId = timestamp.toString() +"_"+ request.getUserId() + "_" + request.getMessageName();

        BrazeTriggerMode brazeMode = BrazeTriggerMode.ASYNC;

        BrazeRequest brazeRequest = requestTransformToBraze(request);
        log.info("Braze Request: " + brazeRequest);

        BrazeResponse brazeResponse = brazeService.sendByBraze(messageId, brazeRequest, brazeMode);

        log.info("Braze Response: " + brazeResponse);

        MessageResponse messageResponse = responseTransformFromBraze(brazeResponse);

        return messageResponse;
    }

    /***
     * V3 request to Braze Request
     * @param request
     * @return
     */
    private BrazeRequest requestTransformToBraze(MessageRequest request){
        BrazeRequest.BrazeRequestBuilder builder = BrazeRequest.builder();

        String userId = request.getUserId();
        builder.userId(userId);

        // TODO trigger moved to central repository
        String userGuid = commonClient.getUserGuidById(userId);
        if (userGuid == null || userGuid.isEmpty()){
            //TODO raise exception here
            log.error("GUID id null!!!");
        }
        builder.userGuid(userGuid);

        builder.templateName(request.getMessageName());

        // TODO record trigger
        builder.trigger("Testing trigger");


        builder.locale(request.getLocale().getDisplayName());

        builder.messageTimestamp(request.getMessageTimestamp());

        builder.mode(request.getMode());

        List<Data> datalist = request.getDatalist();

        Map<String, Object> payload = new HashMap<>();
        if (datalist != null){
            for (Data data: datalist){
                payload.put(data.getKey(), data.getValue());
                if ("email_address".equalsIgnoreCase(data.getKey())){
                    builder.email(data.getValue().toString());
                }
            }
        }
        builder.payload(payload);

        return builder.build();

    }


    private MessageResponse responseTransformFromBraze(BrazeResponse brazeResponse){
        return new MessageResponse(brazeResponse.getMessageId());
    }


}
