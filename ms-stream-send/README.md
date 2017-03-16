# Spring Cloud Stream
. 提供了很多抽象和基础组件来简化消息驱动型微服务应用。包含以下内容：  
* Spring Cloud Stream的应用模型   
* 绑定抽象   
* 持久化发布／订阅支持  
* 消费者组支持  
* 分片支持（Partitioning Support）  
* 可插拔绑定api 应用模型  

### 1.1通过@EnableBinding触发绑定 
将@EnableBinding应用到spring应用的一个配置类中，可以将spring应用变成Spring Cloud Stream应用。    
@EnableBinding注解本身就包含@Configuration注解，并且会触发Spring Cloud Stream 基本配置。     
@EnableBinding注解可以接收一个或多个接口类作为对象，后者包含代表了可绑定构件（一般来说是消息通道）的方法。      
注：在Spring Cloud Stream1.0中，仅有的可绑定构件是Spring 消息 MessageChannel以及它的扩展SubscribableChannel 和 PollableChannel. 未来版本会使用相同的机制扩展对其他类型构件的支持。在本文档中，会继续饮用通道。 

### 1.2@Input 和 @Output 
一个Spring Cloud Stream应用可以有任意数目的input和output通道，后者通过@Input and @Output方法在进口中定义。  
```Java
public interface Barista {
    @Input
    SubscribableChannel orders();
    @Output
    MessageChannel hotDrinks();
    @Output
    MessageChannel coldDrinks();
}
```
将该接口作为@EnableBinding的参数，会相应的触发三个名为orders, hotDrinks, 和 coldDrinks的绑定好的通道。 
```Java
@EnableBinding(Barista.class)
public class CafeConfiguration {
   ...
}
```
定制通道名字   
使用@Input 和 @Output注解，可以自己指定通道的名字，如下所示：  
```Java
public interface Barista {
    ...
    @Input("inboundOrders")
    SubscribableChannel orders();
}
```
这个例子中，创建的绑定队列会被命名为inboundOrders。    
Source, Sink, and Processor 
在大多数用例中，包含一个输入通道或者一个输出通道或者二者都包含，为了更简单的定位，Spring Cloud Stream创造性的提供了三个预定义的接口。   
Source用于有单个输出（outbound）通道的应用。  
```Java
public interface Source {

  String OUTPUT = "output";

  @Output(Source.OUTPUT)
  MessageChannel output();

}
```
Sink用于有单个输入（inbound）通道的应用。
```Java
public interface Sink {

  String INPUT = "input";

  @Input(Sink.INPUT)
  SubscribableChannel input();

}
```
Processor用于单个应用同时包含输入和输出通道的情况。
```Java
public interface Processor extends Source, Sink {
}
```
Spring Cloud Stream对这三个接口没有提供任何特殊处理。他们只是用于创造性的提供。

### 1.3访问绑定通道
#### 1.注入已绑定接口 
对于每一个已绑定的接口， Spring Cloud Stream会生成一个bean实现该接口。唤起这些由@Input或者 @Output注解的方法生成的bean，其中一个bean会返回相应的通道。 
下面例子中，当hello方法被唤起的时候，bean会在output通道上发送一个消息。在注入的Source bean上提供唤醒output()来检索到目标通道。 
```Java
@Componentpublic class SendingBean {

    private Source source;

    @Autowired
    public SendingBean(Source source) {
        this.source = source;
    }

    public void sayHello(String name) {
         source.output().send(MessageBuilder.withPayload(body).build());
    }
}
```
#### 2.直接注入到通道 
绑定的通道也可以直接注入。
```Java
@Componentpublic class SendingBean {
    private MessageChannel output;

    @Autowired
    public SendingBean(MessageChannel output) {
        this.output = output;
    }

    public void sayHello(String name) {
         output.send(MessageBuilder.withPayload(body).build());
    }
}
```
如果通道名称是在声明的注解上指定的，则不能使用方法名称，而要使用通道名称。举例如下：
```Java
public interface CustomSource {
    ...
    @Output("customOutput")
    MessageChannel output();
}
```
通道会按照下面方式注入：
```Java
@Componentpublic class SendingBean {

    @Autowired
    private MessageChannel output;

    @Autowired @Qualifier("customOutput")
    public SendingBean(MessageChannel output) {
        this.output = output;
    }

    public void sayHello(String name) {
         customOutput.send(MessageBuilder.withPayload(body).build());
    }
}
```

