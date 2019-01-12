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
package org.omnifaces.utils.annotation;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * {@link InvocationHandler} implementation that implements the base methods required for an annotation.
 */
class AnnotationInvocationHandler implements InvocationHandler, Serializable {

	private static final long serialVersionUID = 1L;
	private static final Object[] NO_ARGS = new Object[0];

	private final Class<? extends Annotation> type;
	private final Map<String, Object> attributes;

	/**
	 * Create a new {@link AnnotationInvocationHandler} instance for the given annotation type and its attributes.
	 * @param type The annotation type this handler is for.
	 * @param attributes The attributes of the annotation, may be a partial map or even an empty map.
	 */
	AnnotationInvocationHandler(Class<? extends Annotation> type, Map<String, Object> attributes) {
		this.type = type;
		this.attributes = new HashMap<>(attributes);

		for (Method method : type.getDeclaredMethods()) {
			this.attributes.putIfAbsent(method.getName(), method.getDefaultValue());
		}
	}

	public boolean equals(Object proxy, Object other) {
		if (type.isInstance(other)) {
			try {
				Method[] methods = type.getDeclaredMethods();

				if (methods.length == attributes.size()) {
					for (Method method : methods) {
						if (!Objects.deepEquals(invoke(proxy, method, NO_ARGS), method.invoke(other))) {
							return false;
						}
					}

					return true;
				}
			}
			catch (Throwable ignore) {
				//
			}
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = 0;

		for (Method method : type.getDeclaredMethods()) {
			try {
				hashCode += Objects.hashCode(invoke(null, method, NO_ARGS)) ^ 127 * method.getName().hashCode();
			}
			catch (Throwable ignore) {
				//
			}
		}

		return hashCode;
	}

	@Override
	public String toString() {
		return "@" + type.getName() + "(" + attributes + ")";
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		switch (method.getName()) {
			case "annotationType":
				return type;
			case "equals":
				return args.length > 0 && equals(proxy, args[0]);
			case "hashCode":
				return hashCode();
			case "toString":
				return toString();
			default:
				return attributes.get(method.getName());
		}
	}

}