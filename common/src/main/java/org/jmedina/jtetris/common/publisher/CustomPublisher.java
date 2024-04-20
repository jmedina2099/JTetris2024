package org.jmedina.jtetris.common.publisher;

import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.Logger;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;

import reactor.core.Disposable;

/**
 * @author Jorge Medina
 *
 */
public class CustomPublisher<T> implements Publisher<T>, Subscription {

	protected final Logger logger;
	private boolean isRunning = false;
	protected Disposable disposable;
	private Subscriber<? super T> subscriber;
	private AtomicLong requestCounter = new AtomicLong();
	private Queue<T> queue = new ConcurrentLinkedDeque<T>();

	public CustomPublisher(Logger logger) {
		this.logger = logger;
	}

	@Override
	public void subscribe(Subscriber<? super T> subscriber) {
		this.logger.debug("===> CustomPublisher.subscribe()");
		this.isRunning = true;
		this.subscriber = subscriber;
		this.subscriber.onSubscribe(this);
	}

	protected void addToQueue(T op) {
		this.logger.debug("===> CustomPublisher.addToQueue() = " + op);
		this.queue.add(op);
		tryToSend(true);
	}

	public boolean stop() {
		this.logger.debug("===> CustomPublisher.stop()");
		this.isRunning = false;
		this.requestCounter.set(0L);
		this.queue.clear();
		if (Objects.nonNull(this.disposable)) {
			this.disposable.dispose();
		}
		if (Objects.nonNull(this.subscriber)) {
			this.subscriber.onComplete();
		}
		return true;
	}

	@Override
	public void request(long n) {
		this.logger.info("=====> CustomPublisher.request = " + n);
		this.requestCounter.addAndGet(n);
		tryToSend(false);
	}

	@Override
	public void cancel() {
		this.logger.debug("=====> CustomPublisher.cancel()");
		stop();
	}

	private void tryToSend(boolean isOnlyOne) {
		this.logger.debug("===> CustomPublisher.tryToSend({})", isOnlyOne);
		try {
			Optional<T> element;
			if (this.isRunning) {
				this.logger.info("===> START: counter={}, queue={}", this.requestCounter.get(), this.queue.size());
				do {
					element = checkConditionForSendAndGet();
					element.ifPresent(op -> doOnNext(op));
				} while (!isOnlyOne && element.isPresent());
				this.logger.info("===> END:   counter={}, queue={}", this.requestCounter.get(), this.queue.size());
			}
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return;
		}
		this.logger.debug("===> END - CustomPublisher.tryToSend()");
	}

	private synchronized Optional<T> checkConditionForSendAndGet() {
		if (this.requestCounter.get() > 0 && this.queue.size() > 0) {
			this.requestCounter.decrementAndGet();
			return Optional.of(this.queue.remove());
		}
		return Optional.<T>empty();
	}

	private void doOnNext(T op) {
		this.logger.debug("===> CustomPublisher.doOnNext() = " + op);
		try {
			this.subscriber.onNext(op);
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
		}
	}

	@EventListener({ ContextClosedEvent.class })
	protected void onApplicationEvent(ContextClosedEvent event) {
		this.logger.debug("===> CustomPublisher.onApplicationEvent() - ContextClosedEvent");
		stop();
	}

}
