package org.jmedina.jtetris.figures.util;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.springframework.stereotype.Service;

/**
 * @author Jorge Medina
 *
 */
@Service
public class AssertUtilTesting {

	static final String JSON_CAJA = "{\"boxes\":[{\"x\":0.0,\"y\":0.0},{\"x\":0.0,\"y\":20.0},{\"x\":20.0,\"y\":0.0},{\"x\":20.0,\"y\":20.0}]}";
	static final String JSON_ELE = "{\"boxes\":[{\"x\":0.0,\"y\":0.0},{\"x\":0.0,\"y\":20.0},{\"x\":0.0,\"y\":40.0},{\"x\":0.0,\"y\":60.0}]}";

	public void awaitOneSecond() {
		Awaitility.await().pollDelay(1, TimeUnit.SECONDS).until(() -> true);
	}

	public void assertLatch(CountDownLatch latch) throws InterruptedException {
		assertTrue(latch.await(10, TimeUnit.SECONDS));
	}

	public void assertMessageCaja(String message) {
		assertEquals(JSON_CAJA, message);
	}

	public void assertMessageEle(String message) {
		assertEquals(JSON_ELE, message);
	}

	public void assertMessageFigura(String message) {
		assertTrue(Objects.equals(JSON_CAJA, message) || Objects.equals(JSON_ELE, message));
	}

}