package com.mio.hystrix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.EnableTurbine;

@SpringBootApplication
//@EnableTurbineStream
@EnableTurbine
@EnableEurekaClient
@EnableHystrixDashboard
public class MsHystrixTurbineApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsHystrixTurbineApplication.class, args);
	}
}
