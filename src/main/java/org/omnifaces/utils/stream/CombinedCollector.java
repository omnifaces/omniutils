package org.omnifaces.utils.stream;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

// TODO find proper pair option
class CombinedCollector<T, I1, I2, R1, R2> implements Collector<T, Map.Entry<I1, I2>, Map.Entry<R1, R2>> {

	private final Collector<T, I1, R1> collector1;
	private final Collector<T, I2, R2> collector2;

	CombinedCollector(Collector<T, I1, R1> collector1, Collector<T, I2, R2> collector2) {
		this.collector1 = collector1;
		this.collector2 = collector2;
	}

	@Override
	public Supplier<Map.Entry<I1, I2>> supplier() {
		return () -> new AbstractMap.SimpleEntry<>(collector1.supplier().get(), collector2.supplier().get());
	}

	@Override
	public BiConsumer<Map.Entry<I1, I2>, T> accumulator() {
		BiConsumer<I1, T> accumulator1 = collector1.accumulator();
		BiConsumer<I2, T> accumulator2 = collector2.accumulator();

		return (pair, element) -> {
			accumulator1.accept(pair.getKey(), element);
			accumulator2.accept(pair.getValue(), element);
		};
	}

	@Override
	public BinaryOperator<Map.Entry<I1, I2>> combiner() {
		BinaryOperator<I1> combiner1 = collector1.combiner();
		BinaryOperator<I2> combiner2 = collector2.combiner();

		return (pair1, pair2) -> {
			I1 i1 = combiner1.apply(pair1.getKey(), pair2.getKey());
			I2 i2 = combiner2.apply(pair1.getValue(), pair2.getValue());

			return new AbstractMap.SimpleEntry<>(i1, i2);
		};
	}

	@Override
	public Function<Map.Entry<I1, I2>, Map.Entry<R1, R2>> finisher() {
		Function<I1, R1> finisher1 = collector1.finisher();
		Function<I2, R2> finisher2 = collector2.finisher();

		return (pair) -> {
			R1 r1 = finisher1.apply(pair.getKey());
			R2 r2 = finisher2.apply(pair.getValue());

			return new AbstractMap.SimpleEntry<>(r1, r2);
		};
	}

	@Override
	public Set<Characteristics> characteristics() {
		// TODO correctly determine elements
		return Collections.emptySet();
	}
}
