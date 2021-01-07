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
package org.omnifaces.utils;

import static java.util.Comparator.naturalOrder;

import java.util.Comparator;

public final class Comparisons {

	private Comparisons() {
	}

	public static <T extends Comparable<T>> T min(T t1, T t2) {
		return min(naturalOrder(), t1, t2);
	}

	@SafeVarargs
	public static <T extends Comparable<T>> T min(T t1, T t2, T... ts) {
		return min(naturalOrder(), t1, t2, ts);
	}

	public static <T> T min(Comparator<? super T> comparator, T t1, T t2) {
		if (comparator.compare(t1, t2) <= 0) {
			return t1;
		}

		return t2;
	}

	@SafeVarargs
	private static <T extends Comparable<T>> T min(Comparator<T> tComparator, T t1, T t2, T... ts) {
		T min = min(tComparator, t1, t2);

		for (T t : ts) {
			min = min(tComparator, min, t);
		}

		return min;
	}

	public static <T extends Comparable<T>> T max(T t1, T t2) {
		return max(naturalOrder(), t1, t2);
	}

	@SafeVarargs
	public static <T extends Comparable<T>> T max(T t1, T t2, T... ts) {
		return max(naturalOrder(), t1, t2, ts);
	}

	public static <T> T max(Comparator<? super T> comparator, T t1, T t2) {
		if (comparator.compare(t1, t2) >= 0) {
			return t1;
		}

		return t2;
	}

	@SafeVarargs
	private static <T extends Comparable<T>> T max(Comparator<T> comparator, T t1, T t2, T... ts) {
		T max = max(comparator, t1, t2);

		for (T t : ts) {
			max = max(comparator, max, t);
		}

		return max;
	}

}
