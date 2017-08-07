package com.mio.hystrix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@ComponentScan
@RestController
@EnableDiscoveryClient
@SpringBootApplication
@EnableHystrixDashboard
public class MsHystrixTurbineClientApplication {

    @RequestMapping("test")
    public String hello() {
        return "test";

    }
    
	public static void main(String[] args) {
		SpringApplication.run(MsHystrixTurbineClientApplication.class, args);
	}
}
