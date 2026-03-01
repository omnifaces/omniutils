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
package org.omnifaces.utils.function;

import static java.util.Comparator.reverseOrder;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class PredicatesTest {

	@Test
	public void alwaysReturnsTrueForAnyInput() {
		assertTrue(Predicates.<String>always().test("anything"));
		assertTrue(Predicates.<String>always().test(null));
	}

	@Test
	public void neverReturnsFalseForAnyInput() {
		assertFalse(Predicates.<String>never().test("anything"));
		assertFalse(Predicates.<String>never().test(null));
	}

	@Test
	public void isEmptyReturnsTrueForNullOrEmpty() {
		assertTrue(Predicates.<String>isEmpty().test(null));
		assertTrue(Predicates.<String>isEmpty().test(""));
	}

	@Test
	public void isEmptyReturnsFalseForNonEmpty() {
		assertFalse(Predicates.<String>isEmpty().test("x"));
	}

	@Test
	public void isNotEmptyReturnsTrueForNonEmpty() {
		assertTrue(Predicates.<String>isNotEmpty().test("x"));
	}

	@Test
	public void isNotEmptyReturnsFalseForNullOrEmpty() {
		assertFalse(Predicates.<String>isNotEmpty().test(null));
		assertFalse(Predicates.<String>isNotEmpty().test(""));
	}

	@Test
	public void isNullReturnsTrueForNull() {
		assertTrue(Predicates.<String>isNull().test(null));
	}

	@Test
	public void isNullReturnsFalseForNonNull() {
		assertFalse(Predicates.<String>isNull().test("x"));
	}

	@Test
	public void isNotNullReturnsTrueForNonNull() {
		assertTrue(Predicates.<String>isNotNull().test("x"));
	}

	@Test
	public void isNotNullReturnsFalseForNull() {
		assertFalse(Predicates.<String>isNotNull().test(null));
	}

	@Test
	public void isLessThan() {
		assertTrue(Predicates.isLessThan("b").test("a"));
		assertFalse(Predicates.isLessThan("b").test("b"));
		assertFalse(Predicates.isLessThan("a").test("b"));
	}

	@Test
	public void isLessThanWithComparator() {
		assertTrue(Predicates.isLessThan("a", reverseOrder()).test("b"));
		assertFalse(Predicates.isLessThan("b", reverseOrder()).test("a"));
	}

	@Test
	public void isLessThanOrEqual() {
		assertTrue(Predicates.isLessThanOrEqual("b").test("a"));
		assertTrue(Predicates.isLessThanOrEqual("b").test("b"));
		assertFalse(Predicates.isLessThanOrEqual("a").test("b"));
	}

	@Test
	public void isLessThanOrEqualWithComparator() {
		assertTrue(Predicates.isLessThanOrEqual("a", reverseOrder()).test("b"));
		assertTrue(Predicates.isLessThanOrEqual("b", reverseOrder()).test("b"));
	}

	@Test
	public void isComparativelyEqualToReturnsTrueForSameValue() {
		assertTrue(Predicates.isComparativelyEqualTo("b").test("b"));
	}

	@Test
	public void isComparativelyEqualToReturnsFalseForDifferentValue() {
		assertFalse(Predicates.isComparativelyEqualTo("b").test("a"));
	}

	@Test
	public void isComparativelyEqualToWithComparator() {
		assertTrue(Predicates.isComparativelyEqualTo("b", reverseOrder()).test("b"));
		assertFalse(Predicates.isComparativelyEqualTo("b", reverseOrder()).test("a"));
	}

	@Test
	public void isGreaterThan() {
		assertFalse(Predicates.isGreaterThan("b").test("a"));
		assertFalse(Predicates.isGreaterThan("b").test("b"));
		assertTrue(Predicates.isGreaterThan("a").test("b"));
	}

	@Test
	public void isGreaterThanWithComparator() {
		assertTrue(Predicates.isGreaterThan("b", reverseOrder()).test("a"));
		assertFalse(Predicates.isGreaterThan("a", reverseOrder()).test("b"));
	}

	@Test
	public void isGreaterThanOrEqual() {
		assertFalse(Predicates.isGreaterThanOrEqual("b").test("a"));
		assertTrue(Predicates.isGreaterThanOrEqual("b").test("b"));
		assertTrue(Predicates.isGreaterThanOrEqual("a").test("b"));
	}

	@Test
	public void isGreaterThanOrEqualWithComparator() {
		assertTrue(Predicates.isGreaterThanOrEqual("b", reverseOrder()).test("a"));
		assertTrue(Predicates.isGreaterThanOrEqual("b", reverseOrder()).test("b"));
	}

	@Test
	public void mappedAppliesFunctionBeforeTesting() {
		assertTrue(Predicates.mapped(String::length, Predicates.isGreaterThan(2)).test("abc"));
		assertFalse(Predicates.mapped(String::length, Predicates.isGreaterThan(2)).test("ab"));
	}

	@Test
	public void notNegatesThePredicate() {
		assertFalse(Predicates.not(Predicates.<String>always()).test("x"));
		assertTrue(Predicates.not(Predicates.<String>never()).test("x"));
	}

}
