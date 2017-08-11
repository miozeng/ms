# Spring-Cloud-Sleuth
Spring-Cloud-Sleuth是为SpringCloud应用实现了一种分布式追踪解决方案，其兼容了Zipkin, HTrace和log-based追踪   

### 术语
Span：基本工作单元，例如，在一个新建的span中发送一个RPC等同于发送一个回应请求给RPC，span通过一个64位ID唯一标识，trace以另一个64位ID表示，span还有其他数据信息，比如摘要、时间戳事件、关键值注释(tags)、span的ID、以及进度ID(通常是IP地址)  
span在不断的启动和停止，同时记录了时间信息，当你创建了一个span，你必须在未来的某个时刻停止它。      
Trace：一系列spans组成的一个树状结构，例如，如果你正在跑一个分布式大数据工程，你可能需要创建一个trace。      
Annotation：用来及时记录一个事件的存在，一些核心annotations用来定义一个请求的开始和结束      
cs - Client Sent -客户端发起一个请求，这个annotion描述了这个span的开始      
sr - Server Received -服务端获得请求并准备开始处理它，如果将其sr减去cs时间戳便可得到网络延迟     
ss - Server Sent -注解表明请求处理的完成(当请求返回客户端)，如果ss减去sr时间戳便可得到服务端需要的处理请求时间    
cr - Client Received -表明span的结束，客户端成功接收到服务端的回复，如果cr减去cs时间戳便可得到客户端从服务端获取回复的所有所需时间     
下图描述了一次经过service1-service2-service3-service4的span，Trace和Annotation的变化。
![Image text](https://github.com/miozeng/ms/blob/master/ms-sleuth-service1/trace-id.png)

## 实战
### 创建项目
ms-sleuth-service1
### 添加依赖
```xml
<dependency> 
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-sleuth</artifactId>
</dependency>
```
### 修改application
``` xml
logging:
    level: # 日志级别
        root: INFO
        org.springframework.cloud.sleuth: DEBUG
```
### TEST
访问：http://localhost:9421/
控制台会输出日志，可以看到span创建到关闭的日志
日志显示：  
``` xml
2017-03-16 15:48:48.420  INFO [ms-sleuth-service1,e5c13b14dbc06b95,e5c13b14dbc06b95,false] 7300 --- [nio-9411-exec-1] c.mio.sleuth.MsSleuthZipkinApplication   : you called home   

[zipkin-server,e5c13b14dbc06b95,e5c13b14dbc06b95,false] ==》[appname,traceId,spanId,exportable]

```

## Spring-Cloud-Sleuth与zipkin结合使用
zipkin是Twitter开发的一个可扩展的分布式开源追踪框架，用于存储与查看追踪数据。

### 结合使用
1.编写ms-zipkin-server       
2.添加依赖
```  xml
 <dependency>
  <groupId>io.zipkin.java</groupId>
  <artifactId>zipkin-server</artifactId>
 </dependency>
 <dependency>
  <groupId>io.zipkin.java</groupId>
  <artifactId>zipkin-autoconfigure-ui</artifactId>
  <scope>runtime</scope>
 </dependency>

```

3.启动类添加注解     
@EnableZipkinServer    

4.test
启动server
访问服务http://localhost:9411/可以看到  
![Image text](https://github.com/miozeng/ms/blob/master/ms-sleuth-service1/zipkin.png)

### 微服务整合zipkin
1.ms-sleuth-service2添加依赖
``` xml
	<dependency> 
		<groupId>org.springframework.cloud</groupId> 
		<artifactId>spring-cloud-sleuth-zipkin</artifactId> 
	</dependency> 
```

配置配置文件

spring:
  application:
    name: sleuth-ervice2
  sleuth:
    sampler:
      percentage: 1.0  #采样百分比
  zipkin:
    base-url: http://localhost:9411/  #zipkin的地址
    enabled: true

test  
启动server
启动微服务
访问微服务服务可以获得结果http://localhost:9422/
访问zipkinserver点击 find trances 可以查看结果

### 使用消息中间件
使用消息中间件可以微服务zipkinserver解耦，不需要网络互通。      
#### 新建server ms-sleuth-stream-zipkin    
1.pom依赖       
```xml
  <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-sleuth-zipkin-stream</artifactId>
    </dependency>
 <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-sleuth</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-stream-rabbit</artifactId>
    </dependency>
 <dependency>
	  <groupId>io.zipkin.java</groupId>
	  <artifactId>zipkin-autoconfigure-ui</artifactId>
	  <scope>runtime</scope>
 </dependency>
```

2.添加注解@EnableZipkinStreamServer      


3.配置文件修改为       
``` xml
spring:
  application:
    name: zipkin-server
  rabbitmq:
        host: localhost
        port: 5672
        username: guest
        password: guest
        
server:
   port: 9411
```

### 改造微服务ms-sleuth-service2
1.pom添加以下依赖
``` xml
<dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-sleuth-stream</artifactId>
    </dependency>
 <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-sleuth</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-stream-binder-rabbit</artifactId>
    </dependency>
```

2.修改配置文件     
删除
``` xml
  zipkin:
    base-url: http://localhost:9411/  #zipkin的地址
    enabled: true
```
添加
``` xml
 rabbitmq:
        host: localhost
        port: 5672
        username: guest
        password: guest
```
同样的方法可以改造service1 调用2的callhome方法可以追踪到service2调用了service1

### 存储跟踪数据 
zipkinserver支持多种后端存储，如mysql等。
``` xml
 <!--保存到数据库需要如下依赖-->
 <dependency>
  <groupId>io.zipkin.java</groupId>
  <artifactId>zipkin-autoconfigure-storage-mysql</artifactId>
 </dependency>
 <dependency>
  <groupId>mysql</groupId>
  <artifactId>mysql-connector-java</artifactId>
 </dependency>
 <dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-jdbc</artifactId>
 </dependency>
```

修改配置文件
``` xml
#zipkin数据保存到数据库中需要进行如下配置
#表示当前程序不使用sleuth
spring.sleuth.enabled=false
#表示zipkin数据存储方式是mysql
zipkin.storage.type=mysql
#数据库脚本创建地址，当有多个是可使用[x]表示集合第几个元素
spring.datasource.schema[0]=classpath:/zipkin.sql
#spring boot数据源配置
spring.datasource.url=jdbc:mysql://localhost:3306/zipkin?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false
spring.datasource.username=root
spring.datasource.password=123456
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.initialize=true
spring.datasource.continue-on-error=true
```



   
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
```java
@Bean
public Sampler defaultSampler() {
    return new AlwaysSampler();
}
 ```
###  Instrumentation
Spring Cloud Sleuth自动装配所有Spring应用，因此你不用做任何事来让他工作，装配是使用一系列技术添加的，例如对于一个servlet web应用我们使用一个Filter，对于SpringIntegration我们使用ChannelInterceptors。	
用户可以使用span tags定制关键字，为了限制span数据量，一般一个HTTP请求只会被少数元数据标记，例如status code、host以及URL，用户可以通过配置spring.sleuth.keys.http.headers(一系列头名称)添加request headers。		
tags仅在Sampler允许其被收集和输出时工作(默认情况其不工作，因此不会有在不配置的情况下收集过多数据的意外危险出现		 
