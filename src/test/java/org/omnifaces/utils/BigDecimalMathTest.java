/*
 * Copyright 2021 OmniFaces
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.omnifaces.utils;

import static java.math.BigDecimal.TEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.omnifaces.utils.math.BigDecimalMath.nRoot;

import java.math.BigDecimal;
import java.math.MathContext;

import org.junit.jupiter.api.Test;

public class BigDecimalMathTest {

	@Test
	public void testNRoot() {
		MathContext context = new MathContext(10);

		assertEquals(0, nRoot(BigDecimal.valueOf(100), 2, context).compareTo(TEN));
		assertEquals(0, nRoot(BigDecimal.valueOf(256), 8, context).compareTo(BigDecimal.valueOf(2)));
	}
}
