
## Feign简介
在spring Cloud Netflix栈中，各个微服务都是以HTTP接口的形式暴露自身服务的，因此在调用远程服务时就必须使用HTTP客户端。我们可以使用JDK原生的URLConnection、Apache的Http Client、Netty的异步HTTP Client, Spring的RestTemplate。但是，用起来最方便、最优雅的还是要属Feign了。Feign是一种声明式、模板化的HTTP客户端。

为了让Feign知道在调用方法时应该向哪个地址发请求以及请求需要带哪些参数，我们需要定义一个接口：    
1.添加依赖：
``` xml
	<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-feign</artifactId>
	</dependency>
``` 
2.创建feign接口并添加@FeignClient注解
``` java
@FeignClient(name = "eureka-ribbon")
public interface HelloClient {
	@RequestMapping(method = RequestMethod.GET, value = "/hello/")
	String hello();
}
```
    
A: @FeignClient用于通知Feign组件对该接口进行代理(不需要编写接口实现)，使用者可直接通过@Autowired注入。  

B: @RequestMapping表示在调用该方法时需要向/group/{groupId}发送GET请求。   

C: @PathVariable与SpringMVC中对应注解含义相同。  
3.controller中实现
``` java
@Autowired
HelloClient helloClient;
    
@RequestMapping(value = "/hello", method = RequestMethod.GET)
	public String hello() {
		  LOG.log(Level.INFO, "calling trace demo backend");
		return helloClient.hello();
	}
```
4.修改启动类
启动类添加注解@EnableFeignClients

Spring Cloud应用在启动时，Feign会扫描标有@FeignClient注解的接口，生成代理，并注册到Spring容器中。生成代理时Feign会为每个接口方法创建一个RequetTemplate对象，该对象封装了HTTP请求需要的全部信息，请求参数名、请求方法等信息都是在这个过程中确定的，Feign的模板化就体现在这里。 

### 自定义Feign配置
springcloud Feign的默认配置类时FeignClientsConfiguration
Spring Cloud Netflix provides the following beans by default for feign (BeanType beanName: ClassName):

Decoder feignDecoder: ResponseEntityDecoder (which wraps a SpringDecoder)

Encoder feignEncoder: SpringEncoder

Logger feignLogger: Slf4jLogger

Contract feignContract: SpringMvcContract

Feign.Builder feignBuilder: HystrixFeign.Builder

Client feignClient: if Ribbon is enabled it is a LoadBalancerFeignClient, otherwise the default feign client is used.

The OkHttpClient and ApacheHttpClient feign clients can be used by setting feign.okhttp.enabled or feign.httpclient.enabled to true, respectively, and having them on the classpath.

Spring Cloud Netflix does not provide the following beans by default for feign, but still looks up beans of these types from the application context to create the feign client:

Logger.Level

Retryer

ErrorDecoder

Request.Options

Collection<RequestInterceptor>

SetterFactory

example:
``` java
@Configuration
public class FooConfiguration {
    @Bean
    public Contract feignContract() {
        return new feign.Contract.Default();
    }

    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        return new BasicAuthRequestInterceptor("user", "password");
    }
}

修改feign接口注解FeignClient为
@FeignClient(name = "eureka-ribbon",configuration=FooConfiguration.Class)
``` 

