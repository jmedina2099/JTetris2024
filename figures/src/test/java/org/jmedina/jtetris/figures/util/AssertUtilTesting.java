package org.jmedina.jtetris.figures.util;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;

/**
 * @author Jorge Medina
 *
 */
public class AssertUtilTesting {

	static final String JSON_CAJA = "{\"boxes\":[{\"x\":0.0,\"y\":0.0},{\"x\":0.0,\"y\":20.0},{\"x\":20.0,\"y\":0.0},{\"x\":20.0,\"y\":20.0}]}";
	static final String JSON_ELE = "{\"boxes\":[{\"x\":0.0,\"y\":0.0},{\"x\":0.0,\"y\":20.0},{\"x\":0.0,\"y\":40.0},{\"x\":0.0,\"y\":60.0}]}";

	public static void awaitOneSecond() {
		Awaitility.await().pollDelay(1, TimeUnit.SECONDS).until(() -> true);
	}

	public static void assertLatch(CountDownLatch latch) throws InterruptedException {
		assertTrue(latch.await(10, TimeUnit.SECONDS));
	}

	public static void assertMessageCaja(String message) {
		assertEquals(JSON_CAJA, message);
	}

	public static void assertMessageEle(String message) {
		assertEquals(JSON_ELE, message);
	}

	public static void assertMessageFigura(String message) {
		assertTrue(Objects.equals(JSON_CAJA, message) || Objects.equals(JSON_ELE, message));
	}

}