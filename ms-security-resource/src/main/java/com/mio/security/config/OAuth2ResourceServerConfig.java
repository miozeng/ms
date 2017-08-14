package com.mio.security.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;

@Configuration
@EnableResourceServer
public class OAuth2ResourceServerConfig extends ResourceServerConfigurerAdapter{

	@Override
	public void configure(final HttpSecurity http) throws Exception {
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
		.and().authorizeRequests().anyRequest().authenticated();
		//.and().authorizeRequests().antMatchers(HttpMethod.GET,"/foos/**").access("#oauth2.hasScope('read')");
	}
	
	 @Override
     public void configure(final ResourceServerSecurityConfigurer config) {
        config.tokenServices(tokenService());
     }

	@Bean
	public AccessTokenConverter accessTokenConverter(){
		return new DefaultAccessTokenConverter();
	}
	 
	@Primary
	@Bean
	public RemoteTokenServices tokenService() {
	    RemoteTokenServices tokenService = new RemoteTokenServices();
	    tokenService.setCheckTokenEndpointUrl(
	      "http://localhost:8072/spring_oauthServer/oauth/check_token");
	    tokenService.setClientId("test1");
	    tokenService.setClientSecret("test1secret");
	    tokenService.setAccessTokenConverter(accessTokenConverter());
	    return tokenService;
	}
}
