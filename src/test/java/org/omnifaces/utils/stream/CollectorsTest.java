/*
 * Copyright 2018 OmniFaces
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.omnifaces.utils.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.omnifaces.utils.stream.Collectors.findLast;

import java.util.Iterator;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

	@Test
	public void testFindLast() {
		assertEquals(Optional.of("a"), Stream.of("a").collect(findLast()));
		assertEquals(Optional.of("b"), Stream.of("a", "b").collect(findLast()));
		assertEquals(Optional.empty(), Stream.empty().collect(findLast()));
	}
}
