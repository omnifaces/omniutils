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
package org.omnifaces.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class Collections {

	private Collections() {
	}

	/**
	 * Creates an unmodifiable set based on the given values. If one of the values is an instance of an array or a
	 * collection, then each of its values will also be merged into the set. Nested arrays or collections will result
	 * in a {@link ClassCastException}.
	 * @param <E> The expected set element type.
	 * @param values The values to create an unmodifiable set for.
	 * @return An unmodifiable set based on the given values.
	 * @throws ClassCastException When one of the values or one of the arrays or collections is of wrong type.
	 */
	@SuppressWarnings("unchecked")
	public static <E> Set<E> unmodifiableSet(Object... values) {
		Set<E> set = new HashSet<>();

		for (Object value : values) {
			if (value instanceof Object[]) {
				for (Object item : (Object[]) value) {
					set.add((E) item);
				}
			}
			else if (value instanceof Collection<?>) {
				for (Object item : (Collection<?>) value) {
					set.add((E) item);
				}
			}
			else {
				set.add((E) value);
			}
		}

		return java.util.Collections.unmodifiableSet(set);
	}

	/**
	 * Converts an iterable into a list.
	 * <p>
	 * This method makes NO guarantee to whether changes to the source iterable are
	 * reflected in the returned list or not. For instance if the given iterable
	 * already is a list, it's returned directly.
	 *
	 * @param <E> The generic iterable element type.
	 * @param iterable The iterable to be converted.
	 * @return The list representation of the given iterable, possibly the same instance as that iterable.
	 */
	public static <E> List<E> iterableToList(Iterable<E> iterable) {

		List<E> list = null;

		if (iterable instanceof List) {
			list = (List<E>) iterable;
		} else if (iterable instanceof Collection) {
			list = new ArrayList<>((Collection<E>) iterable);
		} else {
			list = new ArrayList<>();
			Iterator<E> iterator = iterable.iterator();
			while (iterator.hasNext()) {
				list.add(iterator.next());
			}
		}

		return list;
	}

	/**
	 * Returns a new map that contains the reverse of the given map.
	 * <p>
	 * The reverse of a map means that every value X becomes a key X' with as corresponding
	 * value Y' the key Y that was originally associated with the value X.
	 *
	 * @param <T> The generic map key/value type.
	 * @param source the map that is to be reversed
	 * @return the reverse of the given map
	 */
	public static <T> Map<T, T> reverse(Map<T, T> source) {
		Map<T, T> target = new HashMap<>();
		for (Entry<T, T> entry : source.entrySet()) {
			target.put(entry.getValue(), entry.getKey());
		}

		return target;
	}

	/**
	 * Returns <tt>true</tt> if the collection is not null and contains the specified element.
	 *
	 * @param collection the collection to test for the specified element
	 * @param object element to test for in the specified collection
	 * @return <tt>true</tt> if the collection is not null and contains the specified element
	 */
	public static boolean contains(Collection<?> collection, Object object) {
		return collection != null && collection.contains(object);
	}
}
