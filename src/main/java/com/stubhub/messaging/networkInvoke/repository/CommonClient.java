package com.stubhub.messaging.networkInvoke.repository;

import com.stubhub.domain.user.services.customers.v2.intf.GetCustomerResponse;
import com.stubhub.messaging.networkInvoke.brazeModel.*;
import com.stubhub.messaging.networkInvoke.exception.BrazeClientException;
import com.stubhub.messaging.networkInvoke.exception.CommonClientException;
import com.stubhub.messaging.networkInvoke.properties.BrazeClientProperties;
import com.stubhub.messaging.networkInvoke.properties.CommonClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CommonClient {

    private static final Logger logger = LoggerFactory.getLogger(CommonClient.class);

    @Autowired
    @Qualifier("commonRestTemplate")
    private RestTemplate commonRestTemplate;

    @Autowired
    private CommonClientProperties commonClientProperties;

    public String getUserGuidById(String userId){
        String userCustomerEndpoint = commonClientProperties.getUserCustomerEndpoint().replace("{userId}", userId);
        int retryTimes = commonClientProperties.getRetryTimes();
        boolean isError = false;
        GetCustomerResponse customerResponse = null;
        do {
            try {
                customerResponse = commonRestTemplate.getForObject(userCustomerEndpoint, GetCustomerResponse.class);
            }catch (CommonClientException e){
                isError = e.getStatusCode().isError();
                retryTimes--;
                if (isError){
                    logger.error("Exception in CommonClient.getCustomerById(), retryTimes={}, exception={}", retryTimes, e.toString());
                    if (retryTimes == 0){
                        throw e;
                    }
                }
            }
        }while (isError && retryTimes > 0);
        if (customerResponse == null){
            return null;
        }

        return customerResponse.getUserCookieGuid();

    }

}
