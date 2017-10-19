# idworker - 基于zookeeper，snowflake算法的分布式ID生成工具
## 是什么？
idworker 是一个基于zookeeper和snowflake算法的分布式ID生成工具，通过zookeeper自动注册机器（最多1024台），无需手动指定workerId和datacenterId

## 怎么用？

```xml
    <idworker:zookeeper id="zkRegistryCenter" server-lists="ip:port,ip:port"/>
    <idworker:generator id="snowflakeGen" group="snowflake-group" registry-center-ref="zkRegistryCenter" />
```
