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

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.omnifaces.utils.Lang.capitalize;
import static org.omnifaces.utils.Lang.coalesce;
import static org.omnifaces.utils.Lang.containsIsoControlCharacters;
import static org.omnifaces.utils.Lang.endsWithOneOf;
import static org.omnifaces.utils.Lang.escapeAsProperty;
import static org.omnifaces.utils.Lang.ifEmptyGet;
import static org.omnifaces.utils.Lang.isAllEmpty;
import static org.omnifaces.utils.Lang.isAnyEmpty;
import static org.omnifaces.utils.Lang.isEmpty;
import static org.omnifaces.utils.Lang.isNotBlank;
import static org.omnifaces.utils.Lang.isOneOf;
import static org.omnifaces.utils.Lang.replaceLast;
import static org.omnifaces.utils.Lang.requireNotEmpty;
import static org.omnifaces.utils.Lang.setIfNotEmpty;
import static org.omnifaces.utils.Lang.startsWithOneOf;
import static org.omnifaces.utils.Lang.toTitleCase;
import static org.omnifaces.utils.Lang.toUrlSafe;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

public class LangTest {

	@Test
	public void setIfNotEmptyTest() {
		setIfNotEmpty(emptyList(), collection -> org.junit.jupiter.api.Assertions.fail());

		AtomicReference<List<String>> reference = new AtomicReference<>();

		setIfNotEmpty(singletonList(""), reference::set);

		assertNotNull(reference.get());
	}

	@Test
	public void titleCaseTest() {
		assertNull(toTitleCase(null));
		assertEquals("Lorem Ipsum Dolor Sit Amet", toTitleCase("lorem ipsum dolor sit amet"));
		assertEquals("Lorem Ipsum Dolor Sit Amet", toTitleCase("LOREM IPSUM DOLOR SIT AMET"));
		assertEquals("Lorem Ipsum Dolor Sit Amet", toTitleCase("lOREM iPSUM dOLOR sIT aMET"));
		assertEquals("Lorem Ipsum Dolor Sit Amet", toTitleCase("LOREm IPSUm DOLOr SIt AMEt"));
		assertEquals("Lorem Ipsum Dolor Sit Amet", toTitleCase("LoReM IpSuM DoLoR SiT AmEt"));
		assertEquals("Lorem Ipsum Dolor Sit Amet", toTitleCase("Lorem Ipsum Dolor Sit Amet"));
	}

	@Test
	public void urlSafeTest() {
		assertNull(toUrlSafe(null));
		assertEquals("lorem-ipsum-dolor-sit-amet", toUrlSafe("lorem ipsum dolor sit amet"));
		assertEquals("LOREM-IPSUM-DOLOR-SIT-AMET", toUrlSafe("LOREM IPSUM DOLOR SIT AMET"));
		assertEquals("lorem-ipsum-dOlor-sIt-amEt", toUrlSafe("lórém ípsúm dÓlor sÍt ámÉt"));
		assertEquals("lorem-ipsum-dolor-sit-amet", toUrlSafe("lorem--ipsum--dolor--sit--amet"));
		assertEquals("lorem-ipsum-dolor-sit-amet", toUrlSafe("?lorem&ipsum%dolor_sit amet-"));
	}

	@Test
	public void isEmptyStringReturnsTrueForNullOrEmpty() {
		assertTrue(isEmpty((String) null));
		assertTrue(isEmpty(""));
		assertFalse(isEmpty("a"));
	}

	@Test
	public void isEmptyArrayReturnsTrueForNullOrEmpty() {
		assertTrue(isEmpty((Object[]) null));
		assertTrue(isEmpty(new Object[0]));
		assertFalse(isEmpty(new Object[]{"a"}));
	}

	@Test
	public void isEmptyCollectionReturnsTrueForNullOrEmpty() {
		assertTrue(isEmpty((java.util.Collection<?>) null));
		assertTrue(isEmpty(emptyList()));
		assertFalse(isEmpty(singletonList("a")));
	}

	@Test
	public void isEmptyMapReturnsTrueForNullOrEmpty() {
		assertTrue(isEmpty((java.util.Map<?, ?>) null));
		assertTrue(isEmpty(emptyMap()));
		assertFalse(isEmpty(singletonMap("k", "v")));
	}

	@Test
	public void isEmptyObjectHandlesAllRecognizedTypes() {
		assertTrue(isEmpty((Object) null));
		assertTrue(isEmpty((Object) ""));
		assertFalse(isEmpty((Object) "x"));
		assertTrue(isEmpty((Object) emptyList()));
		assertFalse(isEmpty((Object) singletonList("a")));
		assertTrue(isEmpty((Object) emptyMap()));
		assertFalse(isEmpty((Object) singletonMap("k", "v")));
		assertTrue(isEmpty(Optional.empty()));
		assertFalse(isEmpty(Optional.of("x")));
		assertTrue(isEmpty((Object) new String[0]));
		assertFalse(isEmpty((Object) new String[]{"a"}));
	}

	@Test
	public void isAllEmptyReturnsTrueWhenAllEmpty() {
		assertTrue(isAllEmpty("", null, emptyList()));
	}

