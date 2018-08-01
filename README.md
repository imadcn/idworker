## idworker - 基于zookeeper，snowflake的分布式统一ID生成工具

[![Build Status](https://travis-ci.org/imadcn/idworker.svg?branch=master)](https://travis-ci.org/imadcn/idworker)
[![Coverage Status](https://coveralls.io/repos/imadcn/idworker/badge.svg?branch=master&service=github)](https://coveralls.io/github/imadcn/idworker?branch=master)
[![Maven Central](https://img.shields.io/maven-central/v/com.imadcn.framework/idworker.svg)](http://mvnrepository.com/artifact/com.imadcn.framework/idworker)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

### 是什么
idworker 是一个基于zookeeper和snowflake算法的分布式统一ID生成工具，通过zookeeper自动注册机器（最多1024台），无需手动指定workerId和datacenterId

### 怎么用
#### Maven

```xml
<dependency>
  <groupId>com.imadcn.framework</groupId>
  <artifactId>idworker</artifactId>
  <version>${latest.version}</version>
</dependency>
```

#### XML配置 

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:idworker="http://code.imadcn.com/schema/idworker"
    xsi:schemaLocation="
        http://www.springframework.org/schema/beans 
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://code.imadcn.com/schema/idworker
        http://code.imadcn.com/schema/idworker/idworker.xsd">
        
    <idworker:registry id="zkRegistryCenter" server-lists="host1:port1,host2:port2"/>
    <idworker:generator id="snowflakeGenerator" registry-center-ref="zkRegistryCenter" />
</beans>

```

#### API

```java
@Autowired
public IdGenerator generator;

public void id() {
    long id = generator.nextId(); // Long型ID(64进制UUID不支持)，随着时间推移，ID长度为7-19位
    long[] ids = generator.nextId(100_000); // 批量Long型ID(64进制UUID不支持)，最多10w个
	
    String strId = generator.nextStringId(); // 字符串格式ID
    String fixedId = generator.nextFixedStringId(); // 固定19位长度字符串Id
}

```

### 配置参考
#### <idworker:registry /> 注册中心配置，如zookeeper（64进制UUID策略可不配置注册中心）

|属性|类型|必填|缺省值|描述|
|:------|:------|:------|:------|:------|
|id|String|是| |Spring容器中的ID|
|server-lists|String|是| |连接Zookeeper服务器的列表<br/>包括IP地址和端口号<br/>多个地址用逗号分隔<br/>如: host1:2181,host2:2181|
|namespace|String|否|idworker|Zookeeper的命名空间|
|base-sleep-time-milliseconds|int|否|1000|等待重试的间隔时间的初始值<br/>单位：毫秒|
|max-sleep-time-milliseconds|int|否|3000|等待重试的间隔时间的最大值<br/>单位：毫秒|
|max-retries|int|否|3|最大重试次数|
|session-timeout-milliseconds|int|否|60000|会话超时时间<br/>单位：毫秒|
|connection-timeout-milliseconds|int|否|15000|连接超时时间<br/>单位：毫秒|
|digest|String|否| |连接Zookeeper的权限令牌<br/>缺省为不需要权限验证|

#### <idworker:generator /> ID生成策略配置

|属性|类型|必填|缺省值|描述|
|:------|:------|:------|:------|:------|
|id|String|是| |Spring容器中的ID|
|strategy|String|是|snowflake|ID生成[snowflake, compress_uuid]，当策略为64进制uuid时，registry-center-ref可不用配置|
|registry-center-ref|String|否| |注册中心SpringBeanRef，当生成策略为snowflake时，必填|
|group|String|否|default|分组名，可以为不同业务分配分组，独立注册|

#### <generator:snowflake /> 生成策略 : snowflake模式

|属性|类型|必填|缺省值|描述|
|:------|:------|:------|:------|:------|
|id|String|是| |Spring容器中的ID|
|registry-center-ref|String|是| |注册中心SpringBeanRef|
|group|String|否|default|分组名，可以为不同业务分配分组，独立注册|

#### <generator:compress-uuid /> 生成策略 : 64进制UUID模式

|属性|类型|必填|缺省值|描述|
|:------|:------|:------|:------|:------|
|id|String|是| |Spring容器中的ID|
