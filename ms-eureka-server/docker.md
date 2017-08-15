## docker
#### 1. docker file 方式
a.copy ms-eureka-server-0.0.1-SNAPSHOT.jar到 Dockerfile 同一目录      
b.docker build -t miozeng/ms-eureka-server Dockerfile的位置      
c.docker run -p 8761:8761 miozeng/ms-eureka-server      

#### 2.maven插件 方式 
a.编写pom.xml       
b.mvn package docker:build        
c.docker run -p 8761:8761 -t miozeng/ms-eureka-server                   

其他说明  
Profiles：                   
$docker run -e "SPRING_PROFILES_ACTIVE=dev" -p 8080:8080 -t springio/gs-spring-boot-docker                
Debug：            
$ docker run -e "JAVA_OPTS=-agentlib:jdwp=transport=dt_socket,address=5005,server=y,suspend=n" -p 8080:8080 -p 5005:5005 -t miozeng/cachetest               


#stop       
docker stop miozeng/ms-eureka-server        
#remove container       
docker rm miozeng/ms-eureka-server        
#remove images        
docker rmi miozeng/ms-eureka-server         