	@Test
	public void isAllEmptyReturnsFalseWhenOneIsNonEmpty() {
		assertFalse(isAllEmpty("", "x", null));
	}

	@Test
	public void isAnyEmptyReturnsTrueWhenAtLeastOneIsEmpty() {
		assertTrue(isAnyEmpty("x", "", "y"));
	}

	@Test
	public void isAnyEmptyReturnsFalseWhenNoneAreEmpty() {
		assertFalse(isAnyEmpty("x", "y", "z"));
	}

	@Test
	public void isNotBlankReturnsFalseForNullOrBlank() {
		assertFalse(isNotBlank(null));
		assertFalse(isNotBlank(""));
		assertFalse(isNotBlank("   "));
	}

	@Test
	public void isNotBlankReturnsTrueForNonBlank() {
		assertTrue(isNotBlank("hello"));
		assertTrue(isNotBlank("  x  "));
	}

	@Test
	public void coalesceReturnsFirstNonNull() {
		assertEquals("b", coalesce(null, "b", "c"));
		assertEquals("a", coalesce("a", "b"));
	}

	@Test
	public void coalesceReturnsNullWhenAllNull() {
		assertNull(coalesce(null, null));
	}

	@Test
	public void isOneOfReturnsTrueWhenMatchFound() {
		assertTrue(isOneOf("b", "a", "b", "c"));
	}

	@Test
	public void isOneOfReturnsFalseWhenNoMatchFound() {
		assertFalse(isOneOf("z", "a", "b", "c"));
	}

	@Test
	public void isOneOfHandlesNullObject() {
		assertTrue(isOneOf(null, "a", null, "c"));
		assertFalse(isOneOf(null, "a", "b"));
	}

	@Test
	public void startsWithOneOfReturnsTrueWhenPrefixMatches() {
		assertTrue(startsWithOneOf("hello world", "foo", "hello"));
	}

	@Test
	public void startsWithOneOfReturnsFalseWhenNoPrefixMatches() {
		assertFalse(startsWithOneOf("hello world", "foo", "bar"));
	}

	@Test
	public void endsWithOneOfReturnsTrueWhenSuffixMatches() {
		assertTrue(endsWithOneOf("hello world", "foo", "world"));
	}

	@Test
	public void endsWithOneOfReturnsFalseWhenNoSuffixMatches() {
		assertFalse(endsWithOneOf("hello world", "foo", "bar"));
	}

	@Test
	public void replaceLastReplacesOnlyLastOccurrence() {
		assertEquals("aXc", replaceLast("abc", "b", "X"));
		assertEquals("abXc", replaceLast("abbc", "b", "X"));
		assertEquals("hello-world", replaceLast("hello world", " ", "-"));
	}

	@Test
	public void containsIsoControlCharactersReturnsTrueForControlChars() {
		assertTrue(containsIsoControlCharacters("hello\nworld"));
		assertTrue(containsIsoControlCharacters("\u0001"));
	}

	@Test
	public void containsIsoControlCharactersReturnsFalseForNormalStrings() {
		assertFalse(containsIsoControlCharacters("hello world"));
		assertFalse(containsIsoControlCharacters("abc123!@#"));
	}

	@Test
	public void capitalizeConvertsFirstCharacterToUpperCase() {
		assertEquals("Hello", capitalize("hello"));
		assertEquals("Hello", capitalize("Hello"));
		assertEquals("A", capitalize("a"));
	}

	@Test
	public void capitalizeReturnsNullOrEmptyUnchanged() {
		assertNull(capitalize(null));
		assertEquals("", capitalize(""));
	}

	@Test
	public void escapeAsPropertyEscapesSpecialCharacters() throws IOException {
		assertEquals("hello", escapeAsProperty("hello"));
		assertEquals("key\\=value", escapeAsProperty("key=value"));
		assertEquals("key\\:value", escapeAsProperty("key:value"));
		assertEquals("\\\\", escapeAsProperty("\\"));
		assertEquals("\\t", escapeAsProperty("\t"));
		assertEquals("\\n", escapeAsProperty("\n"));
		assertEquals("\\r", escapeAsProperty("\r"));
		assertEquals("\\f", escapeAsProperty("\f"));
		assertEquals("\\u0001", escapeAsProperty("\u0001"));
	}

	@Test
	public void requireNotEmptyReturnsValueWhenNotEmpty() throws Exception {
		assertEquals("hello", requireNotEmpty("hello", () -> new Exception("empty")));
	}

	@Test
	public void requireNotEmptyThrowsWhenEmpty() {
		assertThrows(Exception.class, () -> requireNotEmpty("", () -> new Exception("empty")));
	}

	@Test
	public void ifEmptyGetReturnsOriginalValueWhenNotEmpty() {
		assertEquals("hello", ifEmptyGet("hello", () -> "default"));
	}

	@Test
	public void ifEmptyGetReturnsDefaultWhenEmpty() {
		assertEquals("default", ifEmptyGet("", () -> "default"));
		assertEquals("default", ifEmptyGet(null, () -> "default"));
	}

}
