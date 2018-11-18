dubbo项目学习,
基于guns4.0

API网关特性
    接口功能聚合


dubbo一些特性
1.  启动检查
2.  负载均衡 4种
  - random 随机 默认配置
  - roundRobin  轮询 roundrobin
  - leastActive 最少活跃用数
  - consistentHash 一致性Hash （用的较少）
  
3. 多协议 protocol
常用三种
dubbo : TCP 适合数据包较小 100K 消费者个数较多，常规方式
RMI : TCP 适合数据包大小不一，消费者和提供者数量相差不大
Hessian : HTTP 适合数据包大小不一，消费者和提供者数量相差不大