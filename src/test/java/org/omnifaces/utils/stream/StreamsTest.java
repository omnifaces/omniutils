package org.omnifaces.utils.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.omnifaces.utils.stream.Streams.mapToType;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Test;

public class StreamsTest {

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

}
