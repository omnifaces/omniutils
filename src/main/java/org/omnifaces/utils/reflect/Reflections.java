/*
 * Copyright 2017 OmniFaces
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
package org.omnifaces.utils.reflect;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableList;
import static java.util.logging.Level.FINEST;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

public final class Reflections {

	private static final Logger logger = Logger.getLogger(Reflections.class.getName());

	private static final String ERROR_LOAD_CLASS = "Cannot load class '%s'.";
	private static final String ERROR_INSTANTIATE = "Cannot instantiate class '%s'.";
	private static final String ERROR_ACCESS_FIELD = "Cannot access field '%s' of class '%s'.";
	private static final String ERROR_INVOKE_METHOD = "Cannot invoke method '%s' of class '%s' with arguments %s.";
	private static final String ERROR_MAP_FIELD = "Cannot map field '%s' from %s to %s.";

	private Reflections() {
		// Hide constructor.
	}

	/**
	 * Finds a method based on the method name, amount of parameters and limited typing and returns <code>null</code>
	 * is none is found.
	 * <p>
	 * Note that this supports overloading, but a limited one. Given an actual parameter of type Long, this will select
	 * a method accepting Number when the choice is between Number and a non-compatible type like String. However,
	 * it will NOT select the best match if the choice is between Number and Long.
	 *
	 * @param base the object in which the method is to be found
	 * @param methodName name of the method to be found
	 * @param params the method parameters
	 * @return a method if one is found, null otherwise
	 */
	public static Optional<Method> findMethod(Object base, String methodName, Object... params) {

		List<Method> methods = new ArrayList<>();

		for (Class<?> cls = base.getClass(); cls != null; cls = cls.getSuperclass()) {
			for (Method method : cls.getDeclaredMethods()) {
				if (method.getName().equals(methodName) && method.getParameterTypes().length == params.length) {
					methods.add(method);
				}
			}
		}

		if (methods.size() == 1) {
			return Optional.of(methods.get(0));
		}
		else {
			return Optional.ofNullable(closestMatchingMethod(methods, params)); // Overloaded methods were found. Try to find closest match.
		}
	}

	private static Method closestMatchingMethod(List<Method> methods, Object... params) {
		for (Method method : methods) {
			Class<?>[] candidateParams = method.getParameterTypes();
			boolean match = true;

			for (int i = 0; i < params.length; i++) {
				if (!candidateParams[i].isInstance(params[i])) {
					match = false;
					break;
				}
			}

			// If all candidate parameters were expected and for none of them the actual parameter was NOT an instance, we have a match.
			if (match) {
				return method;
			}

			// Else, at least one parameter was not an instance. Go ahead a test then next methods.
		}

		return null;
	}

	/**
	 * Returns the class object associated with the given class name, using the context class loader and if
	 * that fails the defining class loader of the current class.
	 * @param <T> The expected class type.
	 * @param className Fully qualified class name of the class for which a class object needs to be created.
	 * @return The class object associated with the given class name.
	 * @throws IllegalStateException If the class cannot be loaded.
	 * @throws ClassCastException When <code>T</code> is of wrong type.
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> toClass(String className) {
		try {
			return (Class<T>) (Class.forName(className, true, Thread.currentThread().getContextClassLoader()));
		}
		catch (Exception e) {
			try {
				return (Class<T>) Class.forName(className);
			}
			catch (Exception ignore) {
				logger.log(FINEST, "Ignoring thrown exception; previous exception will be rethrown instead.", ignore);
				// Just continue to IllegalStateException on original ClassNotFoundException.
			}

			throw new IllegalStateException(format(ERROR_LOAD_CLASS, className), e);
		}
	}

	/**
	 * Returns the class object associated with the given class name, using the context class loader and if
	 * that fails the defining class loader of the current class. If the class cannot be loaded, then return null
	 * instead of throwing illegal state exception.
	 * @param <T> The expected class type.
	 * @param className Fully qualified class name of the class for which a class object needs to be created.
	 * @return The class object associated with the given class name.
	 * @throws ClassCastException When <code>T</code> is of wrong type.
	 */
	public static <T> Optional<Class<T>> findClass(String className) {
		try {
			return Optional.of(toClass(className));
		}
		catch (Exception ignore) {
			logger.log(FINEST, "Ignoring thrown exception; the sole intent is to return null instead.", ignore);
			return Optional.ofNullable(null);
		}
	}

	/**
	 * Finds a constructor based on the given parameter types and returns <code>null</code> is none is found.
	 * @param clazz The class object for which the constructor is to be found.
	 * @param parameterTypes The desired method parameter types.
	 * @return A constructor if one is found, null otherwise.
	 */
	public static <T> Optional<Constructor<T>> findConstructor(Class<T> clazz, Class<?>... parameterTypes) {
		try {
			return Optional.of(clazz.getConstructor(parameterTypes));
		}
		catch (Exception ignore) {
			logger.log(FINEST, "Ignoring thrown exception; the sole intent is to return null instead.", ignore);
			return Optional.ofNullable(null);
		}
	}

	/**
	 * Returns a new instance of the given class name using the default constructor.
	 * @param <T> The expected return type.
	 * @param className Fully qualified class name of the class for which an instance needs to be created.
	 * @return A new instance of the given class name using the default constructor.
	 * @throws IllegalStateException If the class cannot be loaded.
	 * @throws ClassCastException When <code>T</code> is of wrong type.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T instantiate(String className) {
		return (T) instantiate(toClass(className));
	}

	/**
	 * Returns a new instance of the given class object using the default constructor.
	 * @param <T> The generic object type.
	 * @param clazz The class object for which an instance needs to be created.
	 * @return A new instance of the given class object using the default constructor.
	 * @throws IllegalStateException If the class cannot be found, or cannot be instantiated, or when a security manager
	 * prevents this operation.
	 */
	public static <T> T instantiate(Class<T> clazz) {
		try {
			return clazz.newInstance();
		}
		catch (Exception e) {
			throw new IllegalStateException(format(ERROR_INSTANTIATE, clazz), e);
		}
	}

	/**
	 * Returns the value of the field of the given instance on the given field name.
	 * @param <T> The expected return type.
	 * @param instance The instance to access the given field on.
	 * @param fieldName The name of the field to be accessed on the given instance.
	 * @return The value of the field of the given instance on the given field name.
	 * @throws ClassCastException When <code>T</code> is of wrong type.
	 * @throws IllegalStateException If the field cannot be accessed.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T accessField(Object instance, String fieldName) {
		try {
			Field field = instance.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			return (T) field.get(instance);
		}
		catch (Exception e) {
			throw new IllegalStateException(format(ERROR_ACCESS_FIELD, fieldName, instance.getClass()), e);
		}
	}

	/**
	 * Invoke a method of the given instance on the given method name with the given parameters and return the result.
	 * <p>
	 * Note: the current implementation assumes for simplicity that no one of the given parameters is null. If one of
	 * them is still null, a NullPointerException will be thrown.
	 * @param <T> The expected return type.
	 * @param instance The instance to invoke the given method on.
	 * @param methodName The name of the method to be invoked on the given instance.
	 * @param parameters The method parameters, if any.
	 * @return The result of the method invocation, if any.
	 * @throws NullPointerException When one of the given parameters is null.
	 * @throws IllegalStateException If the method cannot be invoked.
	 * @throws ClassCastException When <code>T</code> is of wrong type.
	 */
	public static <T> T invokeMethod(Object instance, String methodName, Object... parameters) {
		try {
			Method method = findMethod(instance, methodName, parameters).orElseThrow(NoSuchMethodException::new);
			return invokeMethod(instance, method, parameters);
		}
		catch (Exception e) {
			throw new IllegalStateException(format(ERROR_INVOKE_METHOD, methodName, instance.getClass(), Arrays.toString(parameters)), e);
		}
	}

	/**
	 * Invoke given method of the given instance with the given parameters and return the result.
	 * @param <T> The expected return type.
	 * @param instance The instance to invoke the given method on.
	 * @param method The method to be invoked on the given instance.
	 * @param parameters The method parameters, if any.
	 * @return The result of the method invocation, if any.
	 * @throws NullPointerException When one of the given parameters is null.
	 * @throws IllegalStateException If the method cannot be invoked.
	 * @throws ClassCastException When <code>T</code> is of wrong type.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T invokeMethod(Object instance, Method method, Object... parameters) {
		try {
			method.setAccessible(true);
			return (T) method.invoke(instance, parameters);
		}
		catch (Exception e) {
			throw new IllegalStateException(format(ERROR_INVOKE_METHOD, method.getName(), instance.getClass(), Arrays.toString(parameters)), e);
		}
	}

	/**
	 * Map given member from given object to given object.
	 *
	 * @param <T> The from/target object type.
	 * @param member Member to be mapped.
	 * @param from Source object.
	 * @param to Target object.
	 */
	public static <T> void map(Member member, T from, T to) {
		if (member instanceof Field) {
			Field field = (Field) member;

			try {
				field.setAccessible(true);
				field.set(to, field.get(from));
			}
			catch (Exception e) {
				throw new IllegalStateException(format(ERROR_MAP_FIELD, field.getName(), from, to), e);
			}
		}
		else {
			throw new UnsupportedOperationException("Not implemented yet for " + member);
		}
	}

	public static <T> List<Class<?>> getActualTypeArguments(Class<? extends T> subclass, Class<T> superClass) {
		Map<TypeVariable<?>, Type> typeMapping = new HashMap<>();
		Type actualType = subclass.getGenericSuperclass();

		while (!(actualType instanceof ParameterizedType) || !superClass.equals(((ParameterizedType) actualType).getRawType())) {
			if (actualType instanceof ParameterizedType) {
				Class<?> rawType = (Class<?>) ((ParameterizedType) actualType).getRawType();
				TypeVariable<?>[] typeParameters = rawType.getTypeParameters();

				for (int i = 0; i < typeParameters.length; i++) {
					Type typeArgument = ((ParameterizedType) actualType).getActualTypeArguments()[i];
					typeMapping.put(typeParameters[i], typeArgument instanceof TypeVariable ? typeMapping.get(typeArgument) : typeArgument);
				}

				actualType = rawType;
			}

			actualType = ((Class<?>) actualType).getGenericSuperclass();
		}

		List<Class<?>> actualTypeArguments = new ArrayList<>();

		for (Type actualTypeArgument : ((ParameterizedType) actualType).getActualTypeArguments()) {
			if (actualTypeArgument instanceof TypeVariable) {
				actualTypeArguments.add((Class<?>) typeMapping.get(actualTypeArgument));
			}
		}

		return unmodifiableList(actualTypeArguments);
	}

}