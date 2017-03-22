## Zuul 
是在云平台上提供动态路由，监控，弹性，安全等边缘服务的框架。Zuul 相当于是设备和 Netflix 流应用的 Web 网站后端所有请求的前门。Zuul 可以适当的对多个 Amazon Auto Scaling Groups 进行路由请求。

 Zuul提供了一个框架，可以对过滤器进行动态的加载，编译，运行。过滤器之间没有直接的相互通信。他们是通过一个RequestContext的静态类来进行数据传递的。RequestContext类中有ThreadLocal变量来记录每个Request所需要传递的数据。 
过滤器是由Groovy写成。这些过滤器文件被放在Zuul Server上的特定目录下面。Zuul会定期轮询这些目录。修改过的过滤器会动态的加载到Zuul Server中以便于request使用。
下面有几种标准的过滤器类型：  
PRE：这种过滤器在请求到达Origin Server之前调用。比如身份验证，在集群中选择请求的Origin Server，记log等。   
ROUTING：在这种过滤器中把用户请求发送给Origin Server。发送给Origin Server的用户请求在这类过滤器中build。并使用Apache HttpClient或者Netfilx Ribbon发送给Origin Server。   
POST：这种过滤器在用户请求从Origin Server返回以后执行。比如在返回的response上面加response header，做各种统计等。并在该过滤器中把response返回给客户。    
ERROR：在其他阶段发生错误时执行该过滤器。   
客户定制：比如我们可以定制一种STATIC类型的过滤器，用来模拟生成返回给客户的response。   
过滤器的生命周期如下所示：    

![Image text](https://github.com/miozeng/ms/blob/master/ms-gateway-zuul/image.png)

Zuul可以通过加载动态过滤机制，从而实现以下各项功能：   
验证与安全保障: 识别面向各类资源的验证要求并拒绝那些与要求不符的请求。   
审查与监控: 在边缘位置追踪有意义数据及统计结果，从而为我们带来准确的生产状态结论。   
动态路由: 以动态方式根据需要将请求路由至不同后端集群处。  
压力测试: 逐渐增加指向集群的负载流量，从而计算性能水平。  
负载分配: 为每一种负载类型分配对应容量，并弃用超出限定值的请求。  
静态响应处理: 在边缘位置直接建立部分响应，从而避免其流入内部集群。   
多区域弹性: 跨越AWS区域进行请求路由，旨在实现ELB使用多样化并保证边缘位置与使用者尽可能接近。   
除此之外，Netflix公司还利用Zuul的功能通过金丝雀版本实现精确路由与压力测试。     
 
