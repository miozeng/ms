package com.mio.zull;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;


@EnableZuulProxy
@SpringCloudApplication
public class MsZuulApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsZuulApplication.class, args);
	}
}
