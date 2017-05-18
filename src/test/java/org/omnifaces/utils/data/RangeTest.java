package org.omnifaces.utils.data;

public class RangeTest extends BaseRangeTest {

	@Override
	protected <N extends Comparable<N>> Range<N> newRange(N min, N max) {
		return Range.of(min, max);
	}

}
