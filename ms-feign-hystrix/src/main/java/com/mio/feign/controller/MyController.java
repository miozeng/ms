package com.mio.feign.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.FeignClientsConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mio.feign.service.FooClient;

import feign.Client;
import feign.Feign;
import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.Decoder;
import feign.codec.Encoder;

@RestController
@Import(FeignClientsConfiguration.class)
public class MyController {
	
	private FooClient fooClient;

	private FooClient adminClient;

	@Autowired
	public MyController(Decoder decoder, Encoder encoder, Client client) {
		this.fooClient = Feign.builder().client(client)
				.encoder(encoder)
				.decoder(decoder)
				.requestInterceptor(new BasicAuthRequestInterceptor("user", "user"))
				.target(FooClient.class, "http://EUREKA-RIBBON");  //EUREKA-RIBBON is the name of the service registered in the discovery server
		this.adminClient = Feign.builder().client(client)
				.encoder(encoder)
				.decoder(decoder)
				.requestInterceptor(new BasicAuthRequestInterceptor("admin", "admin"))
				.target(FooClient.class, "http://EUREKA-RIBBON");
	}

	@RequestMapping("/user-foos")
	public String getUserFoos() {
		return fooClient.hello();
	}

	@RequestMapping("/admin-foos")
	public String getAdminFoos() {
		return adminClient.hello();
	}
}
