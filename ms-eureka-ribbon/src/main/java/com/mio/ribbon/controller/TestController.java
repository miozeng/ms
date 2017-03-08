package com.mio.ribbon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class TestController {
	
	@Autowired
    RestTemplate restTemplate;
    
	@RequestMapping(value = "/add", method = RequestMethod.GET)
    public String add() {
        return restTemplate.getForEntity("http://EUREKACLIENT/config", String.class).getBody();
    }
}
