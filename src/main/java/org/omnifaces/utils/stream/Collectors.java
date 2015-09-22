package org.omnifaces.utils.stream;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;

public final class Collectors {

	private Collectors() {
	}

	public static <K, V> Collector<?, ?, ?> toMap(Function<V, K> keyMapper) {
		return java.util.stream.Collectors.toMap(keyMapper, Function.identity());
	}

	public static <T> Collector<T, ?, Void> forEachBatch(Consumer<List<T>> batchConsumer, int batchSize) {
		return new ForEachBatchCollector<>(batchConsumer, batchSize);
	}

}
