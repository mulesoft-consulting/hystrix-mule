# hystrix-mule
Mule integration with Netflix Hystrix to be used for client thread control and implementation of a Circuit Breaker pattern.

## Overview
- Circuit Breaker Pattern
- Netflix Hystrix

## Installation
Requires Java and Maven
```
git clone https://github.com/mulesoft-consulting/hystrix-mule
cd hystrix-mule/mule-hystrix
mvn clean install
```

## Using in Mule
### How it works

### pom.xml
```xml
<dependency>
	<groupId>com.mulesoft.consulting</groupId>
	<artifactId>mule-hystrix</artifactId>
	<version>LATEST</version>
</dependency>
```

### Configuring HTTP receiver with custom connection strategy

### Mule flow example

## Dashboard connection using shared HTTP configuration
This is useful in scenarios, like deploying to CloudHub, where it is desired to reuse the existing HTTP configuration. Hystrix dashboard connection will be exposed on the same HTTP endpoint.

### Configuring Mule dashboard connector

### Mule flow example
```xml
<flow name="hystrix-metricsMainFlow" processingStrategy="synchronous">
	<http:listener config-ref="HTTP_Listener_Configuration"
		path="/hystrix/*" doc:name="HTTP" />
	<jersey:resources doc:name="Hystrix metrics JAXRS app">
		<component
			class="com.netflix.hystrix.contrib.metrics.controller.HystrixConfigSseController" />
		<component
			class="com.netflix.hystrix.contrib.metrics.controller.HystrixMetricsStreamController" />
		<component
			class="com.netflix.hystrix.contrib.metrics.controller.HystrixRequestEventsSseController" />
		<component
			class="com.netflix.hystrix.contrib.metrics.controller.HystrixUtilizationSseController" />
		<jersey:package packageName="org.mule.hystrix.jersey.writers" />
	</jersey:resources>
</flow>
```
#### Current limitation

## TODO
- Support for other HTTP-based Mule clients alongside DefaultHttpProcessor
- Support for HTTP2 streaming (when becomes available)
- Turbine autodiscovery of CloudHub workers
