# Spring Cloud  Bus
Spring Cloud  Bus是Spring Cloud的消息机制   
使用轻量级的消息代理，连接分布式系统的节点，这样可以广播传播状态的更新，或者其他指令。
当Git Repository 改变时,通过POST请求Config Server的/bus/refresh,Config Server 会从repository获取最新的信息并通过amqp传递给client.
## spring clou bus实现配置动态更新
### 下载并运行rabbit
运行rabbit或者kafka做消息中间件

### 创建bus服务端
在ms-bus-server添加bus依赖
``` xml
 <dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>
```
配置配置文件
``` xml
spring:
  application:
    name: bus-server
  cloud:
    config:
     uri: http://localhost:8888
 
##以下配置说明config server    
management:
  security:
    enabled: false
##以下配置说明config server 需要登录   
management:
  security:
    enabled: true
  user: 
    name: username
    password: password
```
### 创建bus消费端
1.ms-bus-service添加依赖     

2.ms-bus-service 中实现RefreshScope

```java
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

```

### 配置中心
ms-spring-config不变

### 测试
 1.访问http://localhost:2225/show  Prop value from Config server   
 2.修改service.prop值为  Prop value from Config server2222222222222222    
 3.post访问http://localhost:2224/bus/refresh   
 4.再次访问http://localhost:2225/show Prop value from Config server2222222222222222   

其实为了方便管理，建议将refresh放在和config服务器一起

config client访问有密码server可以有两种方式一种是配置curl风格的uri
spring.cloud.config.url=http://username:password/localhost:8080      
第二种风格是分别配置uri,username,password属性。


### configserver的高可用性
1.git仓库的高可用      
1.使用github等这些本身就提供了高可用性的仓库，但是个人感觉这些仓库读取有点慢    
2.自建git仓库，但是要保证仓库的高可用性，推荐看看gitlab    
2.RabbitMQ高可用性     
 3.configserver本身的高可用性
多个configserver注册到eureka节点上。
