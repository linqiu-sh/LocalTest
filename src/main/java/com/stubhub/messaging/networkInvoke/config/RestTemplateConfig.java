package com.stubhub.messaging.networkInvoke.config;

import com.stubhub.messaging.networkInvoke.repository.BrazeRestTemplateErrorHandler;
import com.stubhub.messaging.networkInvoke.repository.CommonRestTemplateErrorHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate commonRestTemplate(@Qualifier("commonClientHttpRequestFactory")ClientHttpRequestFactory factory){
        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.setErrorHandler(new CommonRestTemplateErrorHandler());
        return restTemplate;
    }

    @Bean
    public RestTemplate brazeRestTemplate(@Qualifier("brazeClientHttpRequestFactory") ClientHttpRequestFactory factory){
        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.setErrorHandler(new BrazeRestTemplateErrorHandler());
        return restTemplate;
    }

    @Bean
    public ClientHttpRequestFactory brazeClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(20000);
        factory.setReadTimeout(5000);
        return factory;
    }

    @Bean
    public ClientHttpRequestFactory commonClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(20000);
        factory.setReadTimeout(5000);
        return factory;
    }

}
