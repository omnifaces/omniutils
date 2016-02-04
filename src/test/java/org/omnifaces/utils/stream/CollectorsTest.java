package org.omnifaces.utils.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.stream.IntStream;

import org.junit.Test;

public class CollectorsTest {

	@Test
	public void testReversedStream() {
		Iterator<Integer> iterator = IntStream.rangeClosed(0, 10000000)
		                                      .boxed()
		                                      .parallel()
		                                      .collect(Collectors.reversedStream()).iterator();

		for (int i = 10000000; i >= 0; i--) {
			assertTrue(iterator.hasNext());
			assertEquals(i, iterator.next().intValue());
		}
	}
}
