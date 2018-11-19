dubbo项目学习, <br/> 
基于guns4.0

API网关特性 <br/> 
    接口功能聚合 <br/> 


dubbo一些特性 <br/> 
1. 启动检查
2. 负载均衡 4种
  - random 随机 默认配置
  - roundRobin  轮询 roundrobin
  - leastActive 最少活跃用数
  - consistentHash 一致性Hash （用的较少）
  
3. 多协议 protocol <br/> 
常用三种 <br/> 
- dubbo : TCP 适合数据包较小 100K 消费者个数较多，常规方式
- RMI : TCP 适合数据包大小不一，消费者和提供者数量相差不大
- Hessian : HTTP 适合数据包大小不一，消费者和提供者数量相差不大

4. dubbo 异步调用 <br/> 


5. 结果缓存 <br/> 
- lru 基于最近最少使用原则删除多余缓存，保持最热的数据被缓存。（一般用这个）
- threadlocal  当前线程缓存
- jcache <br/> 
@reference(cache="lru")


6. 连接控制 <br/> 
一般在提供者设置(注意dubbo是长连接) <br/> 
// 服务端连接数不能超过10个 超出会报错 <br/>
@service(accepts=10)

7. 并发控制 <br/> 
// 最多10个线程同时启动 超出会报错 <br/> 
@service(executes = 10)  <br/> 
// 每个客户端最多10个线程同时启动 超出会报错 <br/> 
@service(actives = 10) <br/> 
