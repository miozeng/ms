package com.mio.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
//@EnableOAuth2Sso
@RestController
public class MsSecurityUiApplication {

//	@Bean
//	public RestTemplate getRestTemplate() {
//		return new RestTemplate();
//	}
//	
//	//OAuth2
//	@Autowired
//	RestTemplate restTemplate;
//	
//	@Value("${messages.url:http://localhost:7777}/api")
//	String messagesUrl;
//
//	@RequestMapping("/")
//	String home(Model model) {
//		List<Message> messages = Arrays.asList(restTemplate.getForObject(messagesUrl + "/messages", Message[].class));
//		model.addAttribute("messages", messages);
//		return "index";
//	}
//
//	@RequestMapping(path = "messages", method = RequestMethod.POST)
//	String postMessages(@RequestParam String text) {
//		Message message = new Message();
//		message.text = text;
//		restTemplate.exchange(RequestEntity
//				.post(UriComponentsBuilder.fromHttpUrl(messagesUrl).pathSegment("messages").build().toUri())
//				.body(message), Message.class);
//		return "redirect:/";
//	}
//
//	public static class Message {
//		public String text;
//		public String username;
//		public LocalDateTime createdAt;
//	}

	public static void main(String[] args) {
		SpringApplication.run(MsSecurityUiApplication.class, args);
	}

}
