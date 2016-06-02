package org.omnifaces.utils.stream;

import static java.util.Collections.emptySet;

import java.util.LinkedList;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

class ReversedStreamCollector<T> implements Collector<T, LinkedList<T>, Stream<T>> {

	@Override
	public Supplier<LinkedList<T>> supplier() {
		return LinkedList::new;
	}

	@Override
	public BiConsumer<LinkedList<T>, T> accumulator() {
		return LinkedList::addFirst;
	}

	@Override
	public BinaryOperator<LinkedList<T>> combiner() {
		return (t1, t2) -> {
			t2.addAll(t1);

			return t2;
		};
	}

	@Override
	public Function<LinkedList<T>, Stream<T>> finisher() {
		return LinkedList::stream;
	}

	@Override
	public Set<Characteristics> characteristics() {
		return emptySet();
	}

}
