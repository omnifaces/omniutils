package org.omnifaces.utils.stream;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
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
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(zippedIterator, 0), false);
	}
}
