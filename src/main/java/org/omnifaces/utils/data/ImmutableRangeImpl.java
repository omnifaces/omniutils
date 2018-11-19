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

class ImmutableRangeImpl<N> extends AbstractRange<N> {

    private static final long serialVersionUID = -1899797137173600162L;
    
    private final N min;
	private final N max;
	private final boolean minInclusive;
	private final boolean maxInclusive;
	private final Comparator<? super N> comparator;

	ImmutableRangeImpl(N min, N max, boolean minInclusive, boolean maxInclusive, Comparator<? super N> comparator) {
		this.min = requireNonNull(min);
		this.max = requireNonNull(max);
		this.minInclusive = minInclusive;
		this.maxInclusive = maxInclusive;
		this.comparator = requireNonNull(comparator);
	}

	@Override
	public N getMin() {
		return min;
	}

	@Override
	public N getMax() {
		return max;
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
	protected int compare(N left, N right) {
		return Objects.compare(left, right, comparator);
	}

	@Override
	public Range<N> withMin(N newMin) {
		return new ImmutableRangeImpl<>(requireNonNull(newMin), max, minInclusive, maxInclusive, comparator);
	}

	@Override
	public Range<N> withMax(N newMax) {
		return new ImmutableRangeImpl<>(min, requireNonNull(newMax), minInclusive, maxInclusive, comparator);
	}

	@Override
	public Range<N> withMinInclusive(boolean newMinInclusive) {
		return new ImmutableRangeImpl<>(min, max, newMinInclusive, maxInclusive, comparator);
	}
	
	@Override
	public Range<N> withMaxInclusive(boolean newMaxInclusive) {
		return new ImmutableRangeImpl<>(min, max, minInclusive, newMaxInclusive, comparator);
	}
}
