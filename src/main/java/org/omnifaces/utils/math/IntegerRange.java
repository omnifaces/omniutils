package org.omnifaces.utils.math;

import java.io.Serializable;

public class IntegerRange extends Range<Integer> implements Serializable {

	private static final long serialVersionUID = 1L;

	public static IntegerRange of(Integer min, Integer max) {
		return (IntegerRange) Range.of(Integer.class, min, max);
	}

	@Override
	public boolean contains(Integer number) {
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
