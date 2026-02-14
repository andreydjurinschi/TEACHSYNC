package org.cedacri.spring.scheduleservice.utils;

import feign.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class FeignConfig {
    @Bean
    public Request.Options feignRequestOptions(){
        return new Request.Options(2, TimeUnit.SECONDS,3, TimeUnit.SECONDS, true);
    }
}
