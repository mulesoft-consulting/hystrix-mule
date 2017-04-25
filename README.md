# hystrix-mule
Mule integration with Netflix Hystrix to be used for client thread control and implementation of a Circuit Breaker pattern.

## Overview
In a distributed environment, inevitably some of the many service dependencies will fail. Hystrix is a library that helps you control the interactions between these distributed services by adding latency tolerance and fault tolerance logic. Hystrix does this by isolating points of access between the services, stopping cascading failures across them, and providing fallback options, all of which improve your system’s overall resiliency.

- [Circuit Breaker Pattern](https://martinfowler.com/bliki/CircuitBreaker.html)
- [Netflix Hystrix](https://github.com/Netflix/Hystrix/wiki)

## Installation
Requires Git, Java and Maven
```
git clone https://github.com/mulesoft-consulting/hystrix-mule
cd hystrix-mule/mule-hystrix
mvn clean install
```

## Using in Mule
### How it works
The implementation allows running Mule processors using Hystrix managed thread pools enabling Hystrix to control the execution of external calls and apply hte circuit breaker logic. The implementation also allows plugging Hystrix dashboards to monitor the execution of external activities, state of thread pools, circuit breaker states and so on. Further integration with other Netflix packages, like [Turbine](https://github.com/Netflix/Turbine/wiki), can allow more sofisticated management and monitoring of external client calls.

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
#### Current limitations
- At the moment, only `DefaultHttpRequester` flow processors are supported, but the strategy class can be easily extended to support more processor classes.
- The dashboards consume stats from the engine using server side event streaming `text/event-stream`, but lack of support of streaming in HTTP listener forces it to close HTTP connection after first response is delivered. Effectively, the dashboards poll the components reconnecting after each request.

## TODO
- Support for other HTTP-based Mule clients alongside DefaultHttpProcessor
- Support for HTTP2 streaming (when becomes available)
- Turbine autodiscovery of CloudHub workers
