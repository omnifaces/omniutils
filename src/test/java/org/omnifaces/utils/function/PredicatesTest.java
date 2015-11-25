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
