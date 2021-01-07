/*
 * Copyright 2021 OmniFaces
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
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
