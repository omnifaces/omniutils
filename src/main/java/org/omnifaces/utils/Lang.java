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

import static java.lang.Character.isSpaceChar;
import static java.lang.Character.toLowerCase;
import static java.lang.Character.toUpperCase;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
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

	public static boolean isNotBlank(String string) {
		return string != null && !string.trim().isEmpty();
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
	 * @param setter a consumer that calls the setter with the value
	 * @param <T> the generic type of the value
	 */
	public static <T> void setIfNotEmpty(T value, Consumer<? super T> setter) {
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
	 * Returns <code>true</code> if the given string starts with one of the given prefixes.
	 * @param string The string to be checked if it starts with one of the given prefixes.
	 * @param prefixes The argument list of prefixes to be checked.
	 * @return <code>true</code> if the given string starts with one of the given prefixes.
	 */
	public static boolean startsWithOneOf(String string, String... prefixes) {
		for (String prefix : prefixes) {
			if (string.startsWith(prefix)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns <code>true</code> if the given string ends with one of the given suffixes.
	 * @param string The string to be checked if it ends with one of the given suffixes.
	 * @param suffixes The argument list of suffixes to be checked.
	 * @return <code>true</code> if the given string ends with one of the given suffixes.
	 */
	public static boolean endsWithOneOf(String string, String... suffixes) {
		for (String suffix : suffixes) {
			if (string.endsWith(suffix)) {
				return true;
			}
		}

		return false;
	}

	/**
     * Replaces the last substring of given string that matches the given regular expression with the given replacement.
	 * @param string The string to be replaced.
	 * @param regex The regular expression to which given string is to be matched.
	 * @param replacement The string to be substituted for the last match.
	 * @return The resulting string.
	 * @author http://stackoverflow.com/a/2282998
	 */
    public static String replaceLast(String string, String regex, String replacement) {
        return string.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
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

	/**
	 * Converts the first character of given string to upper case.
	 * @param string String to be capitalized.
	 * @return The given string capitalized.
	 */
	public static String capitalize(String string) {
		if (string == null || string.isEmpty()) {
			return string;
		}

		return toUpperCase(string.charAt(0)) + string.substring(1);
	}

	/**
	 * Converts given string to title case.
	 * @param string String to be converted to title case.
	 * @return The given string converted to title case.
	 */
	public static String toTitleCase(String string) {
		if (string == null) {
			return null;
		}

		return string.codePoints().collect(StringBuilder::new, (sb, cp) -> {
			sb.appendCodePoint(sb.length() == 0 || isSpaceChar(sb.charAt(sb.length() - 1)) ? toUpperCase(cp) : toLowerCase(cp));
		}, (sb1, sb2) -> {}).toString();
	}

	/**
	 * Converts given string to URL safe format, also called a "slug".
	 * @param string String to be converted to URL safe format.
	 * @return The given string converted to URL safe format.
	 */
	public static String toUrlSafe(String string) {
		if (string == null) {
			return null;
		}

		return Normalizer.normalize(string.trim(), Form.NFD)
			.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
			.replaceAll("[^\\p{Alnum}]+", "-")
			.replaceAll("(^-|-$)", "");
	}

	/**
	 * Escape given string as valid {@link Properties} entry value.
	 * @param string String to be escaped as valid {@link Properties} entry value.
	 * @return The given string escaped as valid {@link Properties} entry value.
	 * @throws IOException When appending a character fails.
	 */
	public static String escapeAsProperty(String string) throws IOException {
		Appendable builder = new StringBuilder(string.length());

		for (char c : string.toCharArray()) {
			if ((c > 61) && (c < 127)) {
				if (c == '\\') {
					builder.append('\\');
					builder.append('\\');
					continue;
				}
				builder.append(c);
				continue;
			}
			switch(c) {
				case '\t':builder.append('\\'); builder.append('t');
						  break;
				case '\n':builder.append('\\'); builder.append('n');
						  break;
				case '\r':builder.append('\\'); builder.append('r');
						  break;
				case '\f':builder.append('\\'); builder.append('f');
						  break;
				case '=': // Fall through
				case ':':
					builder.append('\\'); builder.append(c);
					break;
				default:
					if ((c < 0x0020) || (c > 0x007e)) {
						builder.append(String.format("\\u%04x", (int) c));
					} else {
						builder.append(c);
					}
			}
		}

		return builder.toString();
	}

}