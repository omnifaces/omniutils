package org.omnifaces.utils.math;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Objects;

public abstract class Range<N extends Number> implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Comparator<Number> NUMBER_COMPARATOR = new Comparator<Number>() {

		@Override
		public int compare(Number left, Number right) {
			return (left == null || right == null) ? 0 : toBigDecimal(left).compareTo(toBigDecimal(right));
		}

		private BigDecimal toBigDecimal(Number number) {
			return new BigDecimal(number.toString());
		}

	};

	private N min;
	private N max;

	@SuppressWarnings("unchecked")
	public static <N extends Number> Range<N> of(N min, N max) {
		if (min == null && max == null) {
			throw new NullPointerException("min and max may not be null");
		}

		Class<N> type = (Class<N>) (min != null ? min.getClass() : max.getClass());
		return of(type, min, max);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <N extends Number> Range<N> of(Class<N> type, N min, N max) {
		Range range;

		if (type == Long.class) {
			range = new LongRange();
		}
		else if (type == Integer.class) {
			range = new IntegerRange();
		}
		else if (type == BigDecimal.class) {
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

	public boolean intersects(Range<?> other) {
		checkNonNull(this);
		checkNonNull(other);

		return compare(getMin(), other.getMax()) <= 0 && compare(getMax(), other.getMin()) >= 0;
	}

	private static <N extends Number> int compare(N left, N right) {
		return Objects.compare(left, right, NUMBER_COMPARATOR);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object object) {
		return (object == this) || (object instanceof Range && equals((Range<N>) object));
	}

	public boolean equals(Range<N> other) {
		return Objects.equals(getMin(), other.getMin()) && Objects.equals(getMax(), other.getMax());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getClass(), getMin(), getMax());
	}

	@Override
	public String toString() {
		return "[" + getMin() + ".." + getMax() + "]";
	}

	public N getMin() {
		return min;
	}

	public void setMin(N min) {
		if (min != null && compare(min, getMax()) > 0) {
			throw new IllegalArgumentException("min cannot be greater than max");
		}

		this.min = min;
	}

	public N getMax() {
		return max;
	}

	public void setMax(N max) {
		if (max != null && compare(getMin(), max) > 0) {
			throw new IllegalArgumentException("max cannot be lesser than min");
		}

		this.max = max;
	}

	public abstract boolean contains(N number);

}
