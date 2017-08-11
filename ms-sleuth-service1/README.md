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


## 实战
### 创建项目

### 添加依赖
'''xml
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
访问：这个自己补全
控制台会输出日志，可以看到span创建到关闭的日志


## Spring-Cloud-Sleuth与zipkin结合使用
zipkin是Twitter开发的一个可扩展的分布式开源追踪框架，用于存储与查看追踪数据。

### 结合使用
1.编写zipkin server
1.添加依赖
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

2.启动类添加注解     
@EnableZipkinServer    

test
启动server
访问服务http://host:port可以看到  


### 微服务整合zipkin
1.添加依赖
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
访问微服务服务可以获得结果
访问zipkinserver查看trance

### 使用消息中间件
使用消息中间件可以微服务zipkinserver解耦，不需要网络互通。      
#### 改造zipkin server
1.pom依赖改为
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

2.注解@EnableZipkinServer 改为EnableZipkinStreamServer
### 存储跟踪数据

3.配置文件修改为
``` xml
 rabbitmq:
        host: localhost
        port: 5672
        username: guest
        password: guest
```

### 改造微服务
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

### 存储跟踪数据 
zipkinserver支持多种后端存储，如mysql等。
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
