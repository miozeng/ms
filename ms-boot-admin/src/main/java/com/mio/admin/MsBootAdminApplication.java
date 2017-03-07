package com.mio.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import de.codecentric.boot.admin.config.EnableAdminServer;

@EnableDiscoveryClient
@EnableAdminServer
@SpringBootApplication
public class MsBootAdminApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsBootAdminApplication.class, args);
	}
}
