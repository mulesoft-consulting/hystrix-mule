# hystrix-mule
Mule integration with Netflix Hystrix to be used for client thread control and implementation of a Circuit Breaker pattern.

## Overview
- Circuit Breaker Pattern
- Netflix Hystrix

## Installation

## Using in Mule
### How it works

### Configuring HTTP receiver with custom connection strategy

### Mule flow example

## Dashboard connection using shared HTTP configuration
This is useful in scenarios, like deploying to CloudHub, where it is desired to reuse the existing HTTP configuration. Hystrix dashboard connection will be exposed on the same HTTP endpoint.

### Configuring Mule dashboard connector

### Mule flow example
```xml
<?xml version="1.0" encoding="UTF-8"?>
<mule xmlns:dw="http://www.mulesoft.org/schema/mule/ee/dw"
	xmlns:vm="http://www.mulesoft.org/schema/mule/vm" xmlns:jetty="http://www.mulesoft.org/schema/mule/jetty"
	xmlns:tracking="http://www.mulesoft.org/schema/mule/ee/tracking"
	xmlns:core="http://www.mulesoft.org/schema/mule/core" xmlns:jersey="http://www.mulesoft.org/schema/mule/jersey"
	xmlns:servlet="http://www.mulesoft.org/schema/mule/servlet" xmlns:http="http://www.mulesoft.org/schema/mule/http"
	xmlns="http://www.mulesoft.org/schema/mule/core" xmlns:doc="http://www.mulesoft.org/schema/mule/documentation"
	xmlns:spring="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.mulesoft.org/schema/mule/core http://www.mulesoft.org/schema/mule/core/current/mule.xsd
http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-current.xsd
http://www.mulesoft.org/schema/mule/servlet http://www.mulesoft.org/schema/mule/servlet/current/mule-servlet.xsd
http://www.mulesoft.org/schema/mule/http http://www.mulesoft.org/schema/mule/http/current/mule-http.xsd
http://www.mulesoft.org/schema/mule/jersey http://www.mulesoft.org/schema/mule/jersey/current/mule-jersey.xsd
http://www.mulesoft.org/schema/mule/ee/tracking http://www.mulesoft.org/schema/mule/ee/tracking/current/mule-tracking-ee.xsd
http://www.mulesoft.org/schema/mule/ee/dw http://www.mulesoft.org/schema/mule/ee/dw/current/dw.xsd
http://www.mulesoft.org/schema/mule/vm http://www.mulesoft.org/schema/mule/vm/current/mule-vm.xsd
http://www.mulesoft.org/schema/mule/jetty http://www.mulesoft.org/schema/mule/jetty/current/mule-jetty.xsd">
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
			<!-- <jersey:package packageName="com.netflix.hystrix.contrib.metrics"/> -->
			<jersey:package packageName="org.mule.hystrix.jersey.writers" />
		</jersey:resources>
	</flow>
</mule>
```
#### Current limitation

## TODO
- Support for other HTTP-based Mule clients alongside DefaultHttpProcessor
- Support for HTTP2 streaming (when becomes available)
- Turbine autodiscovery of CloudHub workers
