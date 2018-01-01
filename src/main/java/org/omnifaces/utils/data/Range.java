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
package org.omnifaces.utils.data;

import static java.util.Comparator.naturalOrder;
import static java.util.Objects.requireNonNull;
import static java.util.Spliterator.DISTINCT;
import static java.util.Spliterator.IMMUTABLE;
import static java.util.Spliterator.NONNULL;
import static java.util.Spliterator.ORDERED;
import static java.util.Spliterator.SORTED;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Range<N> {

	N getMin();

	N getMax();

	boolean isMinInclusive();

	boolean isMaxInclusive();

	/**
	 * Check if the given range intersects the current range.
	 *
	 * @param other
	 * 		the range to check against the current range
	 *
	 * @return <code>true</code> if both ranges intersect and <code>false</code> otherwise
	 */
	boolean intersects(Range<N> other);

	/**
	 * Check if a given value is contained within this range
	 *
	 * @param value
	 * 		the value to check
	 *
	 * @return <code>true</code> if the value is contained by this range and <code>false</code> otherwise
	 */
	boolean contains(N value);

	/**
	 * Create a copy of the current range with the given min value.
	 *
	 * @param min
	 * 		the new min value for the copy
	 *
	 * @return a copy of the current range
	 */
	Range<N> withMin(N min);

	/**
	 * Create a copy of the current range with the given max value.
	 *
	 * @param max
	 * 		the new max value for the copy
	 *
	 * @return a copy of the current range
	 */
	Range<N> withMax(N max);

	/**
	 * Create a copy of the current range where the min value will be either inclusive or exclusive.
	 *
	 * @param minInclusive
	 * 		boolean indicating if the min value should be inclusive or exclusive
	 *
	 * @return a copy of the current range
	 */
	Range<N> withMinInclusive(boolean minInclusive);

	/**
	 * Create a copy of the current range where the max value will be either inclusive or exclusive.
	 *
	 * @param maxInclusive
	 * 		boolean indicating if the max value should be inclusive or exclusive
	 *
	 * @return a copy of the current range
	 */
	Range<N> withMaxInclusive(boolean maxInclusive);

	/**
	 * Return a {@link Stream} containing all values contained by the range, with a given incrementer.
	 * <p>
	 * This method should not return the min or max value if they are exclusive.
	 * <p>
	 * The default implementation will apply the incrementer repeatedly on the current value, starting with the min value and will continue
	 * as long as {@link Range#contains(Object)} returns true.
	 *
	 * @param incrementer
	 * 		the incrementer to use to determine the next value
	 *
	 * @return a stream containing all values within the range
	 */
	default Stream<N> stream(UnaryOperator<N> incrementer) {
		N min = requireNonNull(getMin());

		N start = contains(min) ? min : incrementer.apply(min);

		Iterator<N> iterator = new Iterator<N>() {

			private N next = start;

			@Override
			public boolean hasNext() {
				return contains(next);
			}

			@Override
			public N next() {
				N n = next;

				next = incrementer.apply(next);

				return n;
			}
		};

		Spliterator<N> spliterator = Spliterators.spliteratorUnknownSize(iterator, DISTINCT | IMMUTABLE | NONNULL | ORDERED | SORTED);

		return StreamSupport.stream(spliterator, false);
	}

	static <N extends Comparable<N>> Range<N> of(N min, N max) {
		return new ImmutableRangeImpl<>(min, max, true, false, naturalOrder());
	}

	static <N> Range<N> of(N min, N max, Comparator<? super N> comparator) {
		return new ImmutableRangeImpl<>(min, max, true, false, comparator);
	}

	static Range<Double> ofDouble(double min, double max) {
		return new ImmutableRangeImpl<>(min, max, true, false, naturalOrder());
	}

	static Range<Integer> ofInteger(int min, int max) {
		return new ImmutableRangeImpl<>(min, max, true, false, naturalOrder());
	}

	static Range<Long> ofLong(long min, long max) {
		return new ImmutableRangeImpl<>(min, max, true, false, naturalOrder());
	}

	static <N extends Comparable<N>> Range<N> ofClosed(N min, N max) {
		return new ImmutableRangeImpl<>(min, max, true, true, naturalOrder());
	}

	static <N> Range<N> ofClosed(N min, N max, Comparator<? super N> comparator) {
		return new ImmutableRangeImpl<>(min, max, true, true, comparator);
	}

	static Range<Double> ofDoubleClosed(double min, double max) {
		return new ImmutableRangeImpl<>(min, max, true, true, naturalOrder());
	}

	static Range<Integer> ofIntegerClosed(int min, int max) {
		return new ImmutableRangeImpl<>(min, max, true, true, naturalOrder());
	}

	static Range<Long> ofLongClosed(long min, long max) {
		return new ImmutableRangeImpl<>(min, max, true, true, naturalOrder());
	}


}
