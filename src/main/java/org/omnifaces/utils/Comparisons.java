package org.omnifaces.utils;

import static java.util.Comparator.naturalOrder;

import java.util.Comparator;

public final class Comparisons {

	private Comparisons() {
	}

	public static <T extends Comparable<T>> T min(T t1, T t2) {
		return min(t1, t2, naturalOrder());
	}

	public static <T> T min(T t1, T t2, Comparator<? super T> comparator) {
		if (comparator.compare(t1, t2) <= 0) {
			return t1;
		}

		return t2;
	}

	public static <T extends Comparable<T>> T max(T t1, T t2) {
		return max(t1, t2, naturalOrder());
	}

	public static <T> T max(T t1, T t2, Comparator<? super T> comparator) {
		if (comparator.compare(t1, t2) >= 0) {
			return t1;
		}

		return t2;
	}

}
