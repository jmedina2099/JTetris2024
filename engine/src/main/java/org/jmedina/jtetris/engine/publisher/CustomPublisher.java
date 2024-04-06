package org.jmedina.jtetris.engine.publisher;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;

/**
 * @author Jorge Medina
 *
 */
public class CustomPublisher<T> implements Publisher<T>, Subscription {

	private boolean isRunning = false;
	private Subscriber<? super T> subscriber;
	private AtomicInteger requestCounter;
	private Queue<T> queue = new ConcurrentLinkedDeque<T>();
	protected Logger logger;

	@Override
	public void subscribe(Subscriber<? super T> subscriber) {
		this.logger.debug("===> CustomPublisher.subscribe()");
		this.isRunning = true;
		this.requestCounter = new AtomicInteger();
		this.subscriber = subscriber;
		this.subscriber.onSubscribe(this);
	}

	/**
	 * We need this method synchronized, because undertow and wildfly use threads.
	 */
	protected synchronized void addToQueue(T op) {
		this.logger.debug("===> CustomPublisher.addToQueue() = " + op);
		this.queue.add(op);
		tryToSendOne();
	}

	public boolean stop() {
		this.logger.debug("===> CustomPublisher.stop()");
		this.isRunning = false;
		if (Objects.nonNull(this.subscriber)) {
			this.subscriber.onComplete();
		}
		return true;
	}

	@Override
	public void request(long n) {
		this.logger.info("=====> CustomPublisher.request = " + n);
		this.requestCounter.addAndGet((int) n);
		tryToSendOne();
	}

	@Override
	public void cancel() {
		this.logger.debug("=====> CustomPublisher.cancel()");
		stop();
	}

	private synchronized void tryToSendOne() {
		this.logger.debug("===> CustomPublisher.tryToSend()");
		try {
			if (this.isRunning) {
				this.logger.debug("===> START: counter={}, queue={}", this.requestCounter.get(), this.queue.size());
				if (this.requestCounter.get() > 0 && this.queue.size() > 0) {
					doOnNext(this.queue.remove());
					this.requestCounter.decrementAndGet();
				}
				this.logger.info("===> END:   counter={}, queue={}", this.requestCounter.get(), this.queue.size());
			}
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return;
		}
		this.logger.debug("===> END - CustomPublisher.tryToSend()");
	}

	private synchronized void doOnNext(T op) {
		this.logger.debug("===> CustomPublisher.doOnNext() = " + op);
		try {
			this.subscriber.onNext(op);
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
		}
	}

	@EventListener({ ContextClosedEvent.class })
	private void onApplicationEvent(ContextClosedEvent event) {
		this.logger.debug("===> CustomPublisher.onApplicationEvent() - ContextClosedEvent");
		stop();
	}

}
