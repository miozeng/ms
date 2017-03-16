# ms
Microservice demo

_项目基于Spring Boot 1.5.2.RELEASE，Spring Cloud Camden.SR5,各模块简要说明如下：_  

|project|desc|  
|---|---|  
|[ms-eureka-server](ms-eureka-server/README.md)|使用eureka进行服务注册和服务发现.|  
|[ms-config-repo](ms-config-repo/README.md)|配置文件git地址|  
|[ms-spring-config](ms-spring-config/README.md)|使用Spring Cloud Config实现配置集中管理.|  
|[ms-boot-admin](ms-boot-admin/README.md)|Springboot管理|  
|[ms-eureka-client](ms-eureka-client/README.md)|eureka客户端|  
|[ms-data-rest](ms-data-rest/README.md)|Spring data rest使用，提供rest调用，Config配置中心客户端|  
|[ms-eureka-ribbon](ms-eureka-ribbon/README.md)|Ribbon复杂均衡测试使用|  
|[ms-feign-hystrix](ms-feign-hystrix/README.md)|Feign调用服务，使用hystrix实现服务隔离, hystrix dashboard和turbine进行服务监控.|  
|[ms-gateway-zuul](ms-gateway-zuul/README.md)|服务网关|  
|[ms-security-service](ms-security-service/README.md)|安全工具包，为你的应用程序添加安全控制，主要是指OAuth2。(未完待续)|  
|[ms-stream-XXX](ms-stream-send/README.md)|spring-cloud-stream demo 利用stream和rabbitMQ结合写出消息收发demo和聚合demo|  
|[ms-bus-XXX](ms-bus-server/README.md)|spring-cloud-bus动态修改配置文件| 
|[ms-sleuth-zipkin](ms-feign-hystrix/README.md)|分布式追踪解决方案，日志收集工具包，使用了Zipkin(未完待续)|  
|...|spring-cloud-zookeeper(未完待续)|  



## Spring Cloud 
Spring Cloud 为开发者提供了在分布式系统（如配置管理、服务发现、断路器、智能路由、微代理、控制总线、一次性 Token、全局锁、决策竞选、分布式会话和集群状态）操作的开发工具。使用 Spring Cloud 开发者可以快速实现上述这些模式。
Spring cloud 可以基于spring boot，从而快速的生成项目。

## Spring config
Spring Cloud Config配置管理开发工具包，可以让你把配置放到远程服务器，目前支持本地存储、Git以及Subversion。项目提供了一个解决分布式系统的配置管理方案。它包含了Client和Server两个部分.    
Spring Cloud Config Sever的管理git或svn的外部配置，集中配置到所有客户端.        
Spring Cloud Config Client根据Spring框架的Environment和PropertySource从Spring Cloud Config Sever获取配置.     

## spring-cloud-netflix
针对多种Netflix组件提供的开发工具包，其中包括Eureka、Hystrix、Zuul、Archaius等。  
Netflix Eureka：云端负载均衡，一个基于 REST 的服务，用于定位服务，以实现云端的负载均衡和中间层服务器的故障转移。  
Netflix Hystrix：容错管理工具，旨在通过控制服务和第三方库的节点,从而对延迟和故障提供更强大的容错能力。   
Ribbon：客户端负载均衡  
feign: 客户端远程调用   
Netflix Zuul：边缘服务工具，是提供动态路由，监控，弹性，安全等的边缘服务。  
Netflix Archaius：配置管理API，包含一系列配置管理API，提供动态类型化属性、线程安全配置操作、轮询框架、回调机制等功能。  

## spring-cloud-stream
事务型驱动框架，数据流操作开发包，封装了与Redis,Rabbit、Kafka等发送接收消息。

## spring-cloud-bus
事件、消息总线，用于在集群（例如，配置变化事件）中传播状态变化，可与Spring Cloud Config联合实现热部署。

## spring-cloud-sleuth
应用实现了一种分布式追踪解决方案，日志收集工具包，其兼容了Zipkin, HTrace和log-based追踪

## spring-cloud-consul
封装了Consul操作，consul是一个服务发现与配置工具，与Docker容器可以无缝集成。

## spring-cloud-security
实现OAuth2 SSO以及将令牌传递至下游服务等工作。

## spring-cloud-zookeeper
操作Zookeeper的工具包，用于使用zookeeper方式的服务注册和发现。

## Spring Cloud for Cloud Foundry
通过Oauth2协议绑定服务到CloudFoundry，CloudFoundry是VMware推出的开源PaaS云平台。

## spring-cloud-cli
基于 Spring Boot CLI，可以让你以命令行方式快速建立云组件。
