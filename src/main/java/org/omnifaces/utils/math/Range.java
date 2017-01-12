package org.omnifaces.utils.math;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Objects;

public abstract class Range<N extends Number> implements Serializable {

	private static final long serialVersionUID = 1L;

	private N min;
	private N max;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <N extends Number> Range<N> of(N min, N max) {
		checkNonNull(min, max);

		if (toBigDecimal(min).compareTo(toBigDecimal(max)) > 0) {
			throw new IllegalArgumentException("min cannot be greater than max");
		}

		Range range;

		if (min instanceof Long) {
			range = new LongRange();
		}
		else if (min instanceof Integer) {
			range = new IntegerRange();
		}
		else {
			throw new UnsupportedOperationException(min.getClass() + " range not supported");
		}

		range.setMin(min);
		range.setMax(max);
		return range;
	}

	private static void checkNonNull(Object min, Object max) {
		requireNonNull(min, "min");
		requireNonNull(max, "max");
	}

	private static BigDecimal toBigDecimal(Number number) {
		return new BigDecimal(number.toString());
	}

	@SuppressWarnings("unchecked")
	public N[] toArray() {
		checkNonNull(getMin(), getMax());

		N[] array = (N[]) Array.newInstance(getMin() != null ? getMin().getClass() : getMax().getClass(), 2);
		array[0] = getMin();
		array[1] = getMax();
		return array;
	}

	public boolean intersects(Range<?> other) {
		requireNonNull(other, "other");
		checkNonNull(getMin(), getMax());
		checkNonNull(other.getMin(), other.getMax());

		return toBigDecimal(getMin()).compareTo(toBigDecimal(other.getMax())) <= 0
			&& toBigDecimal(getMax()).compareTo(toBigDecimal(other.getMin())) >= 0;
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
		this.min = min;
	}

	public N getMax() {
		return max;
	}

	public void setMax(N max) {
		this.max = max;
	}

}
