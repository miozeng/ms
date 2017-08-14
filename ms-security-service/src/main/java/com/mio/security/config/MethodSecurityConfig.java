package com.mio.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.mio.security.dto.Account;




@Configuration
public class MethodSecurityConfig extends GlobalAuthenticationConfigurerAdapter {

	 @Override
	  public void init(AuthenticationManagerBuilder auth) throws Exception {
	    auth.userDetailsService(userDetailsService());
	    

//	    auth.inMemoryAuthentication()
//	    .withUser("a").roles("ADMIN").password("password")
//	    .and()
//	    .withUser("Mary").roles("BASIC").password("password");
	    
	  }
	 
	 @Bean
	 public UserDetailsService userDetailsService() {
		  
	    return new UserDetailsService() {

	      @Override
	      public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	       // Account account = accountRepository.findByUsername(username);
	       System.out.println("XXXXXXXXXX ");
	       System.out.println("wwwwww " + username);
	       Account account = new Account(); 
	       account.setId("12345");
	       account.setPassword("agentsecretx");
	       account.setUsername("agentx");
	      if(account != null) {
	    
	        return new User(account.getUsername(), account.getPassword(), true, true, true, true,
	                AuthorityUtils.createAuthorityList("USER"));
	        } else {
	          throw new UsernameNotFoundException("could not find the user '"
	                  + username + "'");
	        }
	      }
	      
	    };
	    
	    
	    
	  }

}