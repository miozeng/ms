为什么要统一管理微服务配置
1.集中管理配置      
2.不同环境不同配置      
3.运行期间可动态调整配置     
4.配置调整后可自动更新    
  
spring cloud config

Spring cloud config 为分布式系统外部化配置提供的服务端和客户端的支持；      
config-server 配置服务端，服务管理配置信息，是可横向扩展，集中式的配置服务器     
config-client 客户端，客户端调用server端暴露接口获取配置信息

### 编写config server
1.在github或者svn或者本机创建几个配置文件如     
https://github.com/miozeng/ms/tree/master/ms-config-repo    
2.创建ms-spring-config项目，添加依赖  
``` xml   
<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-config-server</artifactId>
		</dependency>
```

3启动类添加注解     
@EnableConfigServer    

4.编写application.yml
``` xml 
  cloud:
    config:
      server:
           git:
             uri: https://github.com/miozeng/ms
             searchPaths: ms-config-repo
          

      
      
 #profiles:
 #   active: native
 # cloud:
 #   config:
 #     server:
 #       native:
  #        searchLocations: file:///E:/stsworkspace/ms/ms-config-repo  
``` 
     
configserver 端点      
获取git上的资源信息遵循如下规则：      
/{application}/{profile}[/{label}]     
/{application}-{profile}.yml     
/{label}/{application}-{profile}.yml    
/{application}-{profile}.properties    
/{label}/{application}-{profile}.properties      

application:表示应用名称,在client中通过spring.config.name配置      
profile:表示获取指定环境下配置，例如开发环境、测试环境、生产环境 默认值default，实际开发中可以是 dev、test、demo、production等     
label: git标签，默认值master    

### 编写config Client
1.添加依赖
``` xml 
   </dependency>
		<dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-starter-config</artifactId>
	</dependency>
 ``` 
2.编写配置文件
spring:
   cloud:
     config:
      profile:dev #profile:表示获取指定环境下配置
      label:master #git标签，默认值master
      uri:http://localhost:8888  #config-server暴露的获取配置接口

关于config server的git仓库配置还有很多，比如占位符的支持，模式的匹配，搜素目录，启动时加载配置文件等。不详细说明；    

### config server 健康状况指示器
config server自带健康指示器，通过这个指示器能够检查已经配置的EnvironmentRepository是否正常运行。可以通过/health查看端点查询当前的健康状态，默认情况下请求的{application}是app profile和label是对应的EnvironmentRepository实现的默认配置，git的默认配置是profile是default，label是master，也可以自定义这些配置。
通过设置spring.cloud.config.server.health.enabled=false参数来禁用健康指示器。

### 加密解密
Spring Cloud具有用于在本地解密属性值的环境预处理器。 它遵循与Config Server相同的规则，并通过加密具有相同的外部配置。 因此，您可以使用{cipher} *形式的加密值，只要有一个有效的密钥，那么在主应用程序上下文获取环境之前，它们将被解密。 要在应用程序中使用加密功能，您需要在您的类路径中包含Spring Security RSA（Maven协调“org.springframework.security:spring-security-rsa”），并且还需要JVM中强大的JCE扩展。    

#### 对称加密
config server添加
``` xml
encrypt:
    key:mio #设置对称秘钥
```

测试 post    
加密：    
curl http://localhsot:8080/enrypt -d mysercet     
结果会出来一长串 fdasfa2341sdfa134214….    
解密：    
curl http://localhost:8080/decrypt -d fdasfa2341sdfa134214….     
结果会出来 mysercet

#### 非对称加密测试

需要先生成证书

cmd下执行命令
```java
keytool -genkeypair -alias mytestkey -keyalg RSA -dname "CN=Web Server,OU=Unit,O=Organization,L=City,S=State,C=US" -keypass changeme -keystroe server.jks -storepass letmein
```
将server.jks 文件复制到项目下的classpath      

config server 配置   
在 applicaction.yml中配置   
``` xml
encrypt:
  key-store:
    location: server.jks
    password: letmein
    alias: mytestkey
    secret: changeme
```
测试 post     
加密：     
curl http://localhsot:8080/enrypt -d mysercet     
结果会出来一长串 fdasfa2341sdfa1,34214fdafd2341=….     
解密：     
curl http://localhost:8080/decrypt -d fdasfa2341sdfa1,34214fdafd2341=….    
结果会出来 mysercet        

### 存储加密内容

使用{cipher}密文的形式存储
test:
  pwd: '{cipher}3078cb1c5ed223abc8d492b04625b29f069e481e6c6e39e7fbb65e2b19f920df'

configserver 会自动解密内容。想让server直接返回密文本身，可设置spring.cloud.config.server.encrypt.enabled=false


### 使用refresh端点手动刷新
需要actuator的支持   在controller上添加注解@RefrenshScope     
修改git上面的配置后需要post访问http://host:port/refresh (client的host和ip)  才能刷新配置
