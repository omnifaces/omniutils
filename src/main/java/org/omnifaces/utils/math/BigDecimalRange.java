package org.omnifaces.utils.math;

import java.io.Serializable;
import java.math.BigDecimal;

public class BigDecimalRange extends Range<BigDecimal> implements Serializable {

	private static final long serialVersionUID = 1L;

	public static BigDecimalRange of(BigDecimal min, BigDecimal max) {
		return (BigDecimalRange) Range.of(BigDecimal.class, min, max);
	}

	@Override
	public boolean contains(BigDecimal number) {
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
