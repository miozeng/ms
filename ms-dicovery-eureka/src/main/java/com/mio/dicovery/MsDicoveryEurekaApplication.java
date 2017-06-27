package com.mio.dicovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class MsDicoveryEurekaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsDicoveryEurekaApplication.class, args);
	}
}
