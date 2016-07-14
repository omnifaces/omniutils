package org.omnifaces.utils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
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
	 * Returns <code>true</code> if the given value is null or is empty. Types of String, Collection, Map, Optional and Array are
	 * recognized. If none is recognized, then examine the emptiness of the toString() representation instead.
	 *
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
		else if (value instanceof Optional<?>) {
			return !((Optional<?>)value).isPresent();
		}
		else if (value.getClass().isArray()) {
			return Array.getLength(value) == 0;
		}
		else {
			return value.toString() == null || value.toString().isEmpty();
		}
	}

	/**
	 * Returns true if all values are empty, false if at least one value is not empty.
	 * @param values the values to be checked on emptiness
	 * @return True if all values are empty, false otherwise
	 */
	public static boolean isAllEmpty(Object... values) {
		for (Object value : values) {
			if (!isEmpty(value)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Returns <code>true</code> if at least one value is empty.
	 *
	 * @param values the values to be checked on emptiness
	 * @return <code>true</code> if any value is empty and <code>false</code> if no values are empty
	 */
	public static boolean isAnyEmpty(Object... values) {
		for (Object value : values) {
			if (isEmpty(value)) {
				return true;
			}
		}

		return false;
	}

	public static <T, E extends Exception> T requireNotEmpty(T value, Supplier<E> exceptionSupplier) throws E {
		if (isEmpty(value)) {
			throw exceptionSupplier.get();
		}

		return value;
	}

	public static <T> T ifEmptyGet(T value, Supplier<T> defaultSupplier) {
		if (isEmpty(value)) {
			return defaultSupplier.get();
		}

		return value;
	}

	/**
	 * Call the given setter with the given value if {@link #isEmpty(Object)} returns <code>false</code> for the given value.
	 *
	 * @param value the value to set
	 * @param setter the setter to use
	 * @param <T> the generic type of the value
	 */
	public static <T> void ifNotEmptySet(T value, Consumer<? super T> setter) {
		if (!isEmpty(value)) {
			setter.accept(value);
		}
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

	/**
	 * Returns <code>true</code> if the given string contains any ISO control characters.
	 *
	 * @param string the string to check for control characters
	 * @return <code>true</code> if the string contains any ISO control characters and <code>false</code> otherwise
	 */
	public static boolean containsIsoControlCharacters(String string) {
		return string.codePoints().anyMatch(Character::isISOControl);
	}
}
