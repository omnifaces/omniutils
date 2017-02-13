package org.omnifaces.utils.stream;

import static java.util.stream.Collector.Characteristics.IDENTITY_FINISH;
import static java.util.stream.Collector.Characteristics.UNORDERED;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

class SummaryCollector<T> implements Collector<T, Summary<T>, Summary<T>> {

	private final Comparator<? super T> comparator;

	SummaryCollector(Comparator<? super T> comparator) {
		this.comparator = comparator;
	}

	private static class ComparableSummary<T> implements Summary<T> {
		private final Comparator<? super T> comparator;

		private T min;
		private T max;

		private long count;

		private ComparableSummary(Comparator<? super T> comparator) {
			this.comparator = comparator;
		}

		@Override
		public long getCount() {
			return count;
		}

		@Override
		public T getMin() {
			return min;
		}

		@Override
		public T getMax() {
			return max;
		}

		@Override
		public void accept(T t) {
			if (count == 0) {
				min = t;
				max = t;
			} else {
				if (Objects.compare(min, t, comparator) > 0) {
					min = t;
				}
				if (Objects.compare(t, max, comparator) > 0) {
					max = t;
				}
			}

			count++;
		}

		public void combine(Summary<T> summary) {
			if (count == 0) {
				min = summary.getMin();
				max = summary.getMax();
			}
			else {
				if (Objects.compare(min, summary.getMin(), comparator) > 0) {
					min = summary.getMin();
				}
				if (Objects.compare(summary.getMax(), max, comparator) > 0) {
					max = summary.getMax();
				}
			}

			count += summary.getCount();

		}
	}

	@Override
	public Supplier<Summary<T>> supplier() {
		return () -> new ComparableSummary<>(comparator);
	}

	@Override
	public BiConsumer<Summary<T>, T> accumulator() {
		return Summary::accept;
	}

	@Override
	public BinaryOperator<Summary<T>> combiner() {
		return (a, b) -> {
			a.combine(b);

			return a;
		};
	}

	@Override
	public Function<Summary<T>, Summary<T>> finisher() {
		return Function.identity();
	}

	@Override
	public Set<Characteristics> characteristics() {
		return EnumSet.of(IDENTITY_FINISH, UNORDERED);
	}
}
