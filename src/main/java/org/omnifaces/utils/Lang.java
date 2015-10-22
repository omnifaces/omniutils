package org.omnifaces.utils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

public final class Lang {

	private Lang() {
	}

	/**
	 * Returns <code>true</code> if the given string is null or is empty.
	 *
	 * @param string The string to be checked on emptiness.
	 * @return <code>true</code> if the given string is null or is empty.
	 */
	public static boolean isEmpty(String string) {
		return string == null || string.isEmpty();
	}

	/**
	 * Returns <code>true</code> if the given array is null or is empty.
	 *
	 * @param array The array to be checked on emptiness.
	 * @return <code>true</code> if the given array is null or is empty.
	 */
	public static boolean isEmpty(Object[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * Returns <code>true</code> if the given collection is null or is empty.
	 *
	 * @param collection The collection to be checked on emptiness.
	 * @return <code>true</code> if the given collection is null or is empty.
	 */
	public static boolean isEmpty(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}

	/**
	 * Returns <code>true</code> if the given map is null or is empty.
	 *
	 * @param map The map to be checked on emptiness.
	 * @return <code>true</code> if the given map is null or is empty.
	 */
	public static boolean isEmpty(Map<?, ?> map) {
		return map == null || map.isEmpty();
	}

	/**
	 * Returns <code>true</code> if the given value is null or is empty. Types of String, Collection, Map and Array are
	 * recognized. If none is recognized, then examine the emptiness of the toString() representation instead.
	 * @param value The value to be checked on emptiness.
	 * @return <code>true</code> if the given value is null or is empty.
	 */
	public static boolean isEmpty(Object value) {
		if (value == null) {
			return true;
		}
		else if (value instanceof String) {
			return ((String) value).isEmpty();
		}
		else if (value instanceof Collection<?>) {
			return ((Collection<?>) value).isEmpty();
		}
		else if (value instanceof Map<?, ?>) {
			return ((Map<?, ?>) value).isEmpty();
		}
		else if (value.getClass().isArray()) {
			return Array.getLength(value) == 0;
		}
		else {
			return value.toString() == null || value.toString().isEmpty();
		}
	}

	public static <T, E extends Exception> T requireNotEmpty(T value, Supplier<E> exceptionSupplier) throws E {
		if (isEmpty(value)) {
			throw exceptionSupplier.get();
		}

		return value;
	}

	/**
	 * Returns the first non-<code>null</code> object of the argument list, or <code>null</code> if there is no such element.
	 *
	 * @param <T> The generic object type.
	 * @param objects The argument list of objects to be tested for non-<code>null</code>.
	 * @return The first non-<code>null</code> object of the argument list, or <code>null</code> if there is no such element.
	 */
	@SafeVarargs
	public static <T> T coalesce(T... objects) {
		for (T object : objects) {
			if (object != null) {
				return object;
			}
		}

		return null;
	}

	/**
	 * Returns <code>true</code> if the given object equals one of the given objects.
	 *
	 * @param <T> The generic object type.
	 * @param object The object to be checked if it equals one of the given objects.
	 * @param objects The argument list of objects to be tested for equality.
	 * @return <code>true</code> if the given object equals one of the given objects.
	 */
	@SafeVarargs
	public static <T> boolean isOneOf(T object, T... objects) {
		for (Object other : objects) {
			if (object == null ? other == null : object.equals(other)) {
				return true;
			}
		}

		return false;
	}
}
