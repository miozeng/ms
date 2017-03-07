package com.mio.feign.service;

import org.springframework.stereotype.Component;

@Component
public class HystrixClientFallback implements HystrixClient {

	@Override
	public String iFailSometimes() {
		return "fallback";
	}

}
