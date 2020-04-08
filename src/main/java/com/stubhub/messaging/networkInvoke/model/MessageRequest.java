package com.stubhub.messaging.networkInvoke.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.*;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageRequest {
    @JsonProperty("userId")
    private @NotNull @Pattern(regexp = "\\d+") String userId;   //target user id
    @JsonProperty("messageName")
    private @NotNull @Pattern(regexp = ".*\\S.*") String messageName;  //messageName name to send
    @JsonProperty("data")
    private List<com.stubhub.messaging.networkInvoke.model.Data> datalist = new ArrayList<>();
    private String messageTimestamp;    //trigger timestampe
    private Locale locale;              //locale
    private String mode;                //email or sms or ...


//    @XmlElement(
//            name = "mode",
//            required = false
//    )
//    private String mode;
//    @XmlElement(
//            name = "userId",
//            required = false
//    )
//    private String userId;
//    @XmlElement(
//            name = "messageName",
//            required = true
//    )
//    private String messageName;
//    @XmlElement(
//            name = "data",
//            required = false
//    )
//    @JsonProperty("data")
//    private List<Data> datalist = new ArrayList();
//    @XmlElement(
//            name = "messageTimestamp",
//            required = true
//    )
//    private String messageTimestamp;
//    @JsonIgnore
//    private String acceptLanguage;
//    @JsonIgnore
//    private String contactPhoneNumber;
//    @JsonIgnore
//    private Locale locale;
//    @JsonIgnore
//    private boolean ignoreSmsSubscription;
//    @Transient
//    @JsonIgnore
//    private LogKey subStatus;
//    @Transient
//    @JsonIgnore
//    private boolean vendorTemplate;
//    @Transient
//    @JsonIgnore
//    private Calendar messageTimestampCal;
//    @Transient
//    @JsonIgnore
//    private MessageModeType messageModeType;
//    @Transient
//    @JsonIgnore
//    private Long shStoreId;
//    @Transient
//    @JsonIgnore
//    private Map<String, Object> dataMap;
//    @Transient
//    @JsonIgnore
//    private MessageSystemType messageSystem;
//    @Transient
//    @JsonIgnore
//    private Boolean asyncEnabled;
//    @Transient
//    @JsonIgnore
//    private String operatorId;
//    @Transient
//    @JsonIgnore
//    private String proxiedId;
//    @Transient
//    @JsonIgnore
//    private String role;
}
