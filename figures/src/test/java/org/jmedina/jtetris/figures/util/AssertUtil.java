package org.jmedina.jtetris.figures.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awaitility.Awaitility;
import org.jmedina.jtetris.figures.listener.MessageListenerTesting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author Jorge Medina
 *
 */
@Service
public class AssertUtil {

	private final Logger logger = LogManager.getLogger(this.getClass());

	@Autowired
	@Qualifier("messageListener")
	private MessageListenerTesting listener;

	static final String JSON_CAJA = "{\"boxes\":[{\"x\":0.0,\"y\":0.0},{\"x\":0.0,\"y\":20.0},{\"x\":20.0,\"y\":0.0},{\"x\":20.0,\"y\":20.0}]}";
	static final String JSON_ELE = "{\"boxes\":[{\"x\":0.0,\"y\":0.0},{\"x\":0.0,\"y\":20.0},{\"x\":0.0,\"y\":40.0},{\"x\":0.0,\"y\":60.0}]}";

	public void resetMessageListener() {
		this.listener.setMessage(null);
		this.listener.resetLatch();
	}

	public void awaitOneSecond() {
		Awaitility.await().pollDelay(1, TimeUnit.SECONDS).until(() -> true);
	}

	public void assertMessageListenerLatch() throws InterruptedException {
		this.logger.debug("==> UtilTest.assertMessageListenerLatch() = {}", this.listener);
		assertTrue(this.listener.getLatch().await(10, TimeUnit.SECONDS));
	}

	public void assertMessageCaja() {
		this.logger.debug("==> UtilTest.assertMessageCaja() = {}", this.listener);
		this.logger.debug("==> message ==> {}", this.listener.getMessage());
		assertEquals(JSON_CAJA, this.listener.getMessage());
	}

	public void assertMessageEle() {
		this.logger.debug("==> UtilTest.assertMessageEle() = {}", this.listener);
		this.logger.debug("==> message ==> {}", this.listener.getMessage());
		assertEquals(JSON_ELE, this.listener.getMessage());
	}

	public void assertMessageFigure() {
		this.logger.debug("==> UtilTest.assertMessageFigure() = {}", this.listener);
		this.logger.debug("==> message ==> {}", this.listener.getMessage());
		String message = this.listener.getMessage();
		assertTrue(Objects.equals(JSON_CAJA, message) || Objects.equals(JSON_ELE, message));
	}

}