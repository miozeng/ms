# Spring Cloud  Bus
Spring Cloud  Bus是Spring Cloud的消息机制   
当Git Repository 改变时,通过POST请求Config Server的/bus/refresh,Config Server 会从repository获取最新的信息并通过amqp传递给client.

### 下载并运行rabbit
运行rabbit或者kafka做消息中间件

### 创建bus服务端
在ms-bus-server添加bus依赖
 <dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>

### 创建bus消费端
1.添加依赖     

2.ms-bus-service 中实现RefreshScope
'''java

@RestController
@RefreshScope
@SpringBootApplication
public class MsBusServiceApplication {

	@Value("${service.prop:notset}")
	private String aRefreshableProperty;

	@RequestMapping("/show")
	public String sendMessage() {
		return this.aRefreshableProperty;
	}

	public static void main(String[] args) {
		SpringApplication.run(MsBusServiceApplication.class, args);
	}
}

'''

### 配置中心不变，


### 测试
 1.访问http://localhost:2225/show  Prop value from Config server   
 2.修改service.prop值为  Prop value from Config server2222222222222222    
 3.post访问http://localhost:2224/bus/refresh   
 4.再次访问http://localhost:2225/show Prop value from Config server2222222222222222   