package com.mio.feign.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mio.feign.service.HelloClient;
import com.mio.feign.service.HystrixClient;
import com.mio.feign.service.HystrixClient2;

@RestController
@RequestMapping(value = "/test")
public class TestController {

	 private static final Logger LOG = Logger.getLogger(TestController.class.getName());
	@Autowired
	HystrixClient hystrixClient;

	@Autowired
	HelloClient helloClient;
	
	
	@Autowired
	HystrixClient2 hystrixClient2;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String createCompanys() {
		  LOG.log(Level.INFO, "calling trace demo backend");
		return hystrixClient.iFailSometimes();
	}
	
	@RequestMapping(value = "/hh", method = RequestMethod.GET)
	public String createCompanys2() {
		  LOG.log(Level.INFO, "calling trace demo backend");
		return hystrixClient2.iFailSometimes();
	}
	
	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	public String hello() {
		  LOG.log(Level.INFO, "calling trace demo backend");
		return helloClient.hello();
	}
}
