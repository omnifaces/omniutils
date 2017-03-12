package org.omnifaces.utils;

import static java.util.Comparator.naturalOrder;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.omnifaces.utils.Comparators.firstWhen;
import static org.omnifaces.utils.Comparators.lastWhen;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Test;

public class ComparatorsTest {

	@Test
	public void testFirstWhen() {
		List<String> result = Stream.of("D", "A", "BB", "C", "BA")
		                            .sorted(firstWhen(s -> s.startsWith("B"), naturalOrder()))
		                            .collect(toList());

		assertEquals(Arrays.asList("BA", "BB", "A", "C", "D"), result);
	}

	@Test
	public void testLastWhen() {
		List<String> result = Stream.of("D", "A", "BB", "C", "BA")
		                            .sorted(lastWhen(s -> s.startsWith("B"), naturalOrder()))
		                            .collect(toList());


		assertEquals(Arrays.asList("A", "C", "D", "BA", "BB"), result);
	}
}
