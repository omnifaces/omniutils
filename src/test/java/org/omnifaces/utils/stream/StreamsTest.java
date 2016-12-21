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

		public NonComparable increment() {
			return new NonComparable(value + 1);
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
