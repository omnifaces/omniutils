package org.omnifaces.utils.stream;

import static java.util.Collections.emptySet;

import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

class FindLastCollector<T> implements Collector<T, FindLastCollector.LastEncounteredElemement<T>, Optional<T>> {

	static class LastEncounteredElemement<T> {
		private boolean set;
		private T element;

		void nextElement(T nextElement) {
			set = true;
			element = nextElement;
		}

		LastEncounteredElemement<T> combine(LastEncounteredElemement<T> other) {
			if (other.set) {
				return other;
			}

			return this;
		}

		Optional<T> toOptional() {
			if (set) {
				return Optional.of(element);
			}

			return Optional.empty();
		}
	}

	@Override
	public Supplier<LastEncounteredElemement<T>> supplier() {
		return LastEncounteredElemement::new;
	}

	@Override
	public BiConsumer<LastEncounteredElemement<T>, T> accumulator() {
		return LastEncounteredElemement::nextElement;
	}

	@Override
	public BinaryOperator<LastEncounteredElemement<T>> combiner() {
		return LastEncounteredElemement::combine;
	}

	@Override
	public Function<LastEncounteredElemement<T>, Optional<T>> finisher() {
		return LastEncounteredElemement::toOptional;
	}

	@Override
	public Set<Characteristics> characteristics() {
		return emptySet();
	}
}
