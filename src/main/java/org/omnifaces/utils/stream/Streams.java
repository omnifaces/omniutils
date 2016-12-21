package org.omnifaces.utils.stream;

import static java.util.Comparator.naturalOrder;
import static java.util.Spliterator.DISTINCT;
import static java.util.Spliterator.IMMUTABLE;
import static java.util.Spliterator.NONNULL;
import static java.util.Spliterator.ORDERED;
import static java.util.Spliterator.SORTED;
import static java.util.Spliterators.spliteratorUnknownSize;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Streams {

	private static class ZippedIterator<T, U, R> implements Iterator<R> {

		private final Iterator<? extends T> iterator1;

		private final Iterator<? extends U> iterator2;
		private final BiFunction<? super T, ? super U, R> zipFunction;
		public ZippedIterator(Iterator<? extends T> iterator1, Iterator<? extends U> iterator2,
				BiFunction<? super T, ? super U, R> zipFunction) {
			this.iterator1 = iterator1;
			this.iterator2 = iterator2;
			this.zipFunction = zipFunction;
		}

		@Override
		public boolean hasNext() {
			return iterator1.hasNext() || iterator2.hasNext();
		}

		@Override
		public R next() {
			return zipFunction.apply(iterator1.next(), iterator2.next());
		}

	}
	public static <T, U, R> Stream<R> zip(Stream<? extends T> stream1, Stream<? extends U> stream2, BiFunction<? super T, ? super U, R> zipFunction) {
		Spliterator<? extends T> spliterator1 = stream1.spliterator();
		Spliterator<? extends U> spliterator2 = stream2.spliterator();


		ZippedIterator<T, U, R> zippedIterator = new ZippedIterator<>(Spliterators.iterator(spliterator1), Spliterators.iterator(spliterator2), zipFunction);

		// TODO determine and set flags
		return StreamSupport.stream(spliteratorUnknownSize(zippedIterator, 0), false);
	}


	public static <T extends Comparable<T>> Stream<T> rangeClosed(T start, T endInclusive, Function<? super T, ? extends T> incrementer) {
		return rangeClosed(start, endInclusive, incrementer, Comparator.naturalOrder());
	}

	public static <T> Stream<T> rangeClosed(T start, T endInclusive, Function<? super T, ? extends T> incrementer, Comparator<? super T> comparator) {
		return rangeStream(start, endInclusive, true, incrementer, comparator);
	}

	public static <T extends Comparable<T>> Stream<T> range(T start, T endExclusive, Function<? super T, ? extends T> incrementer) {
		return range(start, endExclusive, incrementer, naturalOrder());
	}

	public static <T> Stream<T> range(T start, T endExclusive, Function<? super T, ? extends T> incrementer, Comparator<? super T> comparator) {
		return rangeStream(start, endExclusive, false, incrementer, comparator);
	}

	private static <T> Stream<T> rangeStream(T start, T endExclusive, boolean rangeClosed, Function<? super T, ? extends T> incrementer,
			Comparator<? super T> comparator) {
		Iterator<T> iterator = new RangeIterator<>(start, endExclusive, rangeClosed, comparator, incrementer);

		Spliterator<T> spliterator = spliteratorUnknownSize(iterator, ORDERED | SORTED | DISTINCT | NONNULL | IMMUTABLE);

		return StreamSupport.stream(spliterator, false);
	}

	/**
	 * Returns a {@link java.util.stream.Stream#flatMap(Function) flatMap} {@link java.util.function.Function} that only retains a instances of a given type and casts them to this type.
	 *
	 * <p>
	 *     Unlike other flatMap functions, this function will only return 0 or 1 result. If an instance passed to it
	 *     is of the specified type, then the function will return a {@link java.util.stream.Stream} with only this item, cast to this type.
	 *     If the instance is not of this type, the function will return {@link java.util.stream.Stream#empty()}.
	 *
	 * <b>Example use</b>
	 * Say we have a Stream&lt;X&gt; from which we want retain only all instances of Y, then could do the following to
	 * obtain a Stream&lt;Y&gt;:
	 * <code>
	 *    Stream&lt;X&gt; streamOfX = ...;
	 *    Stream&lt;Y&gt; streamOfY = streamOfX.flatMap(mapToType(Y.class));
	 * </code>
	 *
	 * Which is the equivalent of this:
	 * <code>
	 *     streamOfX.filter(x -&gt; x instanceof Y)
	 *              .map(x -&gt; (Y)x)
	 * </code>
	 * </p>
	 *
	 * @param clazz the type of the instances to retain
	 * @param <T> the type of the elements in the stream
	 * @param <R> the type of the instances to retain
	 * @return a flatMap function that only retains instances of a given type.
	 */
	public static <T, R extends T> Function<T, Stream<R>> mapToType(Class<R> clazz) {
		return t -> {
			if (clazz.isInstance(t)) {
				return Stream.of(clazz.cast(t));
			}

			return Stream.empty();
		};
	}
}
