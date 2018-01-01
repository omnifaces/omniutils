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
package org.omnifaces.utils.function;

import static java.util.Comparator.naturalOrder;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import org.omnifaces.utils.Lang;

public final class Predicates {

	private Predicates() {
	}

	public static <T> Predicate<T> always() {
		return t -> true;
	}

	public static <T> Predicate<T> never() {
		return t -> false;
	}

	public static <T> Predicate<T> isEmpty() {
		return Lang::isEmpty;
	}

	public static <T> Predicate<T> isNotEmpty() {
		return Predicates.<T>isEmpty().negate();
	}

	public static <T> Predicate<T> isNull() {
		return t -> t == null;
	}

	public static <T> Predicate<T> isNotNull() {
		return t -> t != null;
	}

	public static <T extends Comparable<T>> Predicate<T> isLessThan(T value) {
		return isLessThan(value, naturalOrder());
	}

	public static <T> Predicate<T> isLessThan(T value, Comparator<? super T> comparator) {
		Objects.requireNonNull(value);
		Objects.requireNonNull(comparator);

		return t -> comparator.compare(t, value) < 0;
	}

	public static <T extends Comparable<T>> Predicate<T> isLessThanOrEqual(T value) {
		return isLessThanOrEqual(value, naturalOrder());
	}

	public static <T> Predicate<T> isLessThanOrEqual(T value, Comparator<? super T> comparator) {
		Objects.requireNonNull(value);
		Objects.requireNonNull(comparator);

		return t -> comparator.compare(t, value) <= 0;
	}

	public static <T extends Comparable<T>> Predicate<T> isComparativelyEqualTo(T value) {
		return isComparativelyEqualTo(value, naturalOrder());
	}

	public static <T extends Comparable<T>> Predicate<T> isComparativelyEqualTo(T value, Comparator<? super T> comparator) {
		Objects.requireNonNull(value);
		Objects.requireNonNull(comparator);

		return t -> comparator.compare(t, value) == 0;
	}

	public static <T extends Comparable<T>> Predicate<T> isGreaterThan(T value) {
		return isGreaterThan(value, naturalOrder());
	}

	public static <T> Predicate<T> isGreaterThan(T value, Comparator<? super T> comparator) {
		Objects.requireNonNull(value);
		Objects.requireNonNull(comparator);

		return t -> comparator.compare(t, value) > 0;
	}

	public static <T extends Comparable<T>> Predicate<T> isGreaterThanOrEqual(T value) {
		return isGreaterThanOrEqual(value, naturalOrder());
	}

	public static <T> Predicate<T> isGreaterThanOrEqual(T value, Comparator<? super T> comparator) {
		Objects.requireNonNull(value);
		Objects.requireNonNull(comparator);

		return t -> comparator.compare(t, value) >= 0;
	}

	public static <T, R> Predicate<T> mapped(Function<? super T, R> function, Predicate<? super R> predicate) {
		return t -> predicate.test(function.apply(t));
	}
}
