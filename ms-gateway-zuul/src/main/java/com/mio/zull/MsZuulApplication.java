package com.mio.zull;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;



@EnableZuulProxy
@SpringBootApplication
public class MsZuulApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsZuulApplication.class, args);
	}
}
