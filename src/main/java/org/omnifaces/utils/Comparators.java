package org.omnifaces.utils;

import java.util.Comparator;
import java.util.function.Predicate;

public final class Comparators {

	private Comparators() {
	}

	/**
	 * Create a new {@link Comparator} that places values that match the given {@link Predicate} before values that do not.
	 *
	 * If either both values match the predicate or both do not match the predicate, the order is determined by the given comparator.
	 *
	 * @param predicate the predicate to use to sort the values
	 * @param comparator the comparator to use to sort any pairs of values that either both do or do not match the predicate
	 * @param <T> the type of the values
	 *
	 * @return the new comparator
	 */
	public static <T> Comparator<T> firstWhen(Predicate<? super T> predicate, Comparator<? super T> comparator) {
		return firstOrLastWhen(true, predicate, comparator);
	}

	/**
	 * Create a new {@link Comparator} that places values that match the given {@link Predicate} after  values that do not.
	 *
	 * If either both values match the predicate or both do not match the predicate, the order is determined by the given comparator.
	 *
	 * @param predicate the predicate to use to sort the values
	 * @param comparator the comparator to use to sort any pairs of values that either both do or do not match the predicate
	 * @param <T> the type of the values
	 *
	 * @return the new comparator
	 */
	public static <T> Comparator<T> lastWhen(Predicate<? super T> predicate, Comparator<? super T> comparator) {
		return firstOrLastWhen(false, predicate, comparator);
	}

	private static <T> Comparator<T> firstOrLastWhen(boolean matchesFirst, Predicate<? super T> predicate, Comparator<? super T> comparator) {
		final int direction = matchesFirst ? -1 : 1;

		return (t1, t2) -> {
			boolean t1Matches = predicate.test(t1);
			boolean t2Matches = predicate.test(t2);

			if (t1Matches != t2Matches) {
				return t1Matches ? direction : -1 * direction;
			}

			return comparator.compare(t1, t2);
		};
	}
}
