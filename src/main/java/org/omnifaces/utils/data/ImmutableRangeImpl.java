package org.omnifaces.utils.data;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.Objects;

class ImmutableRangeImpl<N> extends AbstractRange<N> {

	private final N min;
	private final N max;
	private final boolean minInclusive;
	private final boolean maxInclusive;
	private final Comparator<? super N> comparator;

	ImmutableRangeImpl(N min, N max, boolean minInclusive, boolean maxInclusive, Comparator<? super N> comparator) {
		this.min = requireNonNull(min);
		this.max = requireNonNull(max);
		this.minInclusive = minInclusive;
		this.maxInclusive = maxInclusive;
		this.comparator = requireNonNull(comparator);
	}

	@Override
	public N getMin() {
		return min;
	}

	@Override
	public N getMax() {
		return max;
	}

	@Override
	public boolean isMinInclusive() {
		return minInclusive;
	}

	@Override
	public boolean isMaxInclusive() {
		return maxInclusive;
	}

	@Override
	protected int compare(N left, N right) {
		return Objects.compare(left, right, comparator);
	}

	@Override
	public Range<N> withMin(N newMin) {
		return new ImmutableRangeImpl<>(requireNonNull(newMin), max, minInclusive, maxInclusive, comparator);
	}

	@Override
	public Range<N> withMax(N newMax) {
		return new ImmutableRangeImpl<>(min, requireNonNull(newMax), minInclusive, maxInclusive, comparator);
	}

	@Override
	public Range<N> withMinInclusive(boolean newMinInclusive) {
		return new ImmutableRangeImpl<>(min, max, newMinInclusive, maxInclusive, comparator);
	}
	
	@Override
	public Range<N> withMaxInclusive(boolean newMaxInclusive) {
		return new ImmutableRangeImpl<>(min, max, minInclusive, newMaxInclusive, comparator);
	}
}
