/*
 * Copyright 2020 OmniFaces
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
package org.omnifaces.utils.time;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.TemporalAdjusters.firstDayOfNextMonth;

import java.time.temporal.TemporalAdjuster;
import java.time.temporal.ValueRange;

public final class TemporalAdjusters {

	private TemporalAdjusters() {
	}

	public static TemporalAdjuster nextDayOfMonth(int dayOfMonth) {
		validateDayOfMonth(dayOfMonth);

		return temporal -> {
			int currentDayOfMonth = temporal.get(DAY_OF_MONTH);

			if(currentDayOfMonth >= dayOfMonth || temporal.range(DAY_OF_MONTH).getMaximum() == currentDayOfMonth) {
				temporal = temporal.with(firstDayOfNextMonth());
			}

			ValueRange dayRange = temporal.range(DAY_OF_MONTH);

			int newDayOfMonth = dayOfMonth;

			if (dayRange.getMaximum() < dayOfMonth) {
				newDayOfMonth = (int) dayRange.getMaximum();
			}

			return temporal.with(DAY_OF_MONTH, newDayOfMonth);
		};
	}

	public static TemporalAdjuster nextOrSameDayOfMonth(int dayOfMonth) {
		validateDayOfMonth(dayOfMonth);

		TemporalAdjuster nextDayOfMonth = nextDayOfMonth(dayOfMonth);

		return temporal -> {
			int currentDayOfMonth = temporal.get(DAY_OF_MONTH);

			if (currentDayOfMonth == dayOfMonth || (currentDayOfMonth < dayOfMonth && currentDayOfMonth == temporal.range(DAY_OF_MONTH).getMaximum())) {
				return temporal;
			}

			return temporal.with(nextDayOfMonth);
		};
	}

	private static void validateDayOfMonth(int dayOfMonth) {
		DAY_OF_MONTH.checkValidValue(dayOfMonth);
	}
}
