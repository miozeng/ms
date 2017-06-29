package com.mio.feign.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import feign.hystrix.FallbackFactory;

@Component
public class HystrixClientFallbackFactory implements FallbackFactory<HystrixClient2>{

    private static final Logger LOGGER = LoggerFactory.getLogger(HystrixClientFallbackFactory.class);
    @Override
    public HystrixClient2 create(Throwable cause) {
        LOGGER.info("fallback; reason was: ()" + cause.getMessage());
        return new HystrixClientWithFallBackFactory() {
            @Override
            public String iFailSometimes() {
                return "fallback; reason was: " + cause.getMessage();
            }
        };
    }
}
