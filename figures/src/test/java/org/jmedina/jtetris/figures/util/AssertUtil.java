package org.jmedina.jtetris.figures.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
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

	public void assertMessageListener() {
		this.logger.debug("==> UtilTest.assertMessageListener()");
		Awaitility.await().atMost(1, TimeUnit.SECONDS).until(hasMessage());
		assertTrue(StringUtils.isNotBlank(this.messageListenerTest.getMessage()));
	}

	public void assertFalseMessageListener() {
		this.logger.debug("==> UtilTest.assertFalseMessageListener()");
		Awaitility.await().atMost(1, TimeUnit.SECONDS).until(hasNotMessage());
		assertTrue(StringUtils.isBlank(this.messageListenerTest.getMessage()));
	}

	public Callable<Boolean> hasMessage() {
		return () -> StringUtils.isNotBlank(this.messageListenerTest.getMessage());
	}

	public Callable<Boolean> hasNotMessage() {
		return () -> StringUtils.isBlank(this.messageListenerTest.getMessage());
	}
}
