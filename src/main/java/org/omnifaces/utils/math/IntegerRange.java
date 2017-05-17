package org.omnifaces.utils.math;

import java.io.Serializable;

@Deprecated
public class IntegerRange extends Range<Integer> implements Serializable {

	private static final long serialVersionUID = 1L;

	public static IntegerRange of(Integer min, Integer max) {
		return (IntegerRange) Range.of(Integer.class, min, max);
	}

	@Override
	protected IntegerRange newInstance() {
		return new IntegerRange();
	}
	
	@Override
	public org.omnifaces.utils.data.Range withMin(Integer min) {
		return of(min, getMax());
	}

	@Override
	public org.omnifaces.utils.data.Range withMax(Integer max) {
		return of(getMin(), max);
	}
}
