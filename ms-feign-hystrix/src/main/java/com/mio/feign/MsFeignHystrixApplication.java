package com.mio.feign;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

//@EnableHystrixDashboard
//@EnableCircuitBreaker
@EnableFeignClients
@EnableEurekaClient
@SpringBootApplication
public class MsFeignHystrixApplication {
	
	    
	public static void main(String[] args) {
		SpringApplication.run(MsFeignHystrixApplication.class, args);
	}
}
