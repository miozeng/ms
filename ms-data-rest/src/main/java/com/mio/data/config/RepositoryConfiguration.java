package com.mio.data.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfiguration {
	  @Bean
	  PersonEventHandler personEventHandler() {
	    return new PersonEventHandler();
	  }

}