### 1.4生产和消费消息
可以使用Spring Integration 的注解或者Spring Cloud Stream的 @StreamListener 注解来实现一个Spring Cloud Stream应用。   
@StreamListener注解模仿其他spring消息注解（例如@MessageMapping, @JmsListener, @RabbitListener等），但是它增加了内容类型管理和类型强制特性。 
#### 1.原生Spring Integration支持 
因为 Spring Cloud Stream是基于Spring Integration构建，Stream完全继承了Integration的基础设施以及构件本身。   
例如，可以将Source的output通道连接到一个MessageSource：
```Java
@EnableBinding(Source.class)public class TimerSource {
  @Value("${format}")
  private String format;

  @Bean
  @InboundChannelAdapter(value = Source.OUTPUT, poller = @Poller(fixedDelay = "${fixedDelay}", maxMessagesPerPoll = "1"))
  public MessageSource<String> timerMessageSource() {
    return () -> new GenericMessage<>(new SimpleDateFormat(format).format(new Date()));
  }
}
```
或者可以在transformer中使用处理器的通道。
```Java
@EnableBinding(Processor.class)public class TransformProcessor {
  @Transformer(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
  public Object transform(String message) {
    return message.toUpper();
  }
}
```
#### 2.使用 @StreamListener进行自动内容类型处理 
作为原生Spring Integration的补充，Spring Cloud Stream提供了自己的@StreamListener注解，该注解模仿spring的其它消息注解（如@MessageMapping, @JmsListener, @RabbitListener等）。   
@StreamListener注解提供了一种更简单的模型来处理输入消息，尤其是处理包含内容类型管理和类型强制的用例的情况。 
Spring Cloud Stream提供了一个扩展的MessageConverter机制，该机制提供绑定通道实现数据处理，   
本例子中，数据会分发给带@StreamListener注解的方法。下面例子展示了处理外部Vote事件的应用：
```Java
@EnableBinding(Sink.class)public class VoteHandler {

  @Autowired
  VotingService votingService;

  @StreamListener(Sink.INPUT)
  public void handle(Vote vote) {
    votingService.record(vote);
  }
}
```
在输入消息内容头为application/json的情况下，@StreamListener和Spring Integration的@ServiceActivator之间会体现出差异。使用@StreamListener的情况下，MessageConverter机制会使用contentType头将string负载解析为Vote对象（也就是如果传输的是对象，应该选用@StreamListener注解）。 
在其它Spring Messaging方法中，消息机制可以使用@Payload, @Headers 和 @Header这些注解。
注：对于那些有返回数据的方法，必须使用@SendTo注解来指定返回数据的输出绑定目标。
```Java
@EnableBinding(Processor.class)public class TransformProcessor {

  @Autowired
  VotingService votingService;

  @StreamListener(Processor.INPUT)
  @SendTo(Processor.OUTPUT)
  public VoteResult handle(Vote vote) {
    return votingService.record(vote);
  }
}
```
注：在RabbitMQ中，内容类型头可以由外部应用设定。

### 1.5聚合Aggregation
Spring Cloud Stream可以支持多种应用的聚合，可以实现多种应用输入输出通道直接连接，而无需额外代价。   
. 在1.0版本中，只有以下类型应用支持聚合：
* sources：带有名为output的单一输出通道的应用。典型情况下，该应用带有包含一个以下类型的绑定    
org.springframework.cloud.stream.messaging.Source
* sinks：带有名为input的单一输入通道的应用。典型情况下，该应用带有包含一个以下类型的绑定    
org.springframework.cloud.stream.messaging.Sink
* processors：带有名为input的单一输入通道和带有名为output的单一输出通道的应用。典型情况下，该应用带有包含一个以下类型的绑定   
type org.springframework.cloud.stream.messaging.Processor.

可以通过创建一系列相互连接的应用将它们聚合到一起，其中，序列中一个元素的输出通道与下一个元素的输入通道连接在一起。
序列可以由一个source或者一个processor开始，可以包含任意数目的processors，并由processors或者sink结束。 
取决于开始和结束元素的特性，序列可以有一个或者多个可绑定的通道，如下：
* 如果序列由source开始，sink结束，应用之间直接通信并且不会绑定通道
* 如果序列由processor开始，它的输入通道会变成聚合的input通道并进行相应的绑定
* 如果序列由processor结束，它的输出通道会变成聚合的output通道并进行相应的绑定
使用AggregateApplicationBuilder功能类来实现聚合，如下例子所示。   
考虑一个包含source,processor 和 sink的工程，它们可以示包含在工程中，或者包含在工程的依赖中。
```Java
@SpringBootApplication@EnableBinding(Sink.class)public class SinkApplication {

    private static Logger logger = LoggerFactory.getLogger(SinkModuleDefinition.class);

    @ServiceActivator(inputChannel=Sink.INPUT)
    public void loggerSink(Object payload) {
        logger.info("Received: " + payload);
    }
}
@SpringBootApplication@EnableBinding(Processor.class)public class ProcessorApplication {

    @Transformer
    public String loggerSink(String payload) {
        return payload.toUpperCase();
    }
}
@SpringBootApplication@EnableBinding(Source.class)public class SourceApplication {

    @Bean
    @InboundChannelAdapter(value = Source.OUTPUT)
    public String timerMessageSource() {
        return new SimpleDateFormat().format(new Date());
    }
}

```
每一个配置可用于运行一个独立的组件，在这个例子中，它们可以这样实现聚合：
```Java
@SpringBootApplicationpublic class SampleAggregateApplication {

    public static void main(String[] args) {
        new AggregateApplicationBuilder()
            .from(SourceApplication.class).args("--fixedDelay=5000")
            .via(ProcessorApplication.class)
            .to(SinkApplication.class).args("--debug=true").run(args);
    }
}
```
序列的开始组件被提供作为from()方法的参数，序列的结束组件被提供作为to()方法的参数，中间处理器组件则作为via()方法的参数。同一类型的多个processors可以链在一起（例如，可以使用不同配置的管道传输方式）。对于每一个组件，编译器可以为Spring Boot 提供运行时参数。