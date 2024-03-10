package org.jmedina.jtetris.figures.figure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jmedina.jtetris.figures.enumeration.FiguraEnumeration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

/**
 * @author Jorge Medina
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class FigureTest {

	private final Logger logger = LogManager.getLogger(this.getClass());

	private static final int CAJA_HASH_CODE = -916896875;
	private static final int ELE_HASH_CODE = 305087440;
	private static final int BOX_HASH_CODE = 3481;
	private static final int CAJA_WITHOUT_BOXES_HASH_CODE = 3583;

	@Test
	@Order(1)
	@DisplayName("Test for Caja")
	void testCaja() {
		Caja caja = new Caja();
		assertEquals(FiguraEnumeration.CAJA, caja.getType());
		caja.setType(FiguraEnumeration.CAJA);
		assertEquals(FiguraEnumeration.CAJA, caja.getType());
	}

	@Test
	@Order(2)
	@DisplayName("Test for Ele")
	void testEle() {
		Ele ele = new Ele();
		assertEquals(FiguraEnumeration.ELE, ele.getType());
		ele.setType(FiguraEnumeration.ELE);
		assertEquals(FiguraEnumeration.ELE, ele.getType());
	}

	@Test
	@Order(3)
	@DisplayName("Test for Caja Equals")
	void testCajaEquals() {
		Caja caja1 = new Caja();
		Caja caja2 = new Caja();
		Caja caja3 = new CajaTest();
		Caja caja4 = new CajaTest2();
		Ele ele = new Ele();
		assertEquals(caja1, caja1);
		assertEquals(caja1, caja2);
		assertNotEquals(caja1, caja3);
		assertNotEquals(caja1, ele);
		assertEquals(false, Objects.equals(caja1, null));
		assertTrue(caja1.canEqual(caja1));
		assertTrue(caja1.canEqual(caja2));
		assertFalse(caja3.canEqual(caja1));
		assertFalse(caja1.canEqual(new String()));
		this.logger.debug("===> caja1.hashCode() = {}", caja1.hashCode());
		this.logger.debug("===> caja2.hashCode() = {}", caja2.hashCode());
		this.logger.debug("===> ele.hashCode() = {}", ele.hashCode());
		assertEquals(CAJA_HASH_CODE, caja1.hashCode());
		assertEquals(caja1.hashCode(), caja2.hashCode());
		assertNotEquals(caja1.hashCode(), ele.hashCode());
		caja4.setBoxes(null);
		assertNotEquals(caja1, caja4);
		caja1.setBoxes(null);
		assertEquals(caja1, caja4);
		caja4.setBoxes(new ArrayList<>());
		assertNotEquals(caja1, caja4);
		this.logger.debug("===> caja1.hashCode() = {}", caja1.hashCode());
		assertEquals(CAJA_WITHOUT_BOXES_HASH_CODE, caja1.hashCode());
		ele.setBoxes(null);
		assertNotEquals(caja1, ele); // without boxes.
	}

	@Test
	@Order(4)
	@DisplayName("Test for Ele Equals")
	void testEleEquals() {
		Ele ele1 = new Ele();
		Ele ele2 = new Ele();
		Ele ele3 = new EleTest();
		Caja caja = new Caja();
		assertEquals(ele1, ele1);
		assertEquals(ele1, ele2);
		assertNotEquals(ele1, ele3);
		assertNotEquals(ele1, caja);
		assertNotEquals(null, ele1);
		assertTrue(ele1.canEqual(ele1));
		assertTrue(ele1.canEqual(ele2));
		assertFalse(ele3.canEqual(ele1));
		assertFalse(ele1.canEqual(new String()));
		this.logger.debug("===> ele1.hashCode() = {}", ele1.hashCode());
		this.logger.debug("===> ele2.hashCode() = {}", ele2.hashCode());
		this.logger.debug("===> caja.hashCode() = {}", caja.hashCode());
		assertEquals(ELE_HASH_CODE, ele1.hashCode());
		assertEquals(ele1.hashCode(), ele2.hashCode());
		assertNotEquals(ele1.hashCode(), caja.hashCode());
	}

	@Test
	@Order(5)
	@DisplayName("Test for Box Equals")
	void testCajaHashCode() {
		Box box1 = new Box();
		Box box2 = new Box();
		Box box3 = new BoxTest();
		Box box4 = new BoxTest2();
		Box box5 = new BoxTest2();
		Caja caja = new Caja();
		assertEquals(box1, box1);
		assertEquals(box1, box2);
		assertNotEquals(box1, box3);
		assertEquals(box1, box4);
		box4.setY(-1);
		assertNotEquals(box1, box4);
		box5.setX(-1);
		assertNotEquals(box1, box5);
		assertNotEquals(box1, caja);
		assertNotEquals(null, box1);
		assertTrue(box1.canEqual(box1));
		assertTrue(box1.canEqual(box2));
		assertFalse(box3.canEqual(box1));
		assertFalse(box1.canEqual(new String()));
		this.logger.debug("===> box1.hashCode() = {}", box1.hashCode());
		this.logger.debug("===> box2.hashCode() = {}", box2.hashCode());
		this.logger.debug("===> caja.hashCode() = {}", caja.hashCode());
		assertEquals(BOX_HASH_CODE, box1.hashCode());
		assertEquals(box1.hashCode(), box2.hashCode());
		assertNotEquals(box1.hashCode(), caja.hashCode());
	}

	class CajaTest extends Caja {
		@Override
		protected boolean canEqual(Object other) {
			return other instanceof CajaTest;
		}
	}

	class CajaTest2 extends Caja {
		@Override
		protected boolean canEqual(Object other) {
			return other instanceof Caja;
		}
	}

	class EleTest extends Ele {
		@Override
		protected boolean canEqual(Object other) {
			return other instanceof EleTest;
		}
	}

	class BoxTest extends Box {
		@Override
		protected boolean canEqual(Object other) {
			return other instanceof BoxTest;
		}
	}

	class BoxTest2 extends Box {
		@Override
		protected boolean canEqual(Object other) {
			return other instanceof Box;
		}
	}
}