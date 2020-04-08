package com.stubhub.messaging.networkInvoke.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder(toBuilder = true)
public class BrazeRequest {
    private @NotNull @Pattern(regexp = "\\d+") String userId;   //target user id
    private @Pattern(regexp = ".*\\S.*") String userGuid;       //target user guid
    private String email;   //target email
    private String phone;   //target phone number
    private @NotNull @Pattern(regexp = ".*\\S.*") String templateName;  //template name to send
    private @Valid Map<String, Object> payload;   //the key-value of payload, e.g. firstName->Alex
    private String messageTimestamp;    //trigger timestampe
    private String locale;              //locale
    private String trigger;             //trigger name
    private String mode;                //email or sms or ...
}
