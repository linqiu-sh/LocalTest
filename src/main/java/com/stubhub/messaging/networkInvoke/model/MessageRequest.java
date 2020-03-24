package com.stubhub.messaging.networkInvoke.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.HashMap;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageRequest {
    @JsonProperty("userId")
    private @NotNull @Pattern(regexp = "\\d+") String userId;   //target user id
    @JsonProperty("userGuid")
    private @Pattern(regexp = ".*\\S.*") String userGuid;       //target user guid
    @JsonProperty("email")
    private String email;   //target email
    @JsonProperty("phone")
    private String phone;   //target phone number
    @JsonProperty("templateName")
    private @NotNull @Pattern(regexp = ".*\\S.*") String templateName;  //template name to send
    @JsonProperty("payload")
    private @Valid Map<String, Object> payload = new HashMap<>();   //the key-value of payload, e.g. firstName->Alex
    private String messageTimestamp;    //trigger timestampe
    private String locale;              //locale
    private String trigger;             //trigger name
    private String mode;                //email or sms or ...
}
