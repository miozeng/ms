# Hystrix
Hystrix 是Netflix开源，针对分布式系统的延迟和容错库。

1:Hystrix使用命令模式HystrixCommand(Command)包装依赖调用逻辑，每个命令在单独线程中/信号授权下执行。   
2:可配置依赖调用超时时间,超时时间一般设为比99.5%平均时间略高即可.当调用超时时，直接返回或执行fallback逻辑。   
3:为每个依赖提供一个小的线程池（或信号），如果线程池已满调用将被立即拒绝，默认不采用排队.加速失败判定时间。    
4:依赖调用结果分:成功，失败（抛出异常），超时，线程拒绝，短路。 请求失败(异常，拒绝，超时，短路)时执行fallback(降级)逻辑。   
5:提供熔断器组件,可以自动运行或手动调用,停止当前依赖一段时间(10秒)，熔断器默认错误率阈值为50%,超过将自动运行。   
6:提供近实时依赖的统计和监控     


## 使用Hystrix容错

### 整合Hystrix
1.添加依赖
``` xml
<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-hystrix</artifactId>
</dependency>
``` 

2.添加注解
```java
@EnableCircuitBreaker
```

### Hystrix断路器的状态监控与深入理解
1、熔断请求判断机制
使用无锁循环队列计数，每个熔断器默认维护10个bucket，每1秒一个bucket，每个blucket记录请求的成功、失败、超时、拒绝的状态，默认错误超过50%且10秒内超过20个请求进行中断拦截。
2、熔断恢复
对于被熔断的请求，每隔5s允许一个请求通过，若请求都是健康的，则对请求健康恢复。
3、熔断器的三种状态
OPEN、HALF-OPEN、CLOSED

### Hystrix线程隔离策略与传播上下文

### Feign使用Hystrix
```java
@FeignClient(name = "EUREKACLIENT", fallback = HystrixClientFallback.class)//
public interface HystrixClient {

	@RequestMapping(method = RequestMethod.GET, value = "/config/")
	String iFailSometimes();
}
```
#### 为feign添加回退
```java
@Component
public class HystrixClientFallback implements HystrixClient {

	@Override
	public String iFailSometimes() {
		return "fallback";
	}

	@HystrixCommand(fallbackMethod = "defaultStores")
	public String test() {
		return "熔断测试";
	}

}
```
#### 通过fallback factory 检查回退原因

#### Feign禁用Hystrix


## Hystrix监控
{
    "hystrix": {
        "openCircuitBreakers": [
            "StoreIntegration::getStoresByLocationLink"
        ],
        "status": "CIRCUIT_OPEN"
    },
    "status": "UP"
}
## Hystrix dashboard可视化监控数据

http://localhost:3333/hystrix
图上会有提示信息：

Cluster via Turbine (default cluster): http://turbine-hostname:port/turbine.stream 
Cluster via Turbine (custom cluster): http://turbine-hostname:port/turbine.stream?cluster=[clusterName]
Single Hystrix App: http://hystrix-app:port/hystrix.stream 
大概意思就是如果查看默认集群使用第一个url,查看指定集群使用第二个url,单个应用的监控使用最后一个，
我们暂时只演示单个应用的所以在输入框中输入： http://localhost:3333/hystrix.stream ，输入之后点击 monitor，进入页面。
如果没有请求会先显示Loading ...，访问http://localhost:3333/hystrix.stream 也会不断的显示ping。
访问http://desktop-2vj9feq:3333/test/hello 会发现页面数据有变化
hystrix-dashboard-1.png
Hystrix Dashboard Wiki上详细说明了图上每个指标的含义，如下图：
hystrix-dashboard-2.png
到此单个应用的熔断监控已经完成。
## Hturbine聚合监控数据
在复杂的分布式系统中，相同服务的节点经常需要部署上百甚至上千个，很多时候，运维人员希望能够把相同服务的节点状态以一个整体集群的形式展现出来，这样可以更好的把握整个系统的状态。 为此，Netflix提供了一个开源项目（Turbine）来提供把多个hystrix.stream的内容聚合为一个数据源供Dashboard展示。

看一个实例Hystrix数据对于整个系统的健康不是很有用。turbine是一个应用程序,该应用程序汇集了所有相关的/hystrix.stream端点到 /turbine.stream用于Hystrix仪表板。运行turbine使用@EnableTurbine注释你的主类，使用spring-cloud-starter-turbine这个jar

