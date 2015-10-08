package org.omnifaces.utils.stream;

import static java.util.function.Function.identity;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;

public final class Collectors {

	private Collectors() {
	}
	
	public static <T, K> Collector<T, ?, Map<K, T>> toMap(Function<? super T, ? extends K> keyMapper) {
		return java.util.stream.Collectors.toMap(keyMapper, identity());
	}

	public static <T> Collector<T, ?, Void> forEachBatch(Consumer<List<T>> batchConsumer, int batchSize) {
		return new ForEachBatchCollector<>(batchConsumer, batchSize);
	}

	public static <T, R1, R2> Collector<T, ?, Map.Entry<R1, R2>> combine(Collector<T, ?, R1> collector1, Collector<T, ?, R2> collector2) {
		return new CombinedCollector<>(collector1, collector2);
	}

}
