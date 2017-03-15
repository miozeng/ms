package com.mio.stream;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.aggregate.AggregateApplicationBuilder;

import com.mio.stream.processor.ProcessorApplication;
import com.mio.stream.sink.SinkApplication;
import com.mio.stream.source.SourceApplication;

@SpringBootApplication
public class MsStreamAggregationApplication {

	public static void main(String[] args) {
		new AggregateApplicationBuilder(MsStreamAggregationApplication.class, args)
		.from(SourceApplication.class).args("--fixedDelay=5000")
		.via(ProcessorApplication.class)
		.to(SinkApplication.class).args("--debug=true").run();
		
//		  new AggregateApplicationBuilder()
//          .from(SourceApplication.class).args("--fixedDelay=5000")
//          .via(ProcessorApplication.class)
//          .to(SinkApplication.class).args("--debug=true").run(args);
	}
}
