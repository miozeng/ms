## 使用Maven插件构建Docker镜像

Docker maven插件有很多种比如如下的   

|插件名称|官方地址|  
|---|---|  
|docker-maven-plugin|https://github.com/spotify/docker-maven-plugin|  
|docker-maven-plugin|https://github.com/fabric8io/docker-maven-plugin|  
|docker-maven-plugin|https://github.com/bibryam/docker-maven-pluginn|  
    
我们以第一种为例子   
1.简单使用   
在pom.xml中添加下面这段  
```xml
    <build>
        <plugins>
            <!-- docker的maven插件，官网：https://github.com/spotify/docker-maven-plugin -->
           	<plugin>
		<groupId>com.spotify</groupId>
                <artifactId>docker-maven-plugin</artifactId>
                <version>0.4.11</version>
				<configuration>
					<imageName>${docker.image.prefix}/${project.artifactId}</imageName>
					<baseImage>java</baseImage>
					<dockerDirectory>src/main/docker</dockerDirectory>
					<resources>
						<resource>
							<targetPath>/</targetPath>
							<directory>${project.build.directory}</directory>
							<include>${project.build.finalName}.jar</include>
						</resource>
					</resources>
				</configuration>
			</plugin>
        </plugins>
</build>
```
执行命令：   
mvn clean package docker:build   
   
2.Dockerfile   
建立文件Dockerfile   
```xml
FROM java:8
VOLUME /tmp
ADD microservice-discovery-eureka-0.0.1-SNAPSHOT.jar app.jar
RUN bash -c 'touch /app.jar'
EXPOSE 9000
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
```
   
修改pom.xml   
```xml
<build>
        <plugins>
            <!-- docker的maven插件，官网：https://github.com/spotify/docker-maven-plugin -->
            <plugin>
                <groupId>com.spotify</groupId>
                <artifactId>docker-maven-plugin</artifactId>
                <version>0.4.12</version>
                <configuration>
                    <!-- 注意imageName一定要是符合正则[a-z0-9-_.]的，否则构建不会成功 -->
                    <!-- 详见：https://github.com/spotify/docker-maven-plugin    Invalid repository name ... only [a-z0-9-_.] are allowed-->
                    <imageName>microservice-discovery-eureka-dockerfile</imageName>
                    <!-- 指定Dockerfile所在的路径 -->
                    <dockerDirectory>${project.basedir}/src/main/docker</dockerDirectory>
                    <resources>
                        <resource>
                            <targetPath>/</targetPath>
                            <directory>${project.build.directory}</directory>
                            <include>${project.build.finalName}.jar</include>
                        </resource>
                    </resources>
                </configuration>
            </plugin>
        </plugins>
</build>
```
   
3.将Docker镜像push到DockerHub上   
首先修改Maven的全局配置文件settings.xml，添加以下段落   
```xml
<servers>
  <server>
    <id>docker-hub</id>
    <username>你的DockerHub用户名</username>
    <password>你的DockerHub密码</password>
    <configuration>
      <email>你的DockerHub邮箱</email>
    </configuration>
  </server>
</servers>
```  
在DockerHub上创建repo  
项目pom.xml修改为如下：注意imageName的路径要和repo的路径一致   
```xml
<build>
        <plugins>
            <!-- docker的maven插件，官网：https://github.com/spotify/docker-maven-plugin -->
            <plugin>
                <groupId>com.spotify</groupId>
                <artifactId>docker-maven-plugin</artifactId>
                <version>0.4.12</version>
                <configuration>
                    <!-- 注意imageName一定要是符合正则[a-z0-9-_.]的，否则构建不会成功 -->
                    <!-- 详见：https://github.com/spotify/docker-maven-plugin Invalid repository 
                        name ... only [a-z0-9-_.] are allowed -->
                    <!-- 如果要将docker镜像push到DockerHub上去的话，这边的路径要和repo路径一致 -->
                    <imageName>eacdy/test</imageName>
                    <!-- 指定Dockerfile所在的路径 -->
                    <dockerDirectory>${project.basedir}/src/main/docker</dockerDirectory>
                    <resources>
                        <resource>
                            <targetPath>/</targetPath>
                            <directory>${project.build.directory}</directory>
                            <include>${project.build.finalName}.jar</include>
                        </resource>
                    </resources>
                    <!-- 以下两行是为了docker push到DockerHub使用的。 -->
                    <serverId>docker-hub</serverId>
                    <registryUrl>https://index.docker.io/v1/</registryUrl>
                </configuration>
            </plugin>
        </plugins>
    </build>
    ```   
