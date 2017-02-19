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

	boolean intersects(Range<N> other);

	boolean contains(N number);

	Range<N> withMin(N min);

	Range<N> withMax(N max);

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
		return new ImmutableRangeImpl<>(min, max, false, naturalOrder());
	}

	static <N> Range<N> of(N min, N max, Comparator<? super N> comparator) {
		return new ImmutableRangeImpl<>(min, max, false, comparator);
	}

	static Range<Double> ofDouble(double min, double max) {
		return new ImmutableRangeImpl<>(min, max, false, naturalOrder());
	}

	static Range<Integer> ofInteger(int min, int max) {
		return new ImmutableRangeImpl<>(min, max, false, naturalOrder());
	}

	static Range<Long> ofLong(long min, long max) {
		return new ImmutableRangeImpl<>(min, max, false, naturalOrder());
	}

	static <N extends Number & Comparable<N>> Range<N> ofClosed(N min, N max) {
		return new ImmutableRangeImpl<>(min, max, true, naturalOrder());
	}

	static <N> Range<N> ofClosed(N min, N max, Comparator<? super N> comparator) {
		return new ImmutableRangeImpl<>(min, max, true, comparator);
	}

	static Range<Double> ofDoubleClosed(double min, double max) {
		return new ImmutableRangeImpl<>(min, max, true, naturalOrder());
	}

	static Range<Integer> ofIntegerClosed(int min, int max) {
		return new ImmutableRangeImpl<>(min, max, true, naturalOrder());
	}

	static Range<Long> ofLongClosed(long min, long max) {
		return new ImmutableRangeImpl<>(min, max, true, naturalOrder());
	}


}
