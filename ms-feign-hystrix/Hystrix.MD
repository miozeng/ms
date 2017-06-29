# Hystrix
Hystrix 是Netflix开源，针对分布式系统的延迟和容错库。
## 使用Hystrix容错
主要实现以下几点实现延迟和容错

包裹请求:Hystrix使用命令模式HystrixCommand(Command)包装依赖调用逻辑，每个命令在单独线程中/信号授权下执行。   
跳闸机制:提供熔断器组件,可以自动运行或手动调用,停止当前依赖一段时间(10秒)，熔断器默认错误率阈值为50%,超过将自动运行。   
资源隔离:为每个依赖提供一个小的线程池（或信号），如果线程池已满调用将被立即拒绝，默认不采用排队.加速失败判定时间。
监控:提供近实时的监控运行指标和配置的变化，例如成功，失败，被拒绝，超时的请求等    
回退机制:依赖调用结果分:成功，失败（抛出异常），超时，线程拒绝，短路。 请求失败(异常，拒绝，超时，短路)时执行fallback(降级)逻辑。  
自我修复：断路器打开一段时间后，会进入半开状态，如果有一个请求成功。则再猜关闭断路器


### 整合Hystrix
1.添加依赖
``` xml
<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-hystrix</artifactId>
</dependency>
``` 

2.在启动类上添加注解@EnableCircuitBreaker和@EnableHystrix

3.在controller中添加注解@HystrixCommand(fallbackMethod = "fallback")
``` java
 @HystrixCommand(fallbackMethod = "fallback")
    public String mockGetUserInfo(){
        int randomInt= random.nextInt(10) ;
        if(randomInt<8){  //模拟调用失败情况
            throw new RuntimeException("call dependency service fail.");
        }else{
            return "UserName:liaokailin;number:"+randomInt;
        }
    }

    public String fallback(){
        return "some exception occur call fallback method.";
    }
```
    
4.@HystrixCommand来配置断路器非常灵活，使用注解@HystrixProperty 的commandProperties来配置@HystrixCommand。
 Command Properties
  Execution
    execution.isolation.strategy （执行的隔离策略）
    execution.isolation.thread.timeoutInMilliseconds
    execution.timeout.enabled
    execution.isolation.thread.interruptOnTimeout
    execution.isolation.semaphore.maxConcurrentRequests
  Fallback
    fallback.isolation.semaphore.maxConcurrentRequests
    fallback.enabled
  Circuit Breaker
    circuitBreaker.enabled （断路器开关）
    circuitBreaker.requestVolumeThreshold （断路器请求阈值）
    circuitBreaker.sleepWindowInMilliseconds（断路器休眠时间）
    circuitBreaker.errorThresholdPercentage（断路器错误请求百分比）
    circuitBreaker.forceOpen（断路器强制开启）
    circuitBreaker.forceClosed（断路器强制关闭）
  Metrics
    metrics.rollingStats.timeInMilliseconds
    metrics.rollingStats.numBuckets
    metrics.rollingPercentile.enabled
    metrics.rollingPercentile.timeInMilliseconds
    metrics.rollingPercentile.numBuckets
    metrics.rollingPercentile.bucketSize
    metrics.healthSnapshot.intervalInMilliseconds
  Request Context
    requestCache.enabled
    requestLog.enabled
Collapser Properties
    maxRequestsInBatch
    timerDelayInMilliseconds
    requestCache.enabled
Thread Pool Properties
    coreSize（线程池大小）
    maxQueueSize（最大队列数量）
    queueSizeRejectionThreshold （队列大小拒绝阈值）
    keepAliveTimeMinutes
    metrics.rollingStats.timeInMilliseconds
    metrics.rollingStats.numBuckets
    
    example:
    
@HystrixCommand(groupKey="UserGroup", commandKey = "GetUserByIdCommand"，
                commandProperties = {
                        @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "500")
                },
                threadPoolProperties = {
                        @HystrixProperty(name = "coreSize", value = "30"),
                        @HystrixProperty(name = "maxQueueSize", value = "101"),
        }
        
        
        
### Hystrix断路器的状态监控与深入理解
1、熔断请求判断机制:使用无锁循环队列计数，每个熔断器默认维护10个bucket，每1秒一个bucket，每个blucket记录请求的成功、失败、超时、拒绝的状态，默认错误超过50%且10秒内超过20个请求进行中断拦截。
2、熔断恢复:对于被熔断的请求，每隔5s允许一个请求通过，若请求都是健康的，则对请求健康恢复。
3、熔断器的三种状态:OPEN、HALF-OPEN、CLOSED
添加actuator 之后断路器的状态就会暴露在actuator 提供的/health下面
 <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

