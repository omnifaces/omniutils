/*
 * Copyright 2019 OmniFaces
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
package org.omnifaces.utils;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.util.Collections;

import org.junit.Test;
import org.omnifaces.utils.annotation.Annotations;

public class AnnotationsTest {

	@Test
	public void createAnnotationInstanceTest() throws Exception {
		Annotation actualFoo = AnnotationsTest.class.getMethod("foo").getAnnotation(Foo.class);
		Annotation actualFooWithAttribute = AnnotationsTest.class.getMethod("fooWithAttribute").getAnnotation(Foo.class);

		Annotation proxiedFoo = Annotations.createAnnotationInstance(Foo.class);
		Annotation proxiedFooWithAttribute = Annotations.createAnnotationInstance(Foo.class, Collections.singletonMap("foo", (Object) "foo"));

		assertTrue(actualFoo.equals(proxiedFoo));
		assertTrue(actualFooWithAttribute.equals(proxiedFooWithAttribute));
		assertEquals(actualFoo.hashCode(), proxiedFoo.hashCode());
		assertEquals(actualFooWithAttribute.hashCode(), proxiedFooWithAttribute.hashCode());

		assertFalse(actualFoo.equals(proxiedFooWithAttribute));
		assertFalse(actualFooWithAttribute.equals(proxiedFoo));
		assertNotEquals(actualFoo.hashCode(), proxiedFooWithAttribute.hashCode());
		assertNotEquals(actualFooWithAttribute.hashCode(), proxiedFoo.hashCode());
	}

	public static @Retention(RUNTIME) @interface Foo {
		String foo() default "";
	}

	@Foo
	public void foo() {
		//
	}

	@Foo(foo="foo")
	public void fooWithAttribute() {
		//
	}

}