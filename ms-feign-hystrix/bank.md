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
 <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-hystrix</artifactId>
  </dependency>

 
2.在启动类上添加注解@EnableCircuitBreaker和@EnableHystrix

3.在controller中添加注解@HystrixCommand(fallbackMethod = "fallback")

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
    
 4@HystrixCommand来配置断路器非常灵活，使用注解@HystrixProperty 的commandProperties来配置@HystrixCommand。
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
#### 为feign添加回退
这个源码贴上就好

#### 通过fallback factory 检查回退原因（test）
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
#### Feign禁用Hystrix
在spring cloud项目中，只要Hystrix在classpath下，Feign就会使用断路器，
1.编写配置文件，这个在用例中有超便宜过来
2.FeignClient configuration=以上的配置问

或者进行全局配置
feign.hystrix.enabled=false;
## Hystrix监控
Hystrix项目只要添加了actuator就可以通过/hystrix.stream端点获取监控信息

## Hystrix dashboard可视化监控数据
1添加 pom的东西
2.添加注解 @...Dashbbord..
## turbine聚合监控数据(ceshi)
上面的例子是单点监控，如果需要同时监控多个服务器，可以使用turbine，聚合Hystrix监控
1.添加依赖
2.添加注解
3.修改配置文件

结合MQ
1.改造微服务
2.改造turbine


