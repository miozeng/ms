package com.mio.discovery.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

	@Autowired
	private DiscoveryClient client;
	

	@GetMapping(value = "/config")
	public String serviceUrl() {
		ServiceInstance localInstance = client.getLocalServiceInstance();
		return "Hello!  " + localInstance.getServiceId() + ":" + localInstance.getHost() + ":"
				+ localInstance.getPort();
	}

	@GetMapping(value = "aa/{name}")
	public String hello(@PathVariable String name) {
		return "Hello! " + name;
	}

}
