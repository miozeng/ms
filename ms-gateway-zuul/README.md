## 服务网关
服务网关封装了应用的内部结构，客户只需要跟网关打交道，开发得到简化，使用微服务网关还有以下优点
1.易于监控，可在微服务网关收集监控数据，将其推送到外部系统进行分析。
2.易于认证，在网关验证即可，不需要每个微服务都验证
3.减少客户端与服务端的交互次数
## Zuul 
zuul是在云平台上提供动态路由，监控，弹性，安全等边缘服务的框架。Zuul 相当于是设备和 Netflix 流应用的 Web 网站后端所有请求的前门。Zuul 可以适当的对多个 Amazon Auto Scaling Groups 进行路由请求。zuul 是Netflix开源的服务网关，可以和feign，ribbon，hystrix配合使用，zuul的核心是一系列的过滤器。
Zuul可以通过加载动态过滤机制，从而实现以下各项功能：   
验证与安全保障: 识别面向各类资源的验证要求并拒绝那些与要求不符的请求。   
审查与监控: 在边缘位置追踪有意义数据及统计结果，从而为我们带来准确的生产状态结论。   
动态路由: 以动态方式根据需要将请求路由至不同后端集群处。  
压力测试: 逐渐增加指向集群的负载流量，从而计算性能水平。  
负载分配: 为每一种负载类型分配对应容量，并弃用超出限定值的请求。  
静态响应处理: 在边缘位置直接建立部分响应，从而避免其流入内部集群。   
多区域弹性: 跨越AWS区域进行请求路由，旨在实现ELB使用多样化并保证边缘位置与使用者尽可能接近。   
除此之外，Netflix公司还利用Zuul的功能通过金丝雀版本实现精确路由与压力测试。     
 
### 编写ZUUL微服务网关
1.添加依赖
``` xml
	<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-zuul</artifactId>
		</dependency>
```		
2.添加注解@EnableZuulProxy
3.编写配置文件
``` xml
server:
  port: 8080
 
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/

zuul:
  routes:
    customer:
      path: /customer/**
      url: http://localhost:8082
    client:
      path: /client/**
      url: http://localhost:2222
```       
      
### ZUUL的路由端点
 当@EnableZuulProxy  与 Actuator 配合使用，ZUUL会暴露一个路由管理端点/routes，可以方便的查看路由端点， spring-cloud-start-zuul 已经包含了Actuator所以会自带/routes方法
 
### 路由配置详解

 
### zuul上传文件

 spring:
  http:
    multipart:
      max-file-size: 10Mb
      max-request-size: 10Mb
      
### zuul过滤器
  过滤器是由Groovy写成。这些过滤器文件被放在Zuul Server上的特定目录下面。Zuul会定期轮询这些目录。修改过的过滤器会动态的加载到Zuul Server中以便于request使用。
下面有几种标准的过滤器类型：  
PRE：这种过滤器在请求到达Origin Server之前调用。比如身份验证，在集群中选择请求的Origin Server，记log等。   
ROUTING：在这种过滤器中把用户请求发送给Origin Server。发送给Origin Server的用户请求在这类过滤器中build。并使用Apache HttpClient或者Netfilx Ribbon发送给Origin Server。   
POST：这种过滤器在用户请求从Origin Server返回以后执行。比如在返回的response上面加response header，做各种统计等。并在该过滤器中把response返回给客户。    
ERROR：在其他阶段发生错误时执行该过滤器。   
客户定制：比如我们可以定制一种STATIC类型的过滤器，用来模拟生成返回给客户的response。   
 过滤器的生命周期如下所示：    

![Image text](https://github.com/miozeng/ms/blob/master/ms-gateway-zuul/image.png)
 
### zuul容错与回退
 
### zuul的高可用
只需将多个 zuul 节点注册到 eureka server上，就可实现 zuul 高可用，当 Zuul 客户端也注册到 eureka server 上时，只需部署多个 Zuul 节点即可实现其高可用
### 使用zuul聚合微服务
假如用户在APP上请求首页，首页内容会包含多个微服务的内容，如果使用手机直接请求各个微服务（即使使用 Zuul 进行转发），那么网络开销，流量耗费，耗费时长可能都无法令我们满意，那么对于这种场景可以使用 Zuul 聚合微服务请求，手机只发送一个请求给 zuul ，由 zuul 请求用户微服务并组织好数据给手机APP







