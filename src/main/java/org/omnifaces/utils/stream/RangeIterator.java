/*
 * Copyright 2020 OmniFaces
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

import static org.omnifaces.utils.function.Predicates.isLessThan;
import static org.omnifaces.utils.function.Predicates.isLessThanOrEqual;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

class RangeIterator<T> implements Iterator<T> {

	private T next;
	private final Predicate<T> hasNext;
	private final Function<? super T, ? extends T> incrementer;

	RangeIterator(T start, T end, boolean rangeClosed, Comparator<? super T> comparator, Function<? super T, ? extends T> incrementer) {
		this.next = start;
		this.hasNext = rangeClosed ? isLessThanOrEqual(end, comparator) : isLessThan(end, comparator);
		this.incrementer = incrementer;
	}

	@Override
	public boolean hasNext() {
		return hasNext.test(next);
	}

	@Override
	public T next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}

		T current = next;

		next = incrementer.apply(next);

		return current;
	}
}
