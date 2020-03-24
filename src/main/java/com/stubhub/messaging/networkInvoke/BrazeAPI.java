package com.stubhub.messaging.networkInvoke;

import com.stubhub.messaging.networkInvoke.model.MessageRequest;
import com.stubhub.messaging.networkInvoke.model.MessageResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

@RequestMapping(path = "/messaging/v3")
public interface BrazeAPI {

    @RequestMapping(method = RequestMethod.GET, path = "/")
    @ResponseBody String home();

    @RequestMapping(method = RequestMethod.POST, path = "/braze/send")
    @ResponseBody MessageResponse sendByBraze(@Valid @RequestBody MessageRequest request);
}
