package org.mule.hystrix.processing;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.processor.MessageProcessorChainBuilder;
import org.mule.hystrix.processing.HystrixProcessingStrategy;
import org.mule.module.http.internal.request.DefaultHttpRequester;

public class HystrixProcessingStrategyTest {

	HystrixProcessingStrategy strategy;
	MessageProcessorChainBuilder chainBuilder;

	@Before
	public void setUp() throws Exception {

		chainBuilder = mock(MessageProcessorChainBuilder.class);

		strategy = new HystrixProcessingStrategy();

		strategy.setHystrixCommandGroupKey("k1");
		strategy.setHystrixCommandKey("k2");

	}

	@Test(expected=IllegalArgumentException.class)
	public void testException() {

		List<MessageProcessor> list = new ArrayList<MessageProcessor>();

		list.add(null);

		strategy.configureProcessors(list, null, chainBuilder, null);

	}
	
	@Test
	public void test1Processor() {
		DefaultHttpRequester p2 = mock(DefaultHttpRequester.class);

		List<MessageProcessor> list = new ArrayList<MessageProcessor>();

		list.add(p2);

		strategy.configureProcessors(list, null, chainBuilder, null);

		verify(chainBuilder, times(1)).chain(any(MessageProcessor.class));

		verify(chainBuilder, times(1)).chain(any(DefaultHttpRequester.class));

	}

	@Test
	public void test3Processors() {
		DefaultHttpRequester p2 = mock(DefaultHttpRequester.class);

		MessageProcessor p1 = mock(MessageProcessor.class);

		MessageProcessor p3 = mock(MessageProcessor.class);

		List<MessageProcessor> list = new ArrayList<MessageProcessor>();

		list.add(p1);

		list.add(p2);

		list.add(p3);

		strategy.configureProcessors(list, null, chainBuilder, null);

		verify(chainBuilder, times(3)).chain(any(MessageProcessor.class));

		verify(chainBuilder, times(1)).chain(any(DefaultHttpRequester.class));

	}

}
