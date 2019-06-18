## idworker - A distributed ID generator tool based on Zookeeper and Snowflake algorithm

[![Build Status](https://travis-ci.org/imadcn/idworker.svg?branch=master)](https://travis-ci.org/imadcn/idworker)
[![Coverage Status](https://coveralls.io/repos/imadcn/idworker/badge.svg?branch=master&service=github)](https://coveralls.io/github/imadcn/idworker?branch=master)
[![Maven Central](https://img.shields.io/maven-central/v/com.imadcn.framework/idworker.svg)](http://mvnrepository.com/artifact/com.imadcn.framework/idworker)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

### What it is
idworker is a distributed ID generator tool based on `Zookeeper` and `Snowflake` algorithm, it will automatically register itself to Zookeeper without configuring WorkerId and DataCenterId manually。

### How to use
#### Maven

```xml
<dependency>
  <groupId>com.imadcn.framework</groupId>
  <artifactId>idworker</artifactId>
  <version>${latest.version}</version>
</dependency>
```

#### XML

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
    long id = generator.nextId(); // a Long format ID (not supported in 64 binary mode)
    long[] ids = generator.nextId(100_000); // batch to get Long format ID (not supported in 64 binary mode), 100K at most once
	
    String strId = generator.nextStringId(); // a String format id
    String fixedId = generator.nextFixedStringId(); //  a fixed String format id
}

```

### Configuration Reference
#### <idworker:registry /> Registry Center Configuration，如zookeeper（64进制UUID策略可不配置注册中心）

|Attribute|Type|Required|Default Value|Compatibility|Description|
|:------|:------|:------|:------|:------|:------|
|id|String|`true`| |1.0.0+|A Spring Container ID|
|server-lists|String|`true`| |1.0.0+|Zookeeper servers list<br/>Including IP/Host and Port<br/>Multiple addresses within the same cluster, separate it with `,`, such as `ip:port,ip:port`|
|namespace|String|false|idworker|1.0.0+|Zookeeper namespace|
|base-sleep-time-milliseconds|int|false|1000|1.0.0+|Default timeout(ms) of retrying to register|
|max-sleep-time-milliseconds|int|false|3000|1.0.0+|max timeout(ms) of retrying to register|
|max-retries|int|false|3|1.0.0+|max retrying times|
|session-timeout-milliseconds|int|false|60000|1.0.0+|session timeout(ms)|
|connection-timeout-milliseconds|int|false|15000|1.0.0+|connection timeout(ms)|
|digest|String|false| |1.0.0+|Zookeeper connection digest.<br/>Default empty|

#### <idworker:generator /> ID生成策略配置

|属性|类型|必填|缺省值|兼容版本|描述|
|:------|:------|:------|:------|:------|:------|
|id|String|是| |1.0.0+|Spring容器中的ID|
|strategy|String|是|snowflake|1.2.0+|ID生成[snowflake, compress_uuid]，当策略为64进制uuid时，registry-center-ref可不用配置|
|registry-center-ref|String|否| |1.0.0+|注册中心SpringBeanRef，当生成策略为snowflake时，必填|
|group|String|否|default|1.0.0+|分组名，可以为不同业务分配分组，独立注册|
|low-concurrency|Boolean|否|false|1.2.5+|低并发模式(此模式下snowflake算法sequence全局递增，不再每秒清零)|

#### <generator:snowflake /> 生成策略 : snowflake模式

|属性|类型|必填|缺省值|兼容版本|描述|
|:------|:------|:------|:------|:------|:------|
|id|String|是| |1.2.0+|Spring容器中的ID|
|registry-center-ref|String|是| |1.2.0+|注册中心SpringBeanRef|
|group|String|否|default|1.2.0+|分组名，可以为不同业务分配分组，独立注册|
|low-concurrency|Boolean|否|false|1.2.5+|低并发模式(此模式下snowflake算法sequence全局递增，不再每秒清零)|

#### <generator:compress-uuid /> 生成策略 : 64进制UUID模式

|属性|类型|必填|缺省值|兼容版本|描述|
|:------|:------|:------|:------|:------|:------|
|id|String|是| |1.2.0+|Spring容器中的ID|
