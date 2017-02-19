package org.omnifaces.utils.stream;

import java.util.function.Consumer;

public interface Summary<T> extends Consumer<T> {

	long getCount();

	T getMin();

	T getMax();

	void combine(Summary<T> summary);

}
