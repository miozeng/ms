package com.mio.hystrix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.turbine.stream.EnableTurbineStream;

@SpringBootApplication
@EnableTurbineStream
//@EnableTurbine
@EnableEurekaClient
public class MsHystrixTurbineApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsHystrixTurbineApplication.class, args);
	}
}
