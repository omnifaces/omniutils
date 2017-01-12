package org.omnifaces.utils.math;

import java.io.Serializable;

public class IntegerRange extends Range<Integer> implements Serializable {

	private static final long serialVersionUID = 1L;

	public static IntegerRange of(Integer min, Integer max) {
		return (IntegerRange) Range.of(min, max);
	}

}
