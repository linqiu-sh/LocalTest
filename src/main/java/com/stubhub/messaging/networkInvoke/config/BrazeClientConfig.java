package com.stubhub.messaging.networkInvoke.config;

import com.stubhub.messaging.networkInvoke.brazeModel.BrazeRequestStatus;
import com.stubhub.messaging.networkInvoke.properties.BrazeClientProperties;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableConfigurationProperties(BrazeClientProperties.class)
@Data
public class BrazeClientConfig {

    @Bean
    public ConcurrentHashMap<String, BrazeRequestStatus> brazeRequestStatusMap(){
        return new ConcurrentHashMap<>(256);
    }
}
