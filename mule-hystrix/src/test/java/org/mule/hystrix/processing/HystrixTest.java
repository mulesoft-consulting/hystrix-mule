package org.mule.hystrix.processing;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.processor.MessageProcessor;
import org.mule.hystrix.processing.HystrixRequestCommand;
import org.mule.transport.http.components.ResourceNotFoundException;

import com.netflix.hystrix.exception.HystrixRuntimeException;

public class HystrixTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testSuccess() {
		MessageProcessor processor = mock(MessageProcessor.class);

		MuleEvent event1 = mock(MuleEvent.class);

		MuleEvent event2 = mock(MuleEvent.class);

		try {
			when(event2.getMessageAsString()).thenReturn("success");
		} catch (MuleException e1) {
			e1.printStackTrace();
			fail("Unexpected exception 1");
		}

		try {
			when(processor.process(event1)).thenReturn(event2);
		} catch (MuleException e) {
			e.printStackTrace();
			fail("Unexpected exception 2");
		}

		try {
			assertEquals("success",
					new HystrixRequestCommand(processor, event1, "k1", "k2").execute().getMessageAsString());
		} catch (MuleException e) {
			e.printStackTrace();
			fail("Unexpected exception 3");
		}
	}

	@Test
	public void testFailure() {
		MessageProcessor processor = mock(MessageProcessor.class);

		MuleEvent event = mock(MuleEvent.class);

		try {
			doThrow(new ResourceNotFoundException(null, event, processor)).when(processor).process(event);
		} catch (MuleException e) {
			e.printStackTrace();
			fail("Unexpected exception 1");
		}

		try {
			new HystrixRequestCommand(processor, event, "k1", "k2").execute();
			fail("Should have thrown an exception");
		} catch (HystrixRuntimeException e) {
			assertEquals("k2 failed and no fallback available.", e.getMessage());
		}
	}

}
