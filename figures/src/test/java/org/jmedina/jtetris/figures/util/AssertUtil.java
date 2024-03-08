package org.jmedina.jtetris.figures.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.jmedina.jtetris.figures.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * @author Jorge Medina
 *
 */
@RequiredArgsConstructor
@Service
public class AssertUtil {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final MessageListener messageListenerTest;

	public void assertMessageListener(String message) {
		this.logger.debug("==> UtilTest.assertMessageListener() = {}",message);
		Awaitility.await().atMost(1, TimeUnit.SECONDS).until(hasMessage(message));
		assertEquals(message,this.messageListenerTest.getMessage());
	}

	public Callable<Boolean> hasMessage(String message) {
		return () -> message.equals(this.messageListenerTest.getMessage());
	}

}
