dubbo项目学习,  
基于guns4.0

API网关特性  
    接口功能聚合  


dubbo常用一些特性  
1. 启动检查
2. 负载均衡 4种
   random 随机 默认配置  
   roundRobin  轮询 roundrobin   
   leastActive 最少活跃用数  
   consistentHash 一致性Hash （用的较少）  
  
3. 多协议 protocol  
常用三种  
  dubbo : TCP 适合数据包较小 100K-200K 消费者个数较多，常规方式   
  RMI : TCP 适合数据包大小不一，消费者和提供者数量相差不大  
  Hessian : HTTP 适合数据包大小不一，消费者和提供者数量相差不大   

4. 异步调用  
  syn=true
  
5. 结果缓存  
  lru 基于最近最少使用原则删除多余缓存，保持最热的数据被缓存。（一般用这个）  
  threadLocal  当前线程缓存  
  jcache  
 @reference(cache="lru")  

6. 连接控制  
 一般在提供者设置(注意dubbo是长连接)  
 // 服务端连接数不能超过10个 超出会报错   
 @service(accepts=10)  

7. 并发控制  
// 最多10个线程同时启动 超出会报错   
@service(executes = 10)   
// 每个客户端最多10个线程同时启动 超出会报错  
@service(actives = 10)  

8. 分组

9. 聚合 

10. 多版本，蓝绿上线

11. 限流  
 高可用的一种手段, dubbo里面可以使用并发和连接控制来限流,  
 一般可以使用漏桶法和令牌桶算法进行限流  

12. 熔断,降级  
 集成使用hystrix
 
13. 本地存根  
 类似于代理模式，可以检测数据的正常性，来做一些统一处理  
 stub="com.zl.DemoServiceStu.class"  

14. 本地伪装  
 dubbo中可作为降级的一种手段
 mock="com.zl.DemoBarService.class"  
 mock 默认只会处理RPCException的异常  
 
15. 隐式参数 
 类似于Session设置Attachment的键值对， 后面的流程可以直接取用，参数只能保存一次远程调用，多次就要多次设置



