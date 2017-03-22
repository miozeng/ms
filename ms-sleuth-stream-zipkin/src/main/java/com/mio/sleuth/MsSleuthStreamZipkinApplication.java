package com.mio.sleuth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.sleuth.zipkin.stream.EnableZipkinStreamServer;

@EnableZipkinStreamServer
@SpringBootApplication
public class MsSleuthStreamZipkinApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsSleuthStreamZipkinApplication.class, args);
	}
}
