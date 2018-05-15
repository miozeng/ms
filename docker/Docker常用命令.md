## docker 架构
#### docker daemon
是一个运行在宿主机的后台进程，可以用个docker客户端与之通信
#### docker client
docker 的用户界面，可以接受用户命令和配置标识，并与 docker daemon通信
#### Images
docker镜像是一个只读模板，可以包含创建docker容器的说明，使用docker镜像可以运行docker镜像中的程序
#### container
容器是镜像的可运行实例，
#### registry
registry是一个集中存储和分发镜像的服务，构建完整的镜像后就可以在当前宿主机上运行，一个registry可以包含多个docker仓库，每个仓库含有多个镜像标签，每个标签对应一个docker镜像，registry也是可以分为共有和私有，最常用的共有registry就是docker hub


![Image text](https://github.com/miozeng/Review/blob/master/docker/docker.png)

## Docker常用命令

#### 1.Docker常用命令
1.查看docker信息（version、info）
查看docker版本     
$docker version          
显示docker系统的信息          
$docker info      
 
#### 2.image常用命令
对image的操作（search、pull、images、rmi、history）   
1.检索image      
$docker search image_name     
    
2.下载image      
$docker pull image_name      
    
3.发布image（push）    
发布docker镜像      
$docker push new_image_name     
    
4.列出镜像列表;   
 -a, --all=false Show all images; --no-trunc=false Don't truncate output; -q, --quiet=false Only show numeric IDs     
$docker images      
    
5.删除一个或者多个镜像;   
 -f, --force=false Force; --no-prune=false Do not delete untagged parents      
$docker rmi image_name     

6.显示一个镜像的历史;   
 --no-trunc=false Don't truncate output; -q, --quiet=false Only show numeric IDs    
$docker history image_name     

7.登录registry server（login）   
登陆registry server; -e, --email="" Email; -p, --password="" Password; -u, --username="" Username     
$docker login     
    



#### 3.Docke容器常用命令
image相当于类，container相当于实例，不过可以动态给实例安装新软件，然后把这个container用commit命令固化成一个image。            
1.启动容器（run）          
docker容器可以理解为在沙盒中运行的进程。这个沙盒包含了该进程运行所必须的资源，包括文件系统、系统类库、shell 环境等等。但这个沙盒默认是不会运行任何程序的。你需要在沙盒中运行一个进程来启动某一个容器。这个进程是该容器的唯一进程，所以当该进程结束的时候，容器也会完全的停止。    
在容器中运行"echo"命令，输出"hello word"     
$docker run image_name echo "hello word"      
交互式进入容器中       
$docker run -i -t image_name /bin/bash         
在容器中安装新的程序          
$docker run image_name apt-get install -y app_name          
   
2.查看容器（ps）   
列出当前所有正在运行的container     
$docker ps      
列出所有的container     
$docker ps -a     
列出最近一次启动的container     
$docker ps -l      
保存对容器的修改（commit）     
当你对某一个容器做了修改之后（通过在容器中运行某一个命令），可以把对容器的修改保存下来，这样下次可以从保存后的最新状态运行该容器。    

3.保存对容器的修改;     
 -a, --author="" Author; -m, --message="" Commit message      
$docker commit ID new_image_name       
     
对容器的操作（rm、stop、start、kill、logs、diff、top、cp、restart、attach）     
4.删除所有容器     
$docker rm     $(docker ps -aq)
     
删除单个容器;    
 -f, --force=false; -l, --link=false Remove the specified link and not the underlying container; -v, -- volumes=false Remove the volumes associated to the container
 
$docker rm Name/ID       
      
5.停止、启动、杀死一个容器      
$docker stop Name/ID    

$docker start Name/ID    

$docker kill Name/ID             
    
6.从一个容器中取日志;   
 -f, --follow=false Follow log output; -t, --timestamps=false Show timestamps     
$docker logs Name/ID           
   
7.列出一个容器里面被改变的文件或者目录，list列表会显示出三种事件，A 增加的，D 删除的，C 被改变的     
$docker diff Name/ID     
     
8.显示一个运行的容器里面的进程信息     
$docker top Name/ID     
                  
9.从容器里面拷贝文件/目录到本地一个路径     
$docker cp Name:/container_path to_path    
$docker cp ID:/container_path to_path      
              
10.重启一个正在运行的容器;   
-t, --time=10 Number of seconds to try to stop for before killing the container, Default=10    
$docker restart Name/ID     
                 
11.附加到一个运行的容器上面;   
 --no-stdin=false Do not attach stdin; --sig-proxy=true Proxify all received signal to the process     
$docker attach ID     
  
Note： attach命令允许你查看或者影响一个运行的容器。你可以在同一时间attach同一个容器。你也可以从一个容器中脱离出来，是从CTRL-C。   
  
12.保存和加载镜像（save、load）   
当需要把一台机器上的镜像迁移到另一台机器的时候，需要保存镜像与加载镜像。  
保存镜像到一个tar包;   
-o, --output="" Write to an file      
$docker save image_name -o file_path     
加载一个tar包格式的镜像;   
 -i, --input="" Read from a tar archive file    
$docker load -i file_path      
机器a     
$docker save image_name > /home/save.tar     
使用scp将save.tar拷到机器b上，然后：     
$docker load < /home/save.tar    


13.导出容器快照到本地文件(docker export)：
首先获取容器id：
docker ps -a
导出容器到本地镜像库：
docker export container_id > centos.tar

14.导入容器快照为镜像(docker import)：
(1)容器在本地：                      
cat centos.tar | docker import - registry.intra.weibo.com/yushuang3/centos:v2.0                   
(2)容器在网络上：                     
docker import http://example.com/exampleimage.tgz registry.intra.weibo.com/yushuang3/centos:v2.0                         
注意：                
用户既可以使用 docker load 来导入镜像存储文件到本地镜像库，            
也可以使用 docker import 来导入一个容器快照到本地镜像库。                    
这两者的区别在于容器快照文件将丢弃所有的历史记录和元数据信息（即仅保存容器当时的快照状态），               
而镜像存储文件将保存完整记录，体积也要大。此外，从容器快照文件导入时可以重新指定标签等元数据信息。                       

 

#### 3.镜像加速器   

Docker Hub 在国外，有时候拉取 Image 极其缓慢，可以使用国内的镜像来实现加速         
阿里云  
echo "DOCKER_OPTS=\"--registry-mirror=https://yourlocation.mirror.aliyuncs.com\"" | sudo tee -a /etc/default/docker  
sudo service docker restart   
其中 https://yourlocation.mirror.aliyuncs.com 是您在阿里云注册后的专属加速器地址：  


DaoCloud  
sudo echo “DOCKER_OPTS=\”\$DOCKER_OPTS –registry-mirror=http://your-id.m.daocloud.io -d\”” >> /etc/default/docker  
sudo service docker restart   
其中 http://your-id.m.daocloud.io 是您在 DaoCloud 注册后的专属加速器地址：   



Windows 10  
对于使用 WINDOWS 10 的系统，在系统右下角托盘图标内右键菜单选择  
Settings ，打开配置窗口后左侧导航菜单选择 Docker Daemon 。编辑窗口内  
的JSON串，填写如阿里云、DaoCloud之类的加速器地址，如：   
{  
"registry-mirrors": [  
"https://zmwa1utj.mirror.aliyuncs.com"  
],
"insecure-registries": []  
}  
编辑完成，点击Apply保存后Docker服务会重新启动。   
