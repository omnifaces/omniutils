package org.omnifaces.utils.math;

import java.math.BigDecimal;
import java.math.MathContext;

public final class BigDecimalMath {

	private BigDecimalMath() {}

	public static BigDecimal nRoot(BigDecimal number, int n, MathContext context) {
		// TODO input validation
		BigDecimal power = BigDecimal.valueOf(n);

		BigDecimal previous = number;

		BigDecimal current = number.divide(power, context);

		while (previous.compareTo(current) != 0) {
			BigDecimal f = current.pow(n).subtract(number);
			BigDecimal fDerivative = power.multiply(current.pow(n - 1));

			BigDecimal next = current.subtract(
					f.divide(fDerivative, context),
					context
			);

			previous = current;
			current = next;
		}

		return current;
	}

}
