/*
 * Copyright 2021 OmniFaces
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.omnifaces.utils;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.omnifaces.utils.Collections.contains;
import static org.omnifaces.utils.Collections.iterableToList;
import static org.omnifaces.utils.Collections.reverse;
import static org.omnifaces.utils.Collections.unmodifiableSet;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class CollectionsTest {

	@Test
	public void unmodifiableSetWithPlainValuesContainsAllValues() {
		Set<String> set = unmodifiableSet("a", "b", "c");
		assertEquals(3, set.size());
		assertTrue(set.contains("a"));
		assertTrue(set.contains("b"));
		assertTrue(set.contains("c"));
	}

	@Test
	public void unmodifiableSetFlattensArrayArguments() {
		String[] arr = {"a", "b"};
		Set<String> set = unmodifiableSet(arr, "c");
		assertEquals(3, set.size());
		assertTrue(set.contains("a"));
		assertTrue(set.contains("b"));
		assertTrue(set.contains("c"));
	}

	@Test
	public void unmodifiableSetFlattensCollectionArguments() {
		Set<String> set = unmodifiableSet(asList("a", "b"), "c");
		assertEquals(3, set.size());
		assertTrue(set.contains("a"));
		assertTrue(set.contains("c"));
	}

	@Test
	public void unmodifiableSetRejectsModification() {
		Set<String> set = unmodifiableSet("a");
		assertThrows(UnsupportedOperationException.class, () -> set.add("b"));
	}


	@Test
	public void iterableToListWithListInputReturnsSameInstance() {
		List<String> original = asList("a", "b");
		assertSame(original, iterableToList(original));
	}

	@Test
	public void iterableToListWithCollectionInputWrapsInNewList() {
		Set<String> set = new HashSet<>(asList("a", "b"));
		List<String> result = iterableToList(set);
		assertEquals(2, result.size());
	}

	@Test
	public void iterableToListWithPlainIterableConvertsToList() {
		Iterable<String> iterable = () -> asList("x", "y", "z").iterator();
		List<String> result = iterableToList(iterable);
		assertEquals(asList("x", "y", "z"), result);
	}


	@Test
	public void reverseSwapsKeysAndValues() {
		Map<String, String> source = Map.of("a", "b", "c", "d");
		Map<String, String> reversed = reverse(source);
		assertEquals("a", reversed.get("b"));
		assertEquals("c", reversed.get("d"));
	}

	@Test
	public void reverseEmptyMapReturnsEmptyMap() {
		assertTrue(reverse(Map.of()).isEmpty());
	}


	@Test
	public void containsReturnsFalseForNullCollection() {
		assertFalse(contains(null, "x"));
	}

	@Test
	public void containsReturnsTrueWhenElementIsPresent() {
		assertTrue(contains(asList("a", "b", "c"), "b"));
	}

	@Test
	public void containsReturnsFalseWhenElementIsAbsent() {
		assertFalse(contains(asList("a", "b"), "z"));
	}

}
