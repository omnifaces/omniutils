/*
 * Copyright 2020 OmniFaces
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
package org.omnifaces.utils.reflect;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableList;
import static java.util.logging.Level.FINEST;
import static org.omnifaces.utils.Lang.capitalize;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
	private static final String ERROR_MODIFY_FIELD = "Cannot modify field '%s' of class '%s' with value %s.";
	private static final String ERROR_INVOKE_METHOD = "Cannot invoke method '%s' of class '%s' with arguments %s.";
	private static final String ERROR_MAP_FIELD = "Cannot map field '%s' from %s to %s.";

	private Reflections() {
		// Hide constructor.
	}

	/**
	 * Finds a field based on the field name.
	 * @param base the object in which the field is to be found
	 * @param fieldName The name the field to be found.
	 * @return The found field, if any.
	 */
	public static Optional<Field> findField(Object base, String fieldName) {
		return findField(base != null ? base.getClass() : null, fieldName);
	}

	/**
	 * Finds a field based on the field name.
	 * @param clazz The class object for which the field is to be found.
	 * @param fieldName The name the field to be found.
	 * @return The found field, if any.
	 */
	public static Optional<Field> findField(Class<?> clazz, String fieldName) {
		for (Class<?> cls = clazz; cls != null; cls = cls.getSuperclass()) {
			for (Field field : cls.getDeclaredFields()) {
				if (field.getName().equals(fieldName)) {
					return Optional.of(field);
				}
			}
		}

		return Optional.empty();
	}

	/**
	 * Finds all fields having all of given annotations.
	 * @param clazz The class object for which the annotated fields is to be found.
	 * @param annotations The annotations of the field.
	 * @return All fields having all of given annotations.
	 * @throws IllegalArgumentException When no annotations are specified.
	 */
	@SafeVarargs
	public static List<Field> listAnnotatedFields(Class<?> clazz, Class<? extends Annotation>... annotations) {
		if (annotations.length == 0) {
			throw new IllegalArgumentException("annotations");
		}

		List<Field> annotatedFields = new ArrayList<>();

		for (Class<?> cls = clazz; cls != null; cls = cls.getSuperclass()) {
			for (Field field : cls.getDeclaredFields()) {
				if (Arrays.stream(annotations).allMatch(field::isAnnotationPresent)) {
					annotatedFields.add(field);
				}
			}
		}

		return annotatedFields;
	}

	/**
	 * Finds all enum fields having all of given annotations.
	 * @param clazz The class object for which the annotated enum fields is to be found.
	 * @param annotations The annotations of the field.
	 * @return All enum fields having all of given annotations.
	 * @throws IllegalArgumentException When no annotations are specified.
	 */
	@SafeVarargs
	@SuppressWarnings("unchecked")
	public static List<Class<? extends Enum<?>>> listAnnotatedEnumFields(Class<?> clazz, Class<? extends Annotation>... annotations) {
		if (annotations.length == 0) {
			throw new IllegalArgumentException("annotations");
		}

		List<Class<? extends Enum<?>>> annotatedEnumFields = new ArrayList<>();

		for (Field field : listAnnotatedFields(clazz, annotations)) {
			if (field.getType().isEnum()) {
				annotatedEnumFields.add((Class<? extends Enum<?>>) field.getType());
			}
			else if (field.getGenericType() instanceof ParameterizedType) {
				for (Type typeArgument : ((ParameterizedType) field.getGenericType()).getActualTypeArguments()) {
					if (typeArgument instanceof Class && ((Class<?>) typeArgument).isEnum()) {
						annotatedEnumFields.add((Class<? extends Enum<?>>) typeArgument);
					}
				}
			}
		}

		return annotatedEnumFields;
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
	 * @return The found method, if any.
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
	 * @return The found class object associated with the given class name, if any.
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
	 * @param <T> The generic object type.
	 * @param clazz The class object for which the constructor is to be found.
	 * @param parameterTypes The desired method parameter types.
	 * @return The found constructor, if any.
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
	public static <T> T accessField(Object instance, String fieldName) {
		try {
			Field field = findField(instance, fieldName).orElseThrow(NoSuchFieldException::new);
			return accessField(instance, field);
		}
		catch (Exception e) {
			throw new IllegalStateException(format(ERROR_ACCESS_FIELD, fieldName, instance != null ? instance.getClass() : null), e);
		}
	}

	/**
	 * Returns the value of the field of the given instance on the given field name.
	 * @param <T> The expected return type.
	 * @param instance The instance to access the given field on.
	 * @param field The field to be accessed on the given instance.
	 * @return The value of the field of the given instance on the given field name.
	 * @throws ClassCastException When <code>T</code> is of wrong type.
	 * @throws IllegalStateException If the field cannot be accessed.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T accessField(Object instance, Field field) {
		try {
			field.setAccessible(true);
			return (T) field.get(instance);
		}
		catch (Exception e) {
			throw new IllegalStateException(format(ERROR_ACCESS_FIELD, field != null ? field.getName() : null, instance != null ? instance.getClass() : null), e);
		}
	}

	/**
	 * Modifies the value of the field of the given instance on the given field name with the given value.
	 * @param <T> The field type.
	 * @param instance The instance to access the given field on.
	 * @param fieldName The name of the field to be accessed on the given instance.
	 * @param value The new value of the field of the given instance on the given field name.
	 * @return The old value of the field of the given instance on the given field name.
	 * @throws ClassCastException When <code>T</code> is of wrong type.
	 * @throws IllegalStateException If the field cannot be modified.
	 */
	public static <T> T modifyField(Object instance, String fieldName, T value) {
		try {
			Field field = findField(instance, fieldName).orElseThrow(NoSuchFieldException::new);
			return modifyField(instance, field, value);
		}
		catch (Exception e) {
			throw new IllegalStateException(format(ERROR_MODIFY_FIELD, fieldName, instance != null ? instance.getClass() : null), e);
		}
	}

	/**
	 * Modifies the value of the given field of the given instance with the given value.
	 * @param <T> The field type.
	 * @param instance The instance to access the given field on.
	 * @param field The field to be accessed on the given instance.
	 * @param value The new value of the given field of the given instance.
	 * @return The old value of the given field of the given instance.
	 * @throws ClassCastException When <code>T</code> is of wrong type.
	 * @throws IllegalStateException If the field cannot be modified.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T modifyField(Object instance, Field field, T value) {
		try {
			field.setAccessible(true);
			Field modifiers = Field.class.getDeclaredField("modifiers");
			modifiers.setAccessible(true);
			modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
			Object oldValue = field.get(instance);
			field.set(instance, value);
			return (T) oldValue;
		}
		catch (Exception e) {
			throw new IllegalStateException(format(ERROR_MODIFY_FIELD, field != null ? field.getName() : null, instance != null ? instance.getClass() : null, value), e);
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
	 * @throws IllegalStateException If the method cannot be invoked.
	 * @throws ClassCastException When <code>T</code> is of wrong type.
	 */
	public static <T> T invokeMethod(Object instance, String methodName, Object... parameters) {
		try {
			Method method = findMethod(instance, methodName, parameters).orElseThrow(NoSuchMethodException::new);
			return invokeMethod(instance, method, parameters);
		}
		catch (Exception e) {
			throw new IllegalStateException(format(ERROR_INVOKE_METHOD, methodName, instance != null ? instance.getClass() : null, Arrays.toString(parameters)), e);
		}
	}

	/**
	 * Invoke given method of the given instance with the given parameters and return the result.
	 * @param <T> The expected return type.
	 * @param instance The instance to invoke the given method on.
	 * @param method The method to be invoked on the given instance.
	 * @param parameters The method parameters, if any.
	 * @return The result of the method invocation, if any.
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
			throw new IllegalStateException(format(ERROR_INVOKE_METHOD, method != null ? method.getName() : null, instance != null ? instance.getClass() : null, Arrays.toString(parameters)), e);
		}
	}

	/**
	 * Invoke getter method of the given instance on the given property name and return the result.
	 * If the property name is dot-separated, then it will be invoked recursively.
	 * @param <T> The expected return type.
	 * @param instance The instance to invoke the given getter method on.
	 * @param propertyName The property name of the getter method to be invoked on the given instance.
	 * @return The result of the method invocation, if any.
	 * @throws IllegalStateException If the getter method cannot be invoked.
	 * @throws ClassCastException When <code>T</code> is of wrong type.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T invokeGetter(Object instance, String propertyName) {
		Object result = instance;

		for (String propertyNameItem : propertyName.split("\\."))
		{
			String capitalizedPropertyName = capitalize(propertyNameItem);
			Optional<Method> booleanGetter = findMethod(result, "is" + capitalizedPropertyName);

			if (booleanGetter.isPresent()) {
				result = invokeMethod(result, booleanGetter.get());
			}
			else {
				result = invokeMethod(result, "get" + capitalizedPropertyName);
			}
		}

		return (T) result;
	}

	/**
	 * Invoke setter method of the given instance on the given property name with the given property value and return the result.
	 * If the property name is dot-separated, then it will be invoked recursively.
	 * @param instance The instance to invoke the given setter method on.
	 * @param propertyName The property name of the setter method to be invoked on the given instance.
	 * @param propertyValue The property value to be set.
	 * @throws IllegalStateException If the setter method cannot be invoked.
	 */
	public static void invokeSetter(Object instance, String propertyName, Object propertyValue) {
		Object target = instance;
		String setterPropertyName = propertyName;
		int recurse = propertyName.lastIndexOf('.');

		if (recurse > 0) {
			String getterPropertyName = propertyName.substring(0, recurse);
			target = invokeGetter(target, getterPropertyName);
			setterPropertyName = propertyName.substring(recurse + 1);
		}

		String capitalizedPropertyName = capitalize(setterPropertyName);
		invokeMethod(target, "set" + capitalizedPropertyName, propertyValue);
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

	/**
	 * Returns the actual type arguments of the given subclass against which are declared on the given superclass.
	 * The returned list is ordered.
	 * @param <T> The generic superclass type.
	 * @param subclass The subclass to get the actual type arguments from.
	 * @param superclass The superclass where the generic type arguments are declared.
	 * @return The actual type arguments of the given subclass against which are declared on the given superclass.
	 */
	public static <T> List<Class<?>> getActualTypeArguments(Class<? extends T> subclass, Class<T> superclass) {
		Map<TypeVariable<?>, Type> typeMapping = new HashMap<>();
		Type actualType = subclass.getGenericSuperclass();

		while (!(actualType instanceof ParameterizedType) || !superclass.equals(((ParameterizedType) actualType).getRawType())) {
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
			actualTypeArguments.add((Class<?>) (actualTypeArgument instanceof TypeVariable ? typeMapping.get(actualTypeArgument) : actualTypeArgument));
		}

		return unmodifiableList(actualTypeArguments);
	}

}