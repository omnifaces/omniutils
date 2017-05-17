package org.omnifaces.utils.math;

import java.io.Serializable;

@Deprecated
public class LongRange extends Range<Long> implements Serializable {

	private static final long serialVersionUID = 1L;

	public static LongRange of(Long min, Long max) {
		return (LongRange) Range.of(Long.class, min, max);
	}

	@Override
	protected LongRange newInstance() {
		return new LongRange();
	}
	
	@Override
	public org.omnifaces.utils.data.Range withMin(Long min) {
		return of(min, getMax());
	}

	@Override
	public org.omnifaces.utils.data.Range withMax(Long max) {
		return of(getMin(), max);
	}

}
