package com.mio.feign.service;

import org.springframework.stereotype.Component;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Component
public class HystrixClientFallback implements HystrixClient {

	@Override
	public String iFailSometimes() {
		return "fallback";
	}

	@HystrixCommand(fallbackMethod = "defaultStores")
	public String test() {
		return "熔断测试";
	}

}
