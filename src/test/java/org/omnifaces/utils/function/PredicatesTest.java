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
package org.omnifaces.utils.function;

import static org.junit.Assert.*;

import org.junit.Test;

public class PredicatesTest {

	@Test
	public void testIsLessThan() {
		assertTrue(Predicates.isLessThan("b").test("a"));
		assertFalse(Predicates.isLessThan("b").test("b"));
		assertFalse(Predicates.isLessThan("a").test("b"));
	}

	@Test
	public void testIsLessThanOrEqual() {
		assertTrue(Predicates.isLessThanOrEqual("b").test("a"));
		assertTrue(Predicates.isLessThanOrEqual("b").test("b"));
		assertFalse(Predicates.isLessThanOrEqual("a").test("b"));
	}

	@Test
	public void testIsGreaterThan() {
		assertFalse(Predicates.isGreaterThan("b").test("a"));
		assertFalse(Predicates.isGreaterThan("b").test("b"));
		assertTrue(Predicates.isGreaterThan("a").test("b"));
	}

	@Test
	public void testIsGreaterThanOrEqual() {
		assertFalse(Predicates.isGreaterThanOrEqual("b").test("a"));
		assertTrue(Predicates.isGreaterThanOrEqual("b").test("b"));
		assertTrue(Predicates.isGreaterThanOrEqual("a").test("b"));
	}
}
