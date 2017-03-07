package com.mio.discovery.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class TestController {

	@Autowired
	private DiscoveryClient client;

	@LoadBalanced
	@Autowired  
	protected RestTemplate restTemplate; 
	
	@GetMapping(value = "/config2")
	public String serviceUrl2() {
		String c =  restTemplate.getForObject("http://localhost:2222/config2", String.class);
		return c;
	}
	

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
