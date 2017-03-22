package com.mio.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http
           .formLogin().loginPage("/login").permitAll()
           .and()
           .requestMatchers()
           .antMatchers("/", "/login", "/oauth/authorize", "/oauth/confirm_access")
           .and()
           .authorizeRequests()
           .anyRequest().authenticated();
    	
		http.headers().httpStrictTransportSecurity();
		http.headers().xssProtection();
		http.headers().contentTypeOptions();
		http
		.headers()
		.contentSecurityPolicy(
		"   default-src 'self'	*.facebook.com *.google.com *.twitter.com www.google-analytics.com www.gstatic.com ; "
		+ "	script-src	'self' 'unsafe-eval' 'unsafe-inline' https://www.google-analytics.com ajax.googleapis.com *.google.com www.gstatic.com https://www.google.com/recaptcha/ https://www.gstatic.com/recaptcha/;"
		+ "	style-src	'self' 'unsafe-eval' 'unsafe-inline' fonts.googleapis.com www.google-analytics.com www.gstatic.com ;"
		+ "frame-src https://www.google.com/recaptcha/ ;"
		+ "img-src	'self' 'unsafe-eval' 'unsafe-inline'  *.google.com www.gstatic.com https://www.google-analytics.com data:;");
		
//		http.addFilterAfter(new CustomFilter(), BasicAuthenticationFilter.class);

    }
    
    /*
     * 	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		AuthenticationProvider authenticationProvider = new CustomAuthenticationProvider();
		return authenticationProvider;
	}*/
     
}
