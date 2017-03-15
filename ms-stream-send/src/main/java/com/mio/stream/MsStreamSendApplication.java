package com.mio.stream;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@EnableBinding(Source.class)
@RestController
@SpringBootApplication
public class MsStreamSendApplication {

	@Autowired
	@Output(Source.OUTPUT)
	private MessageChannel channel;

	@RequestMapping(method = RequestMethod.POST, path = "/send")
	public void write(@RequestBody Map<String, Object> msg) {
		channel.send(MessageBuilder.withPayload(msg).build());
	}

	public static void main(String[] args) {
		SpringApplication.run(MsStreamSendApplication.class, args);
	}
}
