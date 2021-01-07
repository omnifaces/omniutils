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
package org.omnifaces.utils.data;

import static java.util.Comparator.naturalOrder;

import java.util.Comparator;

public interface MutableRange<N> extends Range<N> {
	void setMin(N min);

	void setMax(N max);

	static <N extends Comparable<N>> MutableRange<N> of(N min, N max) {
		return new MutableRangeImpl<>(min, max, true, false, naturalOrder());
	}

	static <N> MutableRange<N> of(N min, N max, Comparator<? super N> comparator) {
		return new MutableRangeImpl<>(min, max, true, false, comparator);
	}

	static MutableRange<Double> ofDouble(double min, double max) {
		return new MutableRangeImpl<>(min, max, true, false, naturalOrder());
	}

	static MutableRange<Integer> ofInteger(int min, int max) {
		return new MutableRangeImpl<>(min, max, true, false, naturalOrder());
	}

	static MutableRange<Long> ofLong(long min, long max) {
		return new MutableRangeImpl<>(min, max, true, false, naturalOrder());
	}

	static <N extends Comparable<N>> MutableRange<N> ofClosed(N min, N max) {
		return new MutableRangeImpl<>(min, max, true, true, naturalOrder());
	}

	static <N> MutableRange<N> ofClosed(N min, N max, Comparator<? super N> comparator) {
		return new MutableRangeImpl<>(min, max, true, true, comparator);
	}

	static MutableRange<Double> ofDoubleClosed(double min, double max) {
		return new MutableRangeImpl<>(min, max, true, true, naturalOrder());
	}

	static MutableRange<Integer> ofIntegerClosed(int min, int max) {
		return new MutableRangeImpl<>(min, max, true, true, naturalOrder());
	}

	static MutableRange<Long> ofLongClosed(long min, long max) {
		return new MutableRangeImpl<>(min, max, true, true, naturalOrder());
	}
}
