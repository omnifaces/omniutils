package org.omnifaces.utils;

import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static org.hamcrest.number.BigDecimalCloseTo.closeTo;
import static org.junit.Assert.assertThat;
import static org.omnifaces.utils.math.BigDecimalMath.nRoot;

import java.math.BigDecimal;
import java.math.MathContext;

import org.junit.Test;

public class BigDecimalMathTest {

	@Test
	public void testNRoot() {
		MathContext context = new MathContext(10);

		assertThat(nRoot(BigDecimal.valueOf(100), 2, context), closeTo(TEN, ZERO));
		assertThat(nRoot(BigDecimal.valueOf(256), 8, context), closeTo(BigDecimal.valueOf(2), ZERO));
	}
}
