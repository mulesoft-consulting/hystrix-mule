package org.mule.hystrix.processing;

import java.util.List;

import org.mule.api.MuleContext;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.processor.MessageProcessorBuilder;
import org.mule.api.processor.MessageProcessorChainBuilder;
import org.mule.api.processor.ProcessingStrategy;
import org.mule.module.http.internal.request.DefaultHttpRequester;

/* 
 * This is modified synchronous Mule processing strategy wrapping DefaultHttpRequester into 
 * Hystrix command. If other types of activities, other than HTTP, are required to be run on
 * Hystrix threads, the if statement can be expanded adding another Requester class wrapping the 
 * original requester method and invoking the process() method as a command. 
*/
public class HystrixProcessingStrategy implements ProcessingStrategy {

	private String hystrixCommandGroupKey;
	private String hystrixCommandKey;

	@Override
	public void configureProcessors(List<MessageProcessor> processors,
			org.mule.api.processor.StageNameSource nameSource, MessageProcessorChainBuilder chainBuilder,
			MuleContext muleContext) {

		for (Object processor : processors) {
			
			// Handling only DefaultHttpRequester at the moment. More can be added.
			if (processor instanceof DefaultHttpRequester) {
				chainBuilder.chain((DefaultHttpRequester) new HystrixHttpRequester((DefaultHttpRequester) processor,
						hystrixCommandGroupKey, hystrixCommandKey));
				
			} else if (processor instanceof MessageProcessor) {
				chainBuilder.chain((MessageProcessor) processor);
			} else if (processor instanceof MessageProcessorBuilder) {
				chainBuilder.chain((MessageProcessorBuilder) processor);
			} else {
				throw new IllegalArgumentException(
						"MessageProcessorBuilder should only have MessageProcessor's or MessageProcessorBuilder's configured");
			}
		}
	}

	public void setHystrixCommandGroupKey(String hystrixCommandGroupKey) {
		this.hystrixCommandGroupKey = hystrixCommandGroupKey;
	}

	public void setHystrixCommandKey(String hystrixCommandKey) {
		this.hystrixCommandKey = hystrixCommandKey;
	}

}