package org.omnifaces.utils.stream;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

import org.omnifaces.utils.function.Predicates;

class RangeIterator<T> implements Iterator<T> {

	private T next;
	private final Predicate<T> hasNext;
	private final Function<? super T, ? extends T> incrementer;

	RangeIterator(T startInclusive, T endExclusive, Comparator<? super T> comparator, Function<? super T, ? extends T> incrementer) {
		this.next = startInclusive;
		this.hasNext = Predicates.isLessThan(endExclusive, comparator);
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
