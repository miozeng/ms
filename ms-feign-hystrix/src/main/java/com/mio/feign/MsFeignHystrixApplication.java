package com.mio.feign;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

import com.mio.feign.service.HystrixClient;

//@EnableHystrixDashboard
//@EnableCircuitBreaker
@EnableAutoConfiguration
@EnableEurekaClient
@EnableFeignClients
public class MsFeignHystrixApplication {

	
	 @Autowired
	 HystrixClient accountClient;

	    @HystrixCommand(ignoreExceptions = RemoteCallException.class)
	    public String isBalanceEnough(Long userId, Long amount) {
	        return accountClient.iFailSometimes();
	    }
	    
	public static void main(String[] args) {
		SpringApplication.run(MsFeignHystrixApplication.class, args);
	}
}
