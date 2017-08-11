package com.mio.zipkin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import zipkin.server.EnableZipkinServer;

@SpringBootApplication
@EnableZipkinServer
public class MsZipkinServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsZipkinServerApplication.class, args);
	}
}
