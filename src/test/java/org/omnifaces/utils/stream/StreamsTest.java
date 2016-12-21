package org.omnifaces.utils.stream;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.util.Arrays.asList;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.omnifaces.utils.stream.Streams.mapToType;
import static org.omnifaces.utils.stream.Streams.range;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Test;

public class StreamsTest {

	public static final BigDecimal TWO = BigDecimal.valueOf(2L);
	public static final BigDecimal THREE = BigDecimal.valueOf(3L);

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
				asList(TWO, ONE, ZERO),
				range(TWO, ONE.negate(), i -> i.subtract(ONE), reverseOrder()).collect(toList())
		);
	}

}
