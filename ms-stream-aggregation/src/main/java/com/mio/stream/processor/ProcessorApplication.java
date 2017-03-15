package com.mio.stream.processor;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.integration.annotation.Transformer;

@SpringBootApplication
@EnableBinding(Processor.class)
public class ProcessorApplication {

	@Transformer
	public String loggerSink(String payload) {
		return payload.toUpperCase();
	}
	
//	@Transformer(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
//	public Message<?> transform(Message<?> inbound) {
//		return inbound;
//	}
}
