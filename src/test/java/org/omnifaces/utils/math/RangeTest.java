package org.omnifaces.utils.math;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RangeTest {

	@Test
	public void testLongRange() {
		LongRange longRange1 = LongRange.of(1L, 10L);
		Range<Long> longRange2 = Range.of(1L, 10L);
		LongRange longRange3 = LongRange.of(-1L, 1L);
		Range<Long> longRange4 = LongRange.of(-10L, 0L);

		testLongRangeEquals(longRange1, longRange2, longRange3, longRange4);
		testLongRangeHashcode(longRange1, longRange2, longRange3, longRange4);
		testLongRangeToString(longRange1, longRange2, longRange3, longRange4);
		testLongRangeToArray(longRange1, longRange2, longRange3, longRange4);
		testLongRangeIntersects(longRange1, longRange2, longRange3, longRange4);
	}

	private void testLongRangeIntersects(Range<Long> longRange1, Range<Long> longRange2, Range<Long> longRange3, Range<Long> longRange4) {
		assertTrue(longRange1.intersects(longRange1));
		assertTrue(longRange1.intersects(longRange2));
		assertTrue(longRange1.intersects(longRange3));
		assertFalse(longRange1.intersects(longRange4));
		assertTrue(longRange2.intersects(longRange1));
		assertTrue(longRange2.intersects(longRange2));
		assertTrue(longRange2.intersects(longRange3));
		assertFalse(longRange2.intersects(longRange4));
		assertTrue(longRange3.intersects(longRange1));
		assertTrue(longRange3.intersects(longRange2));
		assertTrue(longRange3.intersects(longRange3));
		assertTrue(longRange3.intersects(longRange4));
		assertFalse(longRange4.intersects(longRange1));
		assertFalse(longRange4.intersects(longRange2));
		assertTrue(longRange4.intersects(longRange3));
		assertTrue(longRange4.intersects(longRange4));
	}

	private void testLongRangeToArray(Range<Long> longRange1, Range<Long> longRange2, Range<Long> longRange3, Range<Long> longRange4) {
		assertArrayEquals(longRange1.toArray(), longRange2.toArray());
		assertNotEquals(longRange1.toArray(), longRange3.toArray());
		assertNotEquals(longRange1.toArray(), longRange4.toArray());
		assertNotEquals(longRange2.toArray(), longRange3.toArray());
		assertNotEquals(longRange2.toArray(), longRange4.toArray());
		assertNotEquals(longRange3.toArray(), longRange4.toArray());
	}

	private void testLongRangeToString(Range<Long> longRange1, Range<Long> longRange2, Range<Long> longRange3, Range<Long> longRange4) {
		assertEquals(longRange1.toString(), longRange2.toString());
		assertNotEquals(longRange1.toString(), longRange3.toString());
		assertNotEquals(longRange1.toString(), longRange4.toString());
		assertNotEquals(longRange2.toString(), longRange3.toString());
		assertNotEquals(longRange2.toString(), longRange4.toString());
		assertNotEquals(longRange3.toString(), longRange4.toString());
	}

	private void testLongRangeHashcode(Range<Long> longRange1, Range<Long> longRange2, Range<Long> longRange3, Range<Long> longRange4) {
		assertEquals(longRange1.hashCode(), longRange2.hashCode());
		assertNotEquals(longRange1.hashCode(), longRange3.hashCode());
		assertNotEquals(longRange1.hashCode(), longRange4.hashCode());
		assertNotEquals(longRange2.hashCode(), longRange3.hashCode());
		assertNotEquals(longRange2.hashCode(), longRange4.hashCode());
		assertNotEquals(longRange3.hashCode(), longRange4.hashCode());
	}

	private void testLongRangeEquals(Range<Long> longRange1, Range<Long> longRange2, Range<Long> longRange3, Range<Long> longRange4) {
		assertEquals(longRange1, longRange2);
		assertNotEquals(longRange1, longRange3);
		assertNotEquals(longRange1, longRange4);
		assertNotEquals(longRange2, longRange3);
		assertNotEquals(longRange2, longRange4);
		assertNotEquals(longRange3, longRange4);
	}

}
