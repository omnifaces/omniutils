/*
 * Copyright 2018 OmniFaces
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
package org.omnifaces.utils.data;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.Objects;

class MutableRangeImpl<N> extends AbstractRange<N> implements MutableRange<N> {

	private N min;
	private N max;

	private boolean minInclusive;
	private boolean maxInclusive;

	private final Comparator<? super N> comparator;

	MutableRangeImpl(N min, N max, boolean minInclusive, boolean maxInclusive, Comparator<? super N> comparator) {
		this.min = min;
		this.max = max;
		this.minInclusive = minInclusive;
		this.maxInclusive = maxInclusive;
		this.comparator = comparator;
	}

	@Override
	public N getMin() {
		return min;
	}

	@Override
	public void setMin(N min) {
		this.min = min;
	}

	@Override
	public N getMax() {
		return max;
	}

	@Override
	public void setMax(N max) {
		this.max = max;
	}

	@Override
	public boolean isMinInclusive() {
		return minInclusive;
	}

	@Override
	public boolean isMaxInclusive() {
		return maxInclusive;
	}

	@Override
	public Range<N> withMin(N newMin) {
		return new MutableRangeImpl<>(requireNonNull(newMin), max, minInclusive, maxInclusive, comparator);
	}

	@Override
	public Range<N> withMax(N newMax) {
		return new MutableRangeImpl<>(min, requireNonNull(newMax), minInclusive, maxInclusive, comparator);
	}

	@Override
	public Range<N> withMinInclusive(boolean newMinInclusive) {
		return new MutableRangeImpl<>(min, max, newMinInclusive, maxInclusive, comparator);
	}

	@Override
	public Range<N> withMaxInclusive(boolean newMaxInclusive) {
		return new MutableRangeImpl<>(min, max, minInclusive, newMaxInclusive, comparator);
	}

	@Override
	protected int compare(N left, N right) {
		return Objects.compare(left, right, comparator);
	}
}
