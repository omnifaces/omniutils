package org.omnifaces.utils.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public abstract class BaseRangeTest {

	protected abstract <N extends Comparable<N>> Range<N> newRange(N min, N max);

	@Test
	public void testWithMin() {
		Range<Integer> range = newRange(1, 10);

		Range<Integer> newRange = range.withMin(0);

		assertEquals((Integer)1, range.getMin());
		assertNotEquals(range, newRange);

		assertEquals(range.getMax(), newRange.getMax());
		assertEquals(range.isMinInclusive(), newRange.isMinInclusive());
		assertEquals(range.isMaxInclusive(), newRange.isMaxInclusive());

		assertEquals((Integer)0, newRange.getMin());
	}

	@Test
	public void testWithMax() {
		Range<Integer> range = newRange(1, 10);

		Range<Integer> newRange = range.withMax(100);

		assertEquals((Integer)10, range.getMax());
		assertNotEquals(range, newRange);

		assertEquals(range.getMin(), newRange.getMin());
		assertEquals(range.isMinInclusive(), newRange.isMinInclusive());
		assertEquals(range.isMaxInclusive(), newRange.isMaxInclusive());

		assertEquals((Integer)100, newRange.getMax());
	}

	@Test
	public void testWithMinInclusive() {
		Range<Integer> range = newRange(1, 10).withMinInclusive(true);

		assertTrue(range.isMinInclusive());

		Range<Integer> newRange = range.withMinInclusive(false);

		assertTrue(range.isMinInclusive());

		assertFalse(newRange.isMinInclusive());

		assertNotEquals(range, newRange);
		assertEquals(range.getMin(), newRange.getMin());
		assertEquals(range.getMax(), newRange.getMax());
		assertEquals(range.isMaxInclusive(), newRange.isMaxInclusive());
	}

	@Test
	public void testWithMaxInclusive() {
		Range<Integer> range = newRange(1, 10).withMaxInclusive(true);

		assertTrue(range.isMaxInclusive());

		Range<Integer> newRange = range.withMaxInclusive(false);

		assertTrue(range.isMaxInclusive());

		assertFalse(newRange.isMaxInclusive());

		assertNotEquals(range, newRange);
		assertEquals(range.getMin(), newRange.getMin());
		assertEquals(range.getMax(), newRange.getMax());
		assertEquals(range.isMinInclusive(), newRange.isMinInclusive());
	}

	@Test
	public void testContains() {
		Range<Integer> range = newRange(1, 10).withMinInclusive(true).withMaxInclusive(true);

		assertFalse(range.contains(0));
		assertFalse(range.contains(11));
		assertTrue(range.contains(1));
		assertTrue(range.contains(10));
		assertTrue(range.contains(5));

		range = range.withMinInclusive(false);

		assertFalse(range.contains(1));

		range= range.withMaxInclusive(false);

		assertFalse(range.contains(10));
	}

	@Test
	public void testIntersects() {
		Range<Integer> range1 = newRange(1, 10).withMinInclusive(true).withMaxInclusive(false);
		Range<Integer> range2 = newRange(10, 20).withMinInclusive(true).withMaxInclusive(false);

		assertTrue(range1.intersects(range1));
		assertTrue(range2.intersects(range2));

		assertFalse(range1.intersects(range2));
		assertFalse(range2.intersects(range1));

		range1 = range1.withMaxInclusive(true);

		assertTrue(range1.intersects(range2));
		assertTrue(range2.intersects(range1));

		range2 = range2.withMinInclusive(false);

		assertFalse(range1.intersects(range2));
		assertFalse(range2.intersects(range1));

		range1 = range1.withMaxInclusive(false);

		assertFalse(range1.intersects(range2));
		assertFalse(range2.intersects(range1));
	}
}
