package org.mule.hystrix.processing;

import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.processor.MessageProcessor;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;

public class HystrixRequestCommand extends HystrixCommand<MuleEvent> {

	private MessageProcessor processor;
	private MuleEvent event;

	public HystrixRequestCommand(MessageProcessor processor, MuleEvent event, String hystrixCommandGroupKey,
			String hystrixCommandKey) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(hystrixCommandGroupKey))
				.andCommandKey(HystrixCommandKey.Factory.asKey(hystrixCommandKey)));
		this.processor = processor;
		this.event = event;
	}

	@Override
	protected MuleEvent run() {

		MuleEvent response = null;

		try {
			response = processor.process(event);
		} catch (MuleException e) {
			throw new RuntimeException(e);
		}

		return response;
	}

}