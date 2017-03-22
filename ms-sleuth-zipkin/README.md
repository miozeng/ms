### 1.日志收集
添加依赖：   
<dependency> 
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-starter-sleuth</artifactId>
 </dependency>
 
 日志显示：   
2017-03-16 15:48:48.420  INFO [zipkin-server,e5c13b14dbc06b95,e5c13b14dbc06b95,false] 7300 --- [nio-9411-exec-1] c.mio.sleuth.MsSleuthZipkinApplication   : you called home   

[zipkin-server,e5c13b14dbc06b95,e5c13b14dbc06b95,false] ==》[appname,traceId,spanId,exportable]

### 2.通过HTTP使用基于Zipkin的Sleuth
如果你需要Sleuth和Zipkin，只需要添加spring-cloud-starter-zipkin依赖        
<dependency>
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-starter-zipkin</artifactId>
</dependency> 

### 3.通过Spring Cloud Stream使用Sleuth+Zipkin   
通过Spring Cloud Stream使用Sleuth+Zipkin
   <dependency> 
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-sleuth-stream</artifactId>
   </dependency>
   <dependency> 
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-starter-sleuth</artifactId>
   </dependency>
   <!-- EXAMPLE FOR RABBIT BINDING -->
   <dependency> 
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-stream-binder-rabbit</artifactId>
   </dependency>

### 4.Spring Cloud Sleuth Stream Zipkin Collector
  启动一个Spring Cloud Sleuth Stream Zipkin收集器
   <dependency> 
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-sleuth-zipkin-stream</artifactId>
   </dependency>
   <dependency> 
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-starter-sleuth</artifactId>
   </dependency>
   <!-- EXAMPLE FOR RABBIT BINDING -->
   <dependency> 
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-stream-binder-rabbit</artifactId>
   </dependency>
   
   
###  度量(metrics)
1.如果依赖了spring-cloud-sleuth-zipkin，应用将生成并收集Zipkin-compatible traces，一般会通过HTTP将这些traces发送给一个本地Zipkin服务器(port 9411)，使用spring.zipkin.baseUrl来配置服务的地址   
2.如果依赖了spring-cloud-sleuth-stream，应用将通过Spring Cloud Stream生成并收集traces，应用自动成为tracer消息的生产者，这些消息会通过你的中间件分发(e.g. RabbitMQ,Apache Kafka,Redis)     
3.如果使用Zipkin或Stream，使用spring.sleuth.sampler.percentage配置输出spans的百分比(默认10%)，不然你可能会认为Sleuth没有工作，因为他省略了一些spans     
 
注：SLF4J MDC一直处于工作状态，logback用户可以在logs中立刻看到trace和span id，其他logging系统不得不配置他们自己的模式以得到相同的结果，默认logging.pattern.level设置为%clr(%5p) %clr([${spring.application.name:},%X{X-B3-TraceId:-},%X{X-B3-SpanId:-},%X{X-Span-Export:-}]){yellow}(对于logback用户，这是一种Spring Boot特征)，这意味着如果你没有使用SLF4J这个模式将不会自动适用   

###  抽样(Samling)   
在分布式追踪时，数据量可能会非常大，因此抽样就变得非常重要(通常不需要导出所有的spans以得到事件发生原貌)，     
Spring Cloud Sleuth有一个Sampler战略，即用户可以控制抽样算法，Samplers不会停止正在生成的span id(相关的)，但他们会阻止tags和events附加和输出，默认战略是当一个span处于活跃状态会继续trace，但新的span会一直处于不输出状态，如果所有应用都使用这个sampler，你会在logs中看到traces，但不会出现在任何远程仓库。测试状态资源都是充足的，并且你只使用logs的话他就是你需要的全部(e.g.一个ELK集合)，如果输出span数据到Zipkin或Spring Cloud Stream，有AlwaysSampler输出所有数据和PercentageBasedSampler采样spans确定的一部分。
如果使用spring-cloud-sleuth-zipkin或spring-cloud-sleuth-stream，PercentageBasedSampler是默认的，你可以使用spring.sleuth.sampler.percentage配置输出
通过创建一个bean定义就可以新建一个sampler
@Bean
public Sampler defaultSampler() {
    return new AlwaysSampler();
}
 
###  Instrumentation
Spring Cloud Sleuth自动装配所有Spring应用，因此你不用做任何事来让他工作，装配是使用一系列技术添加的，例如对于一个servlet web应用我们使用一个Filter，对于SpringIntegration我们使用ChannelInterceptors。
用户可以使用span tags定制关键字，为了限制span数据量，一般一个HTTP请求只会被少数元数据标记，例如status code、host以及URL，用户可以通过配置spring.sleuth.keys.http.headers(一系列头名称)添加request headers。
tags仅在Sampler允许其被收集和输出时工作(默认情况其不工作，因此不会有在不配置的情况下收集过多数据的意外危险出现
   
   
   
   