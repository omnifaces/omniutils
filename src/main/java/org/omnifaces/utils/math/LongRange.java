package org.omnifaces.utils.math;

import java.io.Serializable;

public class LongRange extends Range<Long> implements Serializable {

	private static final long serialVersionUID = 1L;

	public static LongRange of(Long min, Long max) {
		return (LongRange) Range.of(Long.class, min, max);
	}

}
