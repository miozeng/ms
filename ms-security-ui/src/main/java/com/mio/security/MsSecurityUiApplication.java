package com.mio.security;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.http.RequestEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootApplication
@EnableOAuth2Sso
@RestController
public class MsSecurityUiApplication {

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
	
	//OAuth2
	@Autowired
	RestTemplate restTemplate;
	@Value("${messages.url:http://localhost:7777}/api")
	String messagesUrl;

	@RequestMapping("/")
	String home(Model model) {
		List<Message> messages = Arrays.asList(restTemplate.getForObject(messagesUrl + "/messages", Message[].class));
		model.addAttribute("messages", messages);
		return "index";
	}

	@RequestMapping(path = "messages", method = RequestMethod.POST)
	String postMessages(@RequestParam String text) {
		Message message = new Message();
		message.text = text;
		restTemplate.exchange(RequestEntity
				.post(UriComponentsBuilder.fromHttpUrl(messagesUrl).pathSegment("messages").build().toUri())
				.body(message), Message.class);
		return "redirect:/";
	}

	public static class Message {
		public String text;
		public String username;
		public LocalDateTime createdAt;
	}

	public static void main(String[] args) {
		SpringApplication.run(MsSecurityUiApplication.class, args);
	}

}
