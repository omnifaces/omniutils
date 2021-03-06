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

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.Objects;

public abstract class AbstractRange<N> implements Serializable, Range<N> {

	private static final long serialVersionUID = 1L;

	private void checkNonNull(Range<?> range) {
		requireNonNull(range, "other");
		requireNonNull(range.getMin(), (range != this ? "other " : "") + "min");
		requireNonNull(range.getMax(), (range != this ? "other " : "") + "max");
	}

	@Override
	public boolean intersects(Range<N> other) {
		checkNonNull(this);
		checkNonNull(other);

		boolean intersects;
		if (!isMinInclusive() || !other.isMaxInclusive()) {
			intersects = compare(getMin(), other.getMax()) < 0;
		}
		else {
			intersects = compare(getMin(), other.getMax()) <= 0;
		}

		if (!isMaxInclusive() || !other.isMinInclusive()) {
			intersects &= compare(getMax(), other.getMin()) > 0;
		}
		else {
			intersects &= compare(getMax(), other.getMin()) >= 0;
		}

		return intersects;
	}

	protected abstract int compare(N left, N right);

	@Override
	public boolean contains(N value) {
		int minComparison = getMin() == null ? -1 : compare(getMin(), value);
		int maxComparison = getMax() == null ? 1 : compare(getMax(), value);

		return (isMinInclusive() ? minComparison <= 0 : minComparison < 0) && (isMaxInclusive() ? maxComparison >= 0 : maxComparison > 0);
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}

		if (object instanceof Range) {
			Range<?> other = (Range<?>) object;

			return Objects.equals(getMin(), other.getMin()) &&
					Objects.equals(getMax(), other.getMax())&&
					Objects.equals(isMinInclusive(), other.isMinInclusive()) &&
					Objects.equals(isMaxInclusive(), other.isMaxInclusive());
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getClass(), getMin(), getMax(), isMinInclusive(), isMaxInclusive());
	}

	@Override
	public String toString() {
		return (isMinInclusive() ? "(" : "[") + getMin() + ".." + getMax() + (isMaxInclusive() ? ")" :"]");
	}

}
