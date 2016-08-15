package org.omnifaces.utils.time;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.TemporalAdjusters.firstDayOfNextMonth;

import java.time.temporal.TemporalAdjuster;
import java.time.temporal.ValueRange;

public final class TemporalAdjusters {

	private TemporalAdjusters() {
	}

	public static TemporalAdjuster nextDayOfMonth(int dayOfMonth) {
		return temporal -> {
			// TODO assert that dayOfMonth is actually valid
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

}
