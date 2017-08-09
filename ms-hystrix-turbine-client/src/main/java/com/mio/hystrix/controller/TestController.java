package com.mio.hystrix.controller;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mio.hystrix.service.CommandThatFailsFast;
import com.mio.hystrix.service.HelloClient;

@RestController
@RequestMapping(value = "/test")
public class TestController {

	 private static final Logger LOG = Logger.getLogger(TestController.class.getName());


	@Autowired
	HelloClient helloClient;
	
	 private Random random = new Random();
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public String test() {
		 // 用随机数来模拟错误, 这里让正确率高一些
        return new CommandThatFailsFast(random.nextInt(100) < 95).execute();
	}

	
	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	public String hello() {
		  LOG.log(Level.INFO, "calling trace demo backend");
		return helloClient.hello();
	}
}
