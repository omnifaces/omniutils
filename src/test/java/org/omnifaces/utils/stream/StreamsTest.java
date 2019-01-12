/*
 * Copyright 2019 OmniFaces
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

import static java.math.BigDecimal.ONE;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.omnifaces.utils.stream.Streams.mapToType;
import static org.omnifaces.utils.stream.Streams.range;
import static org.omnifaces.utils.stream.Streams.rangeClosed;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Test;

public class StreamsTest {

	private static final BigDecimal TWO = BigDecimal.valueOf(2L);
	private static final BigDecimal THREE = BigDecimal.valueOf(3L);

	private static class NonComparable {

		private final int value;

		private NonComparable(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public NonComparable decrement() {
			return new NonComparable(value - 1);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			NonComparable that = (NonComparable) o;
			return value == that.value;
		}

		@Override
		public int hashCode() {
			return Objects.hash(value);
		}

		@Override
		public String toString() {
			return "NonComparable{" +
					"value=" + value +
					'}';
		}
	}

	@Test
	public void testMapToType() {
		Optional<String> result = Stream.of((CharSequence) "")
		                                .flatMap(mapToType(String.class))
		                                .findAny();


		assertEquals("", result.get());

		Optional<StringBuilder> stringBuilderOptional = Stream.of((CharSequence) "")
		                                                      .flatMap(mapToType(StringBuilder.class))
		                                                      .findAny();
		assertFalse(stringBuilderOptional.isPresent());
	}

	@Test
	public void testRange() {
		assertEquals(asList(ONE, TWO), range(ONE, THREE, i -> i.add(ONE)).collect(toList()));

		assertEquals(
				asList(new NonComparable(2), new NonComparable(1), new NonComparable(0)),
				range(new NonComparable(2), new NonComparable(-1), NonComparable::decrement,
				      Comparator.comparingInt(NonComparable::getValue).reversed()).collect(toList())
		);
	}

	@Test
	public void testRangeClosed() {
		assertEquals(asList(ONE, TWO, THREE), rangeClosed(ONE, THREE, i -> i.add(ONE)).collect(toList()));

		assertEquals(
				asList(new NonComparable(2), new NonComparable(1), new NonComparable(0)),
				range(new NonComparable(2), new NonComparable(-1), NonComparable::decrement,
				      Comparator.comparingInt(NonComparable::getValue).reversed()).collect(toList())
		);
	}

}
