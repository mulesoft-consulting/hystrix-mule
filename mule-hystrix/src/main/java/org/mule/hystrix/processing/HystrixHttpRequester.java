package org.mule.hystrix.processing;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.construct.FlowConstruct;
import org.mule.api.debug.FieldDebugInfo;
import org.mule.api.exception.MessagingExceptionHandler;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.module.http.internal.request.DefaultHttpRequester;
import org.mule.module.http.internal.request.DefaultHttpRequesterConfig;
import org.mule.module.http.internal.request.HttpRequesterRequestBuilder;
import org.mule.module.http.internal.request.ResponseValidator;

/*
 * Adapter for DefaultHttpRequester wrapping the process() method in a Hystrix command and 
 * delegating other methods to the default implementation.
 */

public class HystrixHttpRequester extends DefaultHttpRequester {

	private DefaultHttpRequester processor;
	private String hystrixCommandGroupKey;
	private String hystrixCommandKey;

	public HystrixHttpRequester(DefaultHttpRequester processor, String hystrixCommandGroupKey,
			String hystrixCommandKey) {
		this.processor = processor;
		this.hystrixCommandGroupKey = hystrixCommandGroupKey;
		this.hystrixCommandKey = hystrixCommandKey;
	}

	@Override
	public MuleEvent process(MuleEvent event) throws MuleException {
		// Injection of Hystrix sync call wrapping the original process()
		// method.
		return new HystrixRequestCommand(processor, event, hystrixCommandGroupKey, hystrixCommandKey).execute();
	}

	@Override
	public void initialise() throws InitialisationException {
		processor.initialise();
	}

	@Override
	public String getHost() {
		return processor.getHost();
	}

	@Override
	public void setHost(String host) {
		processor.setHost(host);
	}

	@Override
	public String getPort() {
		return processor.getPort();
	}

	@Override
	public void setPort(String port) {
		processor.setPort(port);
	}

	@Override
	public String getPath() {
		return processor.getPath();
	}

	@Override
	public void setPath(String path) {
		processor.setPath(path);
	}

	@Override
	public String getUrl() {
		return processor.getUrl();
	}

	@Override
	public void setUrl(String url) {
		processor.setUrl(url);
	}

	@Override
	public HttpRequesterRequestBuilder getRequestBuilder() {
		return processor.getRequestBuilder();
	}

	@Override
	public void setRequestBuilder(HttpRequesterRequestBuilder requestBuilder) {
		processor.setRequestBuilder(requestBuilder);
	}

	@Override
	public String getMethod() {
		return processor.getMethod();
	}

	@Override
	public void setMethod(String method) {
		processor.setMethod(method);
	}

	@Override
	public DefaultHttpRequesterConfig getConfig() {
		return processor.getConfig();
	}

	@Override
	public void setConfig(DefaultHttpRequesterConfig requestConfig) {
		processor.setConfig(requestConfig);
	}

	@Override
	public void setFollowRedirects(String followsRedirects) {
		processor.setFollowRedirects(followsRedirects);
	}

	@Override
	public void setRequestStreamingMode(String requestStreamingMode) {
		processor.setRequestStreamingMode(requestStreamingMode);
	}

	@Override
	public ResponseValidator getResponseValidator() {
		return processor.getResponseValidator();
	}

	@Override
	public void setResponseValidator(ResponseValidator responseValidator) {
		processor.setResponseValidator(responseValidator);
	}

	@Override
	public void setSendBodyMode(String sendBodyMode) {
		processor.setSendBodyMode(sendBodyMode);
	}

	@Override
	public String getSource() {
		return processor.getSource();
	}

	@Override
	public void setSource(String source) {
		processor.setSource(source);
	}

	@Override
	public String getTarget() {
		return processor.getTarget();
	}

	@Override
	public void setTarget(String target) {

		processor.setTarget(target);
	}

	@Override
	public void setParseResponse(String parseResponse) {

		processor.setParseResponse(parseResponse);
	}

	@Override
	public void setResponseTimeout(String responseTimeout) {

		processor.setResponseTimeout(responseTimeout);
	}

	@Override
	public void setMuleContext(MuleContext muleContext) {

		processor.setMuleContext(muleContext);
	}

	@Override
	public void setFlowConstruct(FlowConstruct flowConstruct) {

		processor.setFlowConstruct(flowConstruct);
	}

	@Override
	public List<FieldDebugInfo<?>> getDebugInfo(MuleEvent event) {

		return processor.getDebugInfo(event);
	}

	@Override
	public void setMessagingExceptionHandler(MessagingExceptionHandler messagingExceptionHandler) {

		processor.setMessagingExceptionHandler(messagingExceptionHandler);
	}

	@Override
	public synchronized void setAnnotations(Map<QName, Object> newAnnotations) {

		processor.setAnnotations(newAnnotations);
	}

	@Override
	public int hashCode() {

		return processor.hashCode();
	}

	@Override
	public boolean equals(Object obj) {

		return processor.equals(obj);
	}

	@Override
	public String toString() {

		return processor.toString();
	}

}