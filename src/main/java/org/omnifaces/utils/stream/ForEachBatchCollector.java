/*
 * Copyright 2019 OmniFaces
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.omnifaces.utils.stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

class ForEachBatchCollector<T> implements Collector<T, List<T>, Void> {

	private final Consumer<List<T>> batchConsumer;
	private final int batchSize;

	ForEachBatchCollector(Consumer<List<T>> batchConsumer, int batchSize) {
		if (batchSize <= 0) {
			throw new IllegalArgumentException("Batch size must be greater than 0");
		}

		this.batchConsumer = batchConsumer;
		this.batchSize = batchSize;
	}


	@Override
	public Supplier<List<T>> supplier() {
		return ArrayList::new;
	}

	@Override
	public BiConsumer<List<T>, T> accumulator() {
		return (list, element) -> {
			list.add(element);

			if (list.size() == batchSize) {
				batchConsumer.accept(list);
				list.clear();
			}
		};
	}

	@Override
	public BinaryOperator<List<T>> combiner() {
		return (list1, list2) -> {
			if (list1.size() + list2.size() < batchSize) {
				list1.addAll(list2);
				return list1;
			}

			while (list1.size() < batchSize) {
				list1.add(list2.remove(0));
			}

			batchConsumer.accept(list1);

			return list2;
		};
	}

	@Override
	public Function<List<T>, Void> finisher() {
		return (list) -> {
			if (!list.isEmpty()) {
				batchConsumer.accept(list);
			}

			return null;
		};
	}

	@Override
	public Set<Characteristics> characteristics() {
		return Collections.emptySet();
	}
}