执行命令：   
mvn clean package docker:build  -DpushImage   
搞定，等构建成功后，我们会发现Docker镜像已经被push到DockerHub上了。   
   
4.将镜像push到私有仓库   
在很多场景下，我们需要将镜像push到私有仓库中去，这边为了讲解的全面性，私有仓库采用的是配置登录认证的私有仓库。   

和push镜像到DockerHub中一样，我们首先需要修改Maven的全局配置文件settings.xml，添加以下段落    
```xml
<servers>
  <server>
    <id>docker-registry</id>
    <username>你的DockerHub用户名</username>
    <password>你的DockerHub密码</password>
    <configuration>
      <email>你的DockerHub邮箱</email>
    </configuration>
  </server>
</servers>
```
将项目的pom.xml改成如下，
```xml
<plugin>
  <groupId>com.spotify</groupId>
  <artifactId>docker-maven-plugin</artifactId>
  <version>0.4.12</version>
  <configuration>
    <!-- 路径为：私有仓库地址/你想要的镜像路径 -->
    <imageName>reg.itmuch.com/test-pull-registry</imageName>
    <dockerDirectory>${project.basedir}/src/main/docker</dockerDirectory>

    <resources>
      <resource>
        <targetPath>/</targetPath>
        <directory>${project.build.directory}</directory>
        <include>${project.build.finalName}.jar</include>
      </resource>
    </resources>

    <!-- 与maven配置文件settings.xml一致 -->
    <serverId>docker-registry</serverId>
  </configuration>
</plugin>
```
执行：  
mvn clean package docker:build  -DpushImage    
稍等片刻，将会push成功。   

如果想要从私服上下载该镜像，执行：   
docker login reg.itmuch.com  # 然后输入账号和密码   
docker pull reg.itmuch.com/test-pull-registry   
5.将插件绑定在某个phase执行    
在很多场景下，我们有这样的需求，例如执行mvn clean package 时，自动地为我们构建docker镜像，可以吗？答案是肯定的。我们只需要将插件的goal 绑定在某个phase即可。   

所谓的phase和goal，可以这样理解：maven命令格式是：mvn phase:goal ，例如mvn package docker:build 那么，package 和 docker 都是phase，build 则是goal 。     

下面是示例：  
首先配置属性：  
```xml
<properties>
  <docker.image.prefix>reg.itmuch.com</docker.image.prefix>
</properties>
```
插件配置：
```xml
  <build>
    <plugins>
      <plugin>
        <groupId>com.spotify</groupId>
        <artifactId>docker-maven-plugin</artifactId>
        <executions>
          <execution>
            <id>build-image</id>
            <phase>package</phase>
            <goals>
              <goal>build</goal>
            </goals>
          </execution>
        </executions>
        <configuration>
          <imageName>${docker.image.prefix}/${project.artifactId}</imageName>
          <baseImage>java</baseImage>
          <entryPoint>["java", "-jar", "/${project.build.finalName}.jar"]</entryPoint>
          <resources>
            <resource>
              <targetPath>/</targetPath>
              <directory>${project.build.directory}</directory>
              <include>${project.build.finalName}.jar</include>
            </resource>
          </resources>
        </configuration>
      </plugin>
    </plugins>
  </build>
  ```
如上，我们只需要添加：
```xml
        <executions>
          <execution>
            <id>build-image</id>
            <phase>package</phase>
            <goals>
              <goal>build</goal>
            </goals>
          </execution>
        </executions>
	```
即可。本例指的是讲docker的build目标，绑定在package这个phase上。   
也就是说，用户只需要执行mvn package ，就自动执行了mvn docker:build 。    