### 手动创建Feign（实践 ）
在某些场景中，通过上述方法，还是不能得到一个想要的Feign客户端。 
那这个时候，就需要自己通过[Feign Builder API](https://github.com/OpenFeign/feign/#basics)来手动创建客户端。   
下面这个例子就展现了为同一个接口创建两个不同配置的客户端。
```java
@Import(FeignClientsConfiguration.class)
class FooController {

	private FooClient fooClient;

	private FooClient adminClient;

    @Autowired
	public FooController(
			Decoder decoder, Encoder encoder, Client client) {
		this.fooClient = Feign.builder().client(client)
				.encoder(encoder)
				.decoder(decoder)
				.requestInterceptor(new BasicAuthRequestInterceptor("user", "user"))
				.target(FooClient.class, "http://PROD-SVC");
		this.adminClient = Feign.builder().client(client)
				.encoder(encoder)
				.decoder(decoder)
				.requestInterceptor(new BasicAuthRequestInterceptor("admin", "admin"))
				.target(FooClient.class, "http://PROD-SVC");
    }
}
```
 
### Feign对继承的支持
Feign 支持继承，使用继承可以将一些公共操作分组到一些父接口中，从而简化Feign的开发
基础接口
```java
@RequestMapping(method =RequestMethod.GET, value ="/users/{id}")
public interface IUserService{
    User getUser(@PathVariable("id")String id);
}
```
服务提供者：
```java
@RestController
public class UserServiceFeignController implements IUserService{
//...
}
```
服务消费者
```java
@FeignClient("users")
public interface IFeignUserService extends IUserService{
}
```
### Feign对压缩的支持
``` java
#请求和响应GZIP压缩支持
feign.compression.request.enabled=true
feign.compression.response.enabled=true
#支持压缩的mime types
feign.compression.request.enabled=true
feign.compression.request.mime-types=text/xml,application/xml,application/json
feign.compression.request.min-request-size=2048
```
### Feign的日志

logging.level.project.user.UserClient: DEBUG  
logging.level其他选项包括：
NONE：不记录日志     
BASIC：仅记录请求方法，URL,响应状态代码以及执行时间    
HEADERS：BASIC+请求和响应header  
FULL：header body和元数据   

可在配置类中添加以下配置
``` java
  @Bean
    public Logger.Level feignLoggerLevel() {
        return feign.Logger.Level.FULL;
    }
```    
其次，在application.properties中要设定一行这样的代码：
logging.level.<你的feign client全路径类名>: DEBUG
这样对应的feign client就可以输出日志了。这里必须是DEBUG才能生效。   

### Feign的构造多参数请求
#### GET 方法
方法一   
这是最为直观的方式，URL有几个参数，Feign接口中的方法就有几个参数。使用@RequestParam注解指定请求的参数是什么。
``` java
@FeignClient(name = "microservice-provider-user")
public interface UserFeignClient {
  @RequestMapping(value = "/get", method = RequestMethod.GET)
  public User get1(@RequestParam("id") Long id, @RequestParam("username") String username);
}
``` 
方法二   
多参数的URL也可以使用Map去构建。当目标URL参数非常多的时候，可使用这种方式简化Feign接口的编写
``` java
@FeignClient(name = "microservice-provider-user")
public interface UserFeignClient {
  @RequestMapping(value = "/get", method = RequestMethod.GET)
  public User get2(@RequestParam Map<String, Object> map);
}
``` 

GET方法貌似并不支持pojo的方式传递多个参数
#### POST方法
假设我们的用户微服务的Controller是这样编写的：
``` java
@RestController
public class UserController {
  @PostMapping("/post")
  public User post(@RequestBody User user) {
    ...
  }
}
``` 
我们的Feign接口要如何编写呢？答案非常简单，示例：
``` java
@FeignClient(name = "microservice-provider-user")
public interface UserFeignClient {
  @RequestMapping(value = "/post", method = RequestMethod.POST)
  public User post(@RequestBody User user);
}
``` 


## Hystrix
Hystrix 是Netflix开源，针对分布式系统的延迟和容错库。

1:Hystrix使用命令模式HystrixCommand(Command)包装依赖调用逻辑，每个命令在单独线程中/信号授权下执行。   
2:可配置依赖调用超时时间,超时时间一般设为比99.5%平均时间略高即可.当调用超时时，直接返回或执行fallback逻辑。   
3:为每个依赖提供一个小的线程池（或信号），如果线程池已满调用将被立即拒绝，默认不采用排队.加速失败判定时间。    
4:依赖调用结果分:成功，失败（抛出异常），超时，线程拒绝，短路。 请求失败(异常，拒绝，超时，短路)时执行fallback(降级)逻辑。   
5:提供熔断器组件,可以自动运行或手动调用,停止当前依赖一段时间(10秒)，熔断器默认错误率阈值为50%,超过将自动运行。   
6:提供近实时依赖的统计和监控     



