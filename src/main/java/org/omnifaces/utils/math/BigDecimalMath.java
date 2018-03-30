/*
 * Copyright 2018 OmniFaces
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
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