当我们访问配了断路器的服务失败调用Hystrix的fallback时，访问一次，我们的断路器服务还是up当访问很多次达到断路器的阈值的时候状态才切换到CIRCUIT_OPEN

### Hystrix线程隔离策略与传播上下文
 Hystrix组件提供了两种隔离的解决方案：
 线程池隔离:HystrixCommand将会在单独的线程上执行，并发请求受线程池中的线程数量的限制
 信号量隔离：HystrixCommand将会在调用线程上执行，开销相对较小，并发请求收到信号量个数的限制
 Hystrix默认使用线程池隔离，一般来说只有当负载非常高是考虑使用信号量隔离。
  
  
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
```java
@FeignClient(name = "hello", fallbackFactory = HystrixClientFallbackFactory.class)
protected interface HystrixClient {
    @RequestMapping(method = RequestMethod.GET, value = "/hello")
    Hello iFailSometimes();
}

@Component
static class HystrixClientFallbackFactory implements FallbackFactory<HystrixClient> {
    @Override
    public HystrixClient create(Throwable cause) {
        return new HystrixClientWithFallBackFactory() {
            @Override
            public Hello iFailSometimes() {
                return new Hello("fallback; reason was: " + cause.getMessage());
            }
        };
    }
}
```
#### Feign禁用Hystrix
在spring cloud项目中，只要Hystrix在classpath下，Feign就会使用断路器，
1.编写配置文件，这个在用例中有超便宜过来
```java
@Configuration
public class FooConfiguration {
	@Bean
	@Scope("prototype")
	public Feign.Builder feignBuilder() {
		return Feign.builder();
	}
}
```
2.FeignClient(name = "EUREKACLIENT",  configuration=FooConfiguration.class)

或者进行全局配置
feign.hystrix.enabled=false;



## Hystrix监控
Hystrix项目只要添加了actuator就可以通过/hystrix.stream端点获取监控信息

## Hystrix dashboard可视化监控数据
1.添加依赖
```xml
	<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-hystrix-dashboard</artifactId>
		</dependency>
```

2.添加注解@EnableHystrixDashboard

访问http://localhost:3333/hystrix
页面上会有提示信息：
Cluster via Turbine (default cluster): http://turbine-hostname:port/turbine.stream   
Cluster via Turbine (custom cluster): http://turbine-hostname:port/turbine.stream?cluster=[clusterName]  
Single Hystrix App: http://hystrix-app:port/hystrix.stream   
大概意思就是如果查看默认集群使用第一个url,查看指定集群使用第二个url,单个应用的监控使用最后一个，  
我们暂时只演示单个应用的所以在输入框中输入： http://localhost:3333/hystrix.stream ，输入之 后点击 monitor，进入页面。  
如果没有请求会先显示Loading ...，访问http://localhost:3333/hystrix.stream 也会不断的显示ping。   

访问http://desktop-2vj9feq:3333/test/hello 会发现页面数据有变化

hystrix-dashboard-1.png
Hystrix Dashboard Wiki上详细说明了图上每个指标的含义，如下图：
hystrix-dashboard-2.png
到此单个应用的熔断监控已经完成。

## Hturbine聚合监控数据
上面的例子是单点监控，如果需要同时监控多个服务器，可以使用turbine，聚合Hystrix监控
在复杂的分布式系统中，相同服务的节点经常需要部署上百甚至上千个，很多时候，运维人员希望能够把相同服务的节点状态以一个整体集群的形式展现出来，这样可以更好的把握整个系统的状态。 为此，Netflix提供了一个开源项目（Turbine）来提供把多个hystrix.stream的内容聚合为一个数据源供Dashboard展示。

看一个实例Hystrix数据对于整个系统的健康不是很有用。turbine是一个应用程序,该应用程序汇集了所有相关的/hystrix.stream端点到 /turbine.stream用于Hystrix仪表板。运行turbine使用@EnableTurbine注释你的主类，使用spring-cloud-starter-turbine这个jar


1.添加依赖
2.添加注解
3.修改配置文件

结合MQ
1.改造微服务
2.改造turbine

