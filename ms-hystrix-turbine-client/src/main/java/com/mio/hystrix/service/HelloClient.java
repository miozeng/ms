package com.mio.hystrix.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "EUREKA-RIBBON")
public interface HelloClient {
	@RequestMapping(method = RequestMethod.GET, value = "/hello")
	String hello();
}
