package org.omnifaces.utils.math;

import java.io.Serializable;

public class LongRange extends Range<Long> implements Serializable {

	private static final long serialVersionUID = 1L;

	public static LongRange of(Long min, Long max) {
		return (LongRange) Range.of(Long.class, min, max);
	}


	@Override
	public boolean contains(Long number) {
		if (getMin() != null) {
			if (getMin().compareTo(number) > 0) {
				return false;
			}
		}

		if (getMax() != null) {
			if (getMax().compareTo(number) < 0) {
				return false;
			}
		}

		return true;
	}
}
