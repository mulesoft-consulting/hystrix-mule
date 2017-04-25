package org.mule.hystrix.jersey.writers;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.hystrix.contrib.metrics.HystrixStream;

import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;

@Provider
public class HystrixStreamingOutputProvider implements MessageBodyWriter<HystrixStream> {

	private static final Logger LOGGER = LoggerFactory.getLogger(HystrixStreamingOutputProvider.class);
	private OutputStream localEntity;

	@Override
	public long getSize(HystrixStream arg0, Class<?> arg1, Type arg2, Annotation[] arg3, MediaType arg4) {
		return -1;
	}

	@Override
	public boolean isWriteable(Class<?> t, Type arg1, Annotation[] arg2, MediaType arg3) {
		return HystrixStream.class.isAssignableFrom(t);
	}

	@Override
	public void writeTo(HystrixStream hystrixStream, Class<?> t, Type gt, Annotation[] as, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, final OutputStream entity) throws IOException {

		Subscription sampleSubscription = null;
		final AtomicBoolean moreDataWillBeSent = new AtomicBoolean(true);
		localEntity = new ByteArrayOutputStream();

		try {

			sampleSubscription = hystrixStream.getSampleStream().observeOn(Schedulers.io())
					.subscribe(new Subscriber<String>() {
						@Override
						public void onCompleted() {
							LOGGER.error(
									"HystrixSampleSseServlet: ({}) received unexpected OnCompleted from sample stream",
									getClass().getSimpleName());
							moreDataWillBeSent.set(false);
						}

						@Override
						public void onError(Throwable e) {
							moreDataWillBeSent.set(false);
						}

						@Override
						public void onNext(String sampleDataAsString) {
							if (sampleDataAsString != null) {
								try {
									localEntity.write(("data: " + sampleDataAsString + "\n\n").getBytes());
									localEntity.flush();
								} catch (IOException ioe) {
									moreDataWillBeSent.set(false);
								}
							}
						}
					});

			while (moreDataWillBeSent.get()) {

				/* This is where we are hacking steam response designed to work with HTTP2 to respond only once
				 * to work with Mule HTTP1.1 receiver. This effectively reverses the streaming push into polling.
				 * We are also using a local stream to avoid different threads writing into the same MuleMessage.
				 * */
				if (localEntity.toString().length() > 0) {

					entity.write(localEntity.toString().getBytes());
					entity.flush();

					localEntity.flush();

					moreDataWillBeSent.set(false);
				}

				try {
					Thread.sleep(hystrixStream.getPausePollerThreadDelayInMs());
				} catch (InterruptedException e) {
					moreDataWillBeSent.set(false);
				}
			}
		} finally {
			
			entity.close();
			localEntity.close();
			
			hystrixStream.getConcurrentConnections().decrementAndGet();
			if (sampleSubscription != null && !sampleSubscription.isUnsubscribed()) {
				sampleSubscription.unsubscribe();
			}
		}

	}

}