package com.mio.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@ComponentScan
@RestController
@EnableDiscoveryClient
@SpringBootApplication
public class MsEurekaClientApplication {

	@GetMapping("/")
	public String home() {
		return "Hello world";
	}


	public static void main(String[] args) {
		SpringApplication.run(MsEurekaClientApplication.class, args);
	}
}
