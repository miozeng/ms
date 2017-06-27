# Eureka server 
Eureka server 提供服务发现的能力，各个微服务启动时会向Eureka server发送自己的信息（端口 IP 服务名等），Eureka server会存储这些信息

微服务启动后，会周期性的向Eureka server发送心跳来续租，一般是30s

如果Eureka server在一定的周期内收不到某个服务的心跳，就会注销该实例，一般默认是90s


### Eureka 常用配置：

是否将自己注册到Eureka server，默认为true
由于当前这个应用就是Eureka Server，故而设为false  
eureka.client.registerWithEureka=false 

是否从Eureka server获取注册信息，默认为true
因为这是一个单点的Eureka Server，不需要同步其他的Eureka Server节点的数据，故而设为false。  
eureka.client.fetchRegistry=false

配置eureka服务地址，多个地址可使用 , 分隔
eureka.client.serviceUrl.defaultZone

设置拉取服务注册信息时间，默认60s
eureka.client.registry-fetch-interval-seconds=30

指定续约更新频率，默认是30s
eureka.instance.lease-renewal-interval-in-seconds=15

设置清理无效节点的时间间隔，默认60000，即是60s
eureka.server.eviction-interval-timer-in-ms=30000


### Eureka server高可用性：
Eureka Server除了单点运行之外，还可以通过运行多个实例，并进行互相注册的方式来实现高可用的部署，所以我们只需要将Eureke Server配置其他可用的serviceUrl就能实现高可用部署。
  
demo请看E:\myworkspace\ms\ms-dicovery-eureka
   
Eureka Server的同步遵循着一个非常简单的原则：只要有一条边将节点连接，就可以进行信息传播与同步。
    
两两注册的方式可以实现集群中节点完全对等的效果，实现最高可用性集群，任何一台注册中心故障都不会影响服务的注册与发现

Eureka Server具备单方面有指向的服务传播与同步机制，在一些对服务发现有限制的情况下，可以利用这样的机制进行服务注册与发现的的单向控制

### Eureka server 添加用户认证
一、添加spring-security支持  
``` xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-security</artifactId>
</dependency>
``` 
二、在配置文件中加入安全认证
``` yml
# 安全认证的配置  
security:  
  basic:  
    enabled: true  
  user:  
    name: mio  # 用户名  
    password: password   # 用户密码  
   ```  
三、微服务注册
``` yml
eureka:  
  client:  
    service-url:  
      defaultZone: http://mio:password@localhost:8761/eureka
 ``` 
### Eureka server 元数据

Eureka的元数据有两种：标准元数据和自定义元数据。

标准元数据指的是主机名、IP地址、端口号、状态页和健康检查等信息，这些信息都会被发布在服务注册表中，用于服务之间的调用。

自定义元数据可以使用eureka.instance.metadata-map配置，这些元数据可以在远程客户端中访问，但一般不会改变客户端的行为，除非客户端知道该元数据的含义。
服务1：
#元数据
``` xml
eureka.instance.metadata-map.my-metada=miozeng
``` 
服务2：
``` java
 @GetMapping("/getuser")
 public User showInfo() throws Exception {
     return discoveryClient.getUser("调用服务1");
 }
``` 
访问http://服务2：端口2/getuser
会返回包含元数据的数据

### Eureka server rest端点
非JVM的微服务可使用REST端点操作Eu-reka，从而实现注册与发现。可参考官方api

### Eureka server 自我保护模式
如果在Eureka Server的首页看到以下这段提示，则说明Eureka已经进入了保护模式。

EMERGENCY! EUREKA MAY BE INCORRECTLY CLAIMING INSTANCES ARE UP WHEN THEY'RE NOT. RENEWALS ARE LESSER THAN THRESHOLD AND HENCE THE INSTANCES ARE NOT BEING EXPIRED JUST TO BE SAFE.

Eureka Server节点短时间内丢失过多用户节点可能进入自我保护模式，保护模式主要用于一组客户端和Eureka Server之间存在网络分区场景下的保护。一旦进入保护模式，Eureka Server将会尝试保护其服务注册表中的信息，不再删除服务注册表中的数据（也就是不会注销任何微服务）。当网络正常后可自动退出自我保护模式。

### Eureka server 健康检查

服务列表当状态为“UP”的时候表示服务正常，
在微服务中配置
``` yml
eureka:
  client:
    healthcheck:
      enabled: true
      
``` 
 即可开启健康检查。此配置最好不要配置bootstrap里面，可能会引起异常
