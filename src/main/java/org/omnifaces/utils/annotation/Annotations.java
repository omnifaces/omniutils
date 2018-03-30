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
package org.omnifaces.utils.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.Map;

public final class Annotations {

	private Annotations() {
		//
	}

	/**
	 * Create an instance of the specified annotation type with default attributes.
	 * @param <A> The generic annotation type.
	 * @param type The annotation type.
	 * @return An annotation instance of the specified annotation type with default attributes.
	 */
	public static <A extends Annotation> A createAnnotationInstance(Class<A> type) {
		return createAnnotationInstances(type)[0];
	}

	/**
	 * Create an array of instances of the specified annotation types with default attributes. Useful for varargs calls
	 * such as <code>CDI.current().select(type, createAnnotationInstances(Qualifier1.class, Qualifier2.class))</code>.
	 * @param <A> The generic annotation type.
	 * @param types The annotation types.
	 * @return An array of instances of the specified annotation types with default attributes.
	 */
	@SafeVarargs
	@SuppressWarnings("unchecked")
	public static <A extends Annotation> A[] createAnnotationInstances(Class<A>... types) {
		if (types == null) {
			return null;
		}

		A[] instances = (A[]) Array.newInstance(Annotation.class, types.length);

		for (int i = 0; i < types.length; i++) {
			instances[i] = createAnnotationInstance(types[i], Collections.<String, Object>emptyMap());
		}

		return instances;
	}

	/**
	 * Create an instance of the specified annotation type with given attributes.
	 * @param <A> The generic annotation type.
	 * @param type The annotation type.
	 * @param attributes The annotation attributes. May be a partial attribute map or even an empty map.
	 * @return An annotation instance of the specified annotation type with given attributes.
	 */
	public static <A extends Annotation> A createAnnotationInstance(Class<A> type, Map<String, Object> attributes) {
		InvocationHandler handler = new AnnotationInvocationHandler(type, attributes);
		return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class[] { type }, handler));
	}

}