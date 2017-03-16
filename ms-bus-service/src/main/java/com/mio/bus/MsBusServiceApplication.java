package com.mio.bus;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
@SpringBootApplication
public class MsBusServiceApplication {

	@Value("${service.prop:notset}")
	private String aRefreshableProperty;

	@RequestMapping("/show")
	public String sendMessage() {
		return this.aRefreshableProperty;
	}

	public static void main(String[] args) {
		SpringApplication.run(MsBusServiceApplication.class, args);
	}
}
