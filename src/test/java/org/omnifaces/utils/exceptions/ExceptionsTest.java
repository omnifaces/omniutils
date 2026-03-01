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
package org.omnifaces.utils.exceptions;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.omnifaces.utils.exceptions.Exceptions.excludeAll;
import static org.omnifaces.utils.exceptions.Exceptions.excludeFromStackTrace;
import static org.omnifaces.utils.exceptions.Exceptions.excludeJavaEE;
import static org.omnifaces.utils.exceptions.Exceptions.excludeJavaSE;
import static org.omnifaces.utils.exceptions.Exceptions.getRecursiveStackTrace;

import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

public class ExceptionsTest {

	@Test
	public void getRecursiveStackTraceSimpleExceptionContainsClassNameAndMessage() {
		String trace = getRecursiveStackTrace(new RuntimeException("test error"));
		assertNotNull(trace);
		assertTrue(trace.contains("RuntimeException"));
		assertTrue(trace.contains("test error"));
	}

	@Test
	public void getRecursiveStackTraceChainedExceptionContainsAllLevels() {
		RuntimeException cause = new RuntimeException("root cause");
		RuntimeException wrapper = new RuntimeException("wrapper message", cause);
		String trace = getRecursiveStackTrace(wrapper);
		assertTrue(trace.contains("wrapper message"));
		assertTrue(trace.contains("root cause"));
		assertTrue(trace.contains("Exception level 0"));
		assertTrue(trace.contains("Exception level 1"));
	}

	@Test
	public void getRecursiveStackTraceWithAllExcludingFilterStillContainsHeader() {
		String trace = getRecursiveStackTrace(new RuntimeException("filtered"), frame -> false);
		assertNotNull(trace);
		assertTrue(trace.contains("RuntimeException"));
	}

	@Test
	public void getRecursiveStackTraceWithSuppressedExceptionsContainsSuppressedInfo() {
		RuntimeException main = new RuntimeException("main");
		main.addSuppressed(new RuntimeException("suppressed"));
		String trace = getRecursiveStackTrace(main);
		assertTrue(trace.contains("suppressed"));
	}


	@Test
	public void excludeFromStackTraceFiltersMatchingFramesByClassName() {
		Predicate<StackTraceElement> predicate = excludeFromStackTrace(asList("com.example"));
		StackTraceElement excluded = new StackTraceElement("com.example.Foo", "method", "Foo.java", 1);
		StackTraceElement included = new StackTraceElement("org.other.Bar", "method", "Bar.java", 1);
		assertFalse(predicate.test(excluded));
		assertTrue(predicate.test(included));
	}

	@Test
	public void excludeFromStackTraceEmptyListKeepsAllFrames() {
		Predicate<StackTraceElement> predicate = excludeFromStackTrace(emptyList());
		assertTrue(predicate.test(new StackTraceElement("any.Class", "method", "Class.java", 1)));
	}

	@Test
	public void excludeFromStackTraceNullListThrows() {
		assertThrows(NullPointerException.class, () -> excludeFromStackTrace(null));
	}


	@Test
	public void excludeJavaSEFiltersReflectionFrames() {
		Predicate<StackTraceElement> predicate = excludeJavaSE();
		StackTraceElement reflectFrame = new StackTraceElement("java.lang.reflect.Method", "invoke", "Method.java", 1);
		StackTraceElement normalFrame = new StackTraceElement("org.example.Foo", "bar", "Foo.java", 1);
		assertFalse(predicate.test(reflectFrame));
		assertTrue(predicate.test(normalFrame));
	}

	@Test
	public void excludeJavaEEFiltersCatalinaFrames() {
		Predicate<StackTraceElement> predicate = excludeJavaEE();
		StackTraceElement catalinaFrame = new StackTraceElement("org.apache.catalina.core.StandardFilter", "doFilter", "StandardFilter.java", 1);
		StackTraceElement normalFrame = new StackTraceElement("org.example.Foo", "bar", "Foo.java", 1);
		assertFalse(predicate.test(catalinaFrame));
		assertTrue(predicate.test(normalFrame));
	}

	@Test
	public void excludeAllFiltersBothJavaSEAndJavaEEFrames() {
		Predicate<StackTraceElement> predicate = excludeAll();
		StackTraceElement reflectFrame = new StackTraceElement("java.lang.reflect.Method", "invoke", "Method.java", 1);
		StackTraceElement catalinaFrame = new StackTraceElement("org.apache.catalina.core.StandardFilter", "doFilter", "StandardFilter.java", 1);
		StackTraceElement normalFrame = new StackTraceElement("org.example.Foo", "bar", "Foo.java", 1);
		assertFalse(predicate.test(reflectFrame));
		assertFalse(predicate.test(catalinaFrame));
		assertTrue(predicate.test(normalFrame));
	}

}
