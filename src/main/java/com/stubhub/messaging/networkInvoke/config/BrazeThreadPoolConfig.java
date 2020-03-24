package com.stubhub.messaging.networkInvoke.config;

import com.stubhub.messaging.networkInvoke.properties.BrazeRequestSchedulerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
@EnableConfigurationProperties(BrazeRequestSchedulerProperties.class)
public class BrazeThreadPoolConfig {

    @Autowired
    BrazeRequestSchedulerProperties brazeRequestSchedulerProperties;

    @Bean
    public BlockingDeque<Runnable> sendingRequests(){
        return new LinkedBlockingDeque<>();
    }

    @Bean
    public BlockingQueue<Runnable> sendingResponses(){
        return new LinkedBlockingQueue<>();
    }

    @Bean
    public ExecutorService sendingRequestsTaskPool(@Qualifier("sendingRequests") BlockingDeque<Runnable> sendingRequests){
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors()
                , Integer.MAX_VALUE
                , brazeRequestSchedulerProperties.getKeepAliveTime()
                , TimeUnit.MILLISECONDS
                , sendingRequests);

        return threadPoolExecutor;
    }

    @Bean
    public ExecutorService sendingResponsesHandlingTaskPool(@Qualifier("sendingResponses") BlockingQueue<Runnable> sendingResponses){
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1
                , Runtime.getRuntime().availableProcessors()
                , brazeRequestSchedulerProperties.getKeepAliveTime()
                , TimeUnit.MILLISECONDS
                , sendingResponses);

        return threadPoolExecutor;
    }

    @Bean
    public ExecutorService brazeMetadataTaskPool(){
        return Executors.newFixedThreadPool(1);
    }

}
