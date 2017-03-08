package com.mio.feign.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "eurekaclient")//, fallback = HystrixClientFallback.class
public interface HystrixClient {

	@RequestMapping(method = RequestMethod.GET, value = "/config/")
	String iFailSometimes();
}
