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
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

/**
 * @author Jorge Medina
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
class FigureDBTest {

	private final Logger logger = LogManager.getLogger(this.getClass());

	private static final int CAJA_HASH_CODE = -916896875;
	private static final int ELE_HASH_CODE = 305087440;
	private static final int BOX_HASH_CODE = -478671462;
	private static final int CAJA_WITHOUT_BOXES_HASH_CODE = 3583;
	private static final int ELE_WITHOUT_BOXES_HASH_CODE = 3642;
	private static final int BOX_WITH_COORDS_0 = 3481;

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
		this.logger.debug("===> caja1.hashCode() = {} {}", caja1.hashCode(), caja1);
		this.logger.debug("===> caja2.hashCode() = {} {}", caja2.hashCode(), caja2);
		this.logger.debug("===> caja3.hashCode() = {} {}", caja3.hashCode(), caja3);
		this.logger.debug("===> caja4.hashCode() = {} {}", caja4.hashCode(), caja4);
		assertEquals(CAJA_HASH_CODE, caja1.hashCode());
		assertEquals(caja1.hashCode(), caja2.hashCode());
		assertEquals(caja1.hashCode(), caja3.hashCode());
		assertEquals(caja1.hashCode(), caja4.hashCode());
		assertNotEquals(caja1.hashCode(), ele.hashCode());
		caja4.setBoxes(null);
		assertNotEquals(caja1, caja4);
		caja1.setBoxes(null);
		assertEquals(caja1, caja4);
		caja4.setBoxes(new ArrayList<>());
		assertNotEquals(caja1, caja4);
		caja1.setBoxes(null);
		caja2.setBoxes(null);
		caja3.setBoxes(null);
		caja4.setBoxes(null);
		this.logger.debug("===> caja1.hashCode() with boxes null = {} {}", caja1.hashCode(), caja1);
		this.logger.debug("===> caja2.hashCode() with boxes null = {} {}", caja2.hashCode(), caja2);
		this.logger.debug("===> caja3.hashCode() with boxes null = {} {}", caja3.hashCode(), caja3);
		this.logger.debug("===> caja4.hashCode() with boxes null = {} {}", caja4.hashCode(), caja4);
		assertEquals(CAJA_WITHOUT_BOXES_HASH_CODE, caja1.hashCode());
		assertEquals(caja1.hashCode(), caja2.hashCode());
		assertEquals(caja1.hashCode(), caja3.hashCode());
		assertEquals(caja1.hashCode(), caja4.hashCode());
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
		Ele ele4 = new EleTest2();
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
		this.logger.debug("===> ele1.hashCode() = {} {}", ele1.hashCode(), ele1);
		this.logger.debug("===> ele2.hashCode() = {} {}", ele2.hashCode(), ele2);
		this.logger.debug("===> ele3.hashCode() = {} {}", ele3.hashCode(), ele3);
		this.logger.debug("===> ele4.hashCode() = {} {}", ele4.hashCode(), ele4);
		assertEquals(ELE_HASH_CODE, ele1.hashCode());
		assertEquals(ele1.hashCode(), ele2.hashCode());
		assertEquals(ele1.hashCode(), ele3.hashCode());
		assertEquals(ele1.hashCode(), ele4.hashCode());
		assertNotEquals(ele1.hashCode(), caja.hashCode());
		ele1.setBoxes(null);
		ele2.setBoxes(null);
		ele3.setBoxes(null);
		ele4.setBoxes(null);
		assertEquals(ELE_WITHOUT_BOXES_HASH_CODE, ele1.hashCode());
		assertEquals(ele1.hashCode(), ele2.hashCode());
		assertEquals(ele1.hashCode(), ele3.hashCode());
		assertEquals(ele1.hashCode(), ele4.hashCode());
		this.logger.debug("===> ele1.hashCode() with boxes null = {} {}", ele1.hashCode(), ele1);
		this.logger.debug("===> ele2.hashCode() with boxes null = {} {}", ele2.hashCode(), ele2);
		this.logger.debug("===> ele3.hashCode() with boxes null = {} {}", ele3.hashCode(), ele3);
		this.logger.debug("===> ele4.hashCode() with boxes null = {} {}", ele4.hashCode(), ele4);
	}

	@Test
	@Order(5)
	@DisplayName("Test for Box Equals")
	void testCajaHashCode() {
		BoxDB box1 = new BoxDB();
		BoxDB box2 = new BoxDB();
		BoxDB box3 = new BoxTest();
		BoxDB box4 = new BoxTest2();
		BoxDB box5 = new BoxTest2();
		box1.setX(1.2);
		box1.setY(1.3);
		box2.setX(1.2);
		box2.setY(1.3);
		box3.setX(1.2);
		box3.setY(1.3);
		box4.setX(1.2);
		box4.setY(1.3);
		box5.setX(1.2);
		box5.setY(1.3);
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
		this.logger.debug("===> box1.hashCode() with coords (1.2,1.3) {} {}", box1.hashCode(), box1);
		this.logger.debug("===> box2.hashCode() with coords (1.2,1.3) {} {}", box2.hashCode(), box2);
		this.logger.debug("===> box3.hashCode() with coords (1.2,1.3) {} {}", box3.hashCode(), box3);
		this.logger.debug("===> box4.hashCode() with coords (1.2,-1) {} {}", box4.hashCode(), box4);
		this.logger.debug("===> box5.hashCode() with coords (-1,1.3) {} {}", box5.hashCode(), box5);
		assertEquals(BOX_HASH_CODE, box1.hashCode());
		assertEquals(box1.hashCode(), box2.hashCode());
		assertEquals(box1.hashCode(), box3.hashCode());
		assertNotEquals(box1.hashCode(), box4.hashCode());
		assertNotEquals(box1.hashCode(), box5.hashCode());
		assertNotEquals(box1.hashCode(), caja.hashCode());
		box1.setX(0);
		box1.setY(0);
		box2.setX(0);
		box2.setY(0);
		box3.setX(0);
		box3.setY(0);
		box4.setX(0);
		box4.setY(0);
		box5.setX(0);
		box5.setY(0);
		assertEquals(BOX_WITH_COORDS_0, box1.hashCode());
		assertEquals(box1.hashCode(), box2.hashCode());
		assertEquals(box1.hashCode(), box3.hashCode());
		assertEquals(box1.hashCode(), box4.hashCode());
		assertEquals(box1.hashCode(), box5.hashCode());
		this.logger.debug("===> box1.hashCode() with coords (0,0) = {} {}", box1.hashCode(), box1);
		this.logger.debug("===> box2.hashCode() with coords (0,0) = {} {}", box2.hashCode(), box2);
		this.logger.debug("===> box3.hashCode() with coords (0,0) = {} {}", box3.hashCode(), box3);
		this.logger.debug("===> box4.hashCode() with coords (0,0) = {} {}", box4.hashCode(), box4);
		this.logger.debug("===> box5.hashCode() with coords (0,0) = {} {}", box5.hashCode(), box5);
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

	class EleTest2 extends Ele {
		@Override
		protected boolean canEqual(Object other) {
			return other instanceof Ele;
		}
	}

	class BoxTest extends BoxDB {
		@Override
		protected boolean canEqual(Object other) {
			return other instanceof BoxTest;
		}
	}

	class BoxTest2 extends BoxDB {
		@Override
		protected boolean canEqual(Object other) {
			return other instanceof BoxDB;
		}
	}
}