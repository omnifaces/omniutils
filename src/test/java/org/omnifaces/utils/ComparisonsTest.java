package org.omnifaces.utils;

import static java.util.Comparator.reverseOrder;
import static org.junit.Assert.assertEquals;
import static org.omnifaces.utils.Comparisons.max;
import static org.omnifaces.utils.Comparisons.min;

import org.junit.Test;

public class ComparisonsTest {

	@Test
	public void testMin() {
		assertEquals(1L, (long) min(1, 2));
		assertEquals(1L, (long) min(2, 1));
		assertEquals(2L, (long) min(2, 2));

		assertEquals(2L, (long) min(4, 3, 2));
	}

	@Test
	public void testMinWithComparator() {
		assertEquals(2L, (long) min(reverseOrder(), 1, 2));
		assertEquals(2L, (long) min(reverseOrder(), 2, 1));
		assertEquals(2L, (long) min(reverseOrder(), 2, 2));
	}

	@Test
	public void testMax() {
		assertEquals(2L, (long) max(1, 2));
		assertEquals(2L, (long) max(2, 1));
		assertEquals(2L, (long) max(2, 2));

		assertEquals(2L, (long) max(0, 1, 2));
	}

	@Test
	public void testMaxWithComparator() {
		assertEquals(1L, (long) max(reverseOrder(), 1, 2));
		assertEquals(1L, (long) max(reverseOrder(), 2, 1));
		assertEquals(2L, (long) max(reverseOrder(), 2, 2));
	}

}
