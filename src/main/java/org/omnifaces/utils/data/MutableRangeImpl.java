package org.omnifaces.utils.data;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.Objects;

class MutableRangeImpl<N> extends AbstractRange<N> implements MutableRange<N> {

	private N min;
	private N max;

	private boolean minInclusive;
	private boolean maxInclusive;

	private final Comparator<? super N> comparator;

	MutableRangeImpl(N min, N max, boolean minInclusive, boolean maxInclusive, Comparator<? super N> comparator) {
		this.min = min;
		this.max = max;
		this.minInclusive = minInclusive;
		this.maxInclusive = maxInclusive;
		this.comparator = comparator;
	}

	@Override
	public N getMin() {
		return min;
	}

	@Override
	public void setMin(N min) {
		this.min = min;
	}

	@Override
	public N getMax() {
		return max;
	}

	@Override
	public void setMax(N max) {
		this.max = max;
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
	public Range<N> withMin(N newMin) {
		return new MutableRangeImpl<>(requireNonNull(newMin), max, minInclusive, maxInclusive, comparator);
	}

	@Override
	public Range<N> withMax(N newMax) {
		return new MutableRangeImpl<>(min, requireNonNull(newMax), minInclusive, maxInclusive, comparator);
	}

	@Override
	public Range<N> withMinInclusive(boolean newMinInclusive) {
		return new MutableRangeImpl<>(min, max, newMinInclusive, maxInclusive, comparator);
	}

	@Override
	public Range<N> withMaxInclusive(boolean newMaxInclusive) {
		return new MutableRangeImpl<>(min, max, minInclusive, newMaxInclusive, comparator);
	}

	@Override
	protected int compare(N left, N right) {
		return Objects.compare(left, right, comparator);
	}
}
