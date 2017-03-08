package com.mio.feign.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mio.feign.service.HystrixClient;

@RestController
@RequestMapping(value = "/test")
public class TestController {

	@Autowired
	HystrixClient hystrixClient;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String createCompanys() {
		return hystrixClient.iFailSometimes();
	}
}
