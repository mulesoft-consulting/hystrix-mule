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
Processors to be wired with Hystrix-managed connection pools, need to be placed into their own flows and invoked using `flow-ref`. These separate flows need to be wired with custom processing strategy `org.mule.hystrix.processing.HystrixProcessingStrategy`. The strategy needs to be initialised using 2 properties: `hystrixCommandKey` and `hystrixCommandGroupKey` and can also be controlled through [Hystrix properties](https://github.com/Netflix/Hystrix/wiki/Configuration) added to the property files. Note, that exceptions are thrown wrapped in `com.netflix.hystrix.exception.HystrixRuntimeException` and require further unpicking to determine the rootcause. The fallBack procedures are implemented through Mule exception handling and can allow custom configuration of fallBack responses for timeouts, bad requests, errors, etc.

### Mule flow example
```xml
   <http:listener-config name="HTTP_Listener_Configuration"
      host="0.0.0.0" port="8081" doc:name="HTTP Listener Configuration" />
   <http:request-config name="HTTP_Request_Configuration"
      host="google.com" port="80" doc:name="HTTP Request Configuration" />
      
      <!-- If custom processing strategies are using the same keys, 
      it will appear as a single Hystrix component  -->
   <custom-processing-strategy name="Custom_Processing_Strategy1"
      class="org.mule.hystrix.processing.HystrixProcessingStrategy"
      doc:name="Custom Processing Strategy">
      <spring:property name="hystrixCommandGroupKey" value="${hystrix.command.group.key}" />
      <spring:property name="hystrixCommandKey" value="${hystrix.command.key1}" />
   </custom-processing-strategy>
      <custom-processing-strategy name="Custom_Processing_Strategy2"
      class="org.mule.hystrix.processing.HystrixProcessingStrategy"
      doc:name="Custom Processing Strategy">
      <spring:property name="hystrixCommandGroupKey" value="${hystrix.command.group.key}" />
      <spring:property name="hystrixCommandKey" value="${hystrix.command.key2}" />
   </custom-processing-strategy>
   
   <flow name="hystrix-testFlow">
      <http:listener config-ref="HTTP_Listener_Configuration"
         path="/" doc:name="HTTP" />
      <flow-ref name="Hystrix-flow1" doc:name="Hystrix-flow1" />
      <flow-ref name="Hystrix-flow2" doc:name="Hystrix-flow2" />
   </flow>
   
   <!-- Activities in these flows are performed using Hystrix thread pool -->
   <flow name="Hystrix-flow1" processingStrategy="Custom_Processing_Strategy1">
      <http:request config-ref="HTTP_Request_Configuration"
         path="/" method="GET" followRedirects="false" parseResponse="false"
         doc:name="HTTP" />
      <exception-strategy ref="Choice"
         doc:name="Reference Exception Strategy" />
   </flow>
   <flow name="Hystrix-flow2" processingStrategy="Custom_Processing_Strategy2">
      <http:request config-ref="HTTP_Request_Configuration"
         path="/" method="GET" followRedirects="false" parseResponse="false"
         doc:name="Copy_of_HTTP" />
      <exception-strategy ref="Choice"
         doc:name="Copy_of_Reference Exception Strategy" />
   </flow>
   
   <!-- Exceptions thrown in flows are wrapped in com.netflix.hystrix.exception.HystrixRuntimeException
   Refer to Hystrix Wiki for more info -->
   <choice-exception-strategy name="Choice">
      <catch-exception-strategy
         when="#[exception.causedBy(com.netflix.hystrix.exception.HystrixRuntimeException) &amp;&amp; exception.cause.cause.toString() == &quot;java.util.concurrent.TimeoutException&quot;]"
         enableNotifications="false" doc:name="Timeout">
         <set-payload value="{&quot;result&quot;: &quot;timeout&quot;}"
            doc:name="Set Payload" />
         <set-property propertyName="http.result" value="200"
            doc:name="Property" />
      </catch-exception-strategy>
      <catch-exception-strategy
         enableNotifications="false" doc:name="Other error">
         <set-payload value="{&quot;result&quot;: &quot;something else&quot;}"
            doc:name="Set Payload" />
         <set-property propertyName="http.result" value="200"
            doc:name="Property" />
      </catch-exception-strategy>
   </choice-exception-strategy>
```

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
