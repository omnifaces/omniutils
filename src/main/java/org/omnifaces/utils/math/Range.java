package org.omnifaces.utils.math;

import static java.util.Comparator.naturalOrder;
import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Objects;

import org.omnifaces.utils.data.MutableRange;

@Deprecated
public abstract class Range<N extends Number & Comparable<N>> extends org.omnifaces.utils.data.AbstractRange<N> implements MutableRange<N>, Serializable {

	private static final long serialVersionUID = 1L;

	private boolean minInclusive = true;
	private boolean maxInclusive = true;

	private N min;
	private N max;

	@SuppressWarnings("unchecked")
	public static <N extends Number & Comparable<N>> Range<N> of(N min, N max) {
		if (min == null && max == null) {
			throw new NullPointerException("min and max may not be null");
		}

		Class<N> type = (Class<N>) (min != null ? min.getClass() : max.getClass());
		return of(type, min, max);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <N extends Number & Comparable<N>> Range<N> of(Class<N> type, N min, N max) {
		Range range;

		if (type.equals(Long.class)) {
			range = new LongRange();
		}
		else if (type.equals(Integer.class)) {
			range = new IntegerRange();
		}
		else if (type.equals(BigDecimal.class)) {
			range = new BigDecimalRange();
		}
		else {
			throw new UnsupportedOperationException(type + " range not supported");
		}

		range.setMin(min);
		range.setMax(max);
		return range;
	}

	private void checkNonNull(Range<?> range) {
		requireNonNull(range, "other");
		requireNonNull(range.getMin(), (range != this ? "other " : "") + "min");
		requireNonNull(range.getMax(), (range != this ? "other " : "") + "max");
	}

	@SuppressWarnings("unchecked")
	public N[] toArray() {
		checkNonNull(this);

		N[] array = (N[]) Array.newInstance(getMin() != null ? getMin().getClass() : getMax().getClass(), 2);
		array[0] = getMin();
		array[1] = getMax();
		return array;
	}



	protected int compare(N left, N right) {
		return Objects.compare(left, right, naturalOrder());
	}

	public N getMin() {
		return min;
	}

	public void setMin(N min) {
		if (min != null && max != null && compare(min, getMax()) > 0) {
			throw new IllegalArgumentException("min cannot be greater than max");
		}

		this.min = min;
	}

	public N getMax() {
		return max;
	}

	public void setMax(N max) {
		if (max != null && min != null && compare(getMin(), max) > 0) {
			throw new IllegalArgumentException("max cannot be lesser than min");
		}

		this.max = max;
	}

	protected abstract Range<N> newInstance();

	@Override
	public boolean isMinInclusive() {
		return true;
	}

	@Override
	public boolean isMaxInclusive() {
		return true;
	}

	public void setMinInclusive(boolean minInclusive) {
		this.minInclusive = minInclusive;
	}

	public void setMaxInclusive(boolean maxInclusive) {
		this.maxInclusive = maxInclusive;
	}

	@Override
	public Range<N> withMinInclusive(boolean newMinInclusive) {
		return newInstance(min, max, newMinInclusive, maxInclusive);
	}

	@Override
	public Range<N> withMaxInclusive(boolean newMaxInclusive) {
		return newInstance(min, max, minInclusive, newMaxInclusive);
	}

	public Range<N> withMin(N newMin) {
		return newInstance(newMin, max, minInclusive, maxInclusive);
	}

	public Range<N> withMax(N newMax) {
		return newInstance(min, newMax, minInclusive, maxInclusive);
	}

	private Range<N> newInstance(N min, N max, boolean minInclusive, boolean maxInclusive) {
		Range<N> newRange = newInstance();

		newRange.setMin(min);
		newRange.setMax(max);
		newRange.setMinInclusive(minInclusive);
		newRange.setMaxInclusive(maxInclusive);

		return newRange;
	}
}
