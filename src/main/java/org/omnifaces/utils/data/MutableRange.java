package org.omnifaces.utils.data;

import static java.util.Comparator.naturalOrder;

import java.util.Comparator;

public interface MutableRange<N> extends Range<N> {
	void setMin(N min);

	void setMax(N max);

	static <N extends Comparable<N>> MutableRange<N> of(N min, N max) {
		return new MutableRangeImpl<>(min, max, true, false, naturalOrder());
	}

	static <N> MutableRange<N> of(N min, N max, Comparator<? super N> comparator) {
		return new MutableRangeImpl<>(min, max, true, false, comparator);
	}

	static MutableRange<Double> ofDouble(double min, double max) {
		return new MutableRangeImpl<>(min, max, true, false, naturalOrder());
	}

	static MutableRange<Integer> ofInteger(int min, int max) {
		return new MutableRangeImpl<>(min, max, true, false, naturalOrder());
	}

	static MutableRange<Long> ofLong(long min, long max) {
		return new MutableRangeImpl<>(min, max, true, false, naturalOrder());
	}

	static <N extends Comparable<N>> MutableRange<N> ofClosed(N min, N max) {
		return new MutableRangeImpl<>(min, max, true, true, naturalOrder());
	}

	static <N> MutableRange<N> ofClosed(N min, N max, Comparator<? super N> comparator) {
		return new MutableRangeImpl<>(min, max, true, true, comparator);
	}

	static MutableRange<Double> ofDoubleClosed(double min, double max) {
		return new MutableRangeImpl<>(min, max, true, true, naturalOrder());
	}

	static MutableRange<Integer> ofIntegerClosed(int min, int max) {
		return new MutableRangeImpl<>(min, max, true, true, naturalOrder());
	}

	static MutableRange<Long> ofLongClosed(long min, long max) {
		return new MutableRangeImpl<>(min, max, true, true, naturalOrder());
	}
}
