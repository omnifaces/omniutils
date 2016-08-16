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
