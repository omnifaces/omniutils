package org.omnifaces.utils.data;

public class MutableRangeTest extends BaseRangeTest {

	@Override
	protected <N extends Comparable<N>> Range<N> newRange(N min, N max) {
		return MutableRange.of(min, max);
	}

}
