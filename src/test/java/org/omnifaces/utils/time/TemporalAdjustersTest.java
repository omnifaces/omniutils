package org.omnifaces.utils.time;

import static org.junit.Assert.assertEquals;
import static org.omnifaces.utils.time.TemporalAdjusters.nextDayOfMonth;
import static org.omnifaces.utils.time.TemporalAdjusters.nextOrSameDayOfMonth;

import java.time.LocalDate;

import org.junit.Test;

public class TemporalAdjustersTest {

	@Test
	public void testNextDayOfMonth() {
		assertEquals(LocalDate.of(2016, 1, 31), LocalDate.of(2016, 1, 1).with(nextDayOfMonth(31)));

		assertEquals(LocalDate.of(2016, 3, 31), LocalDate.of(2016, 2, 29).with(nextDayOfMonth(31)));

		assertEquals(LocalDate.of(2015, 2, 28), LocalDate.of(2015, 2, 21).with(nextDayOfMonth(31)));

		assertEquals(LocalDate.of(2016, 2, 29), LocalDate.of(2016, 2, 21).with(nextDayOfMonth(31)));

		assertEquals(LocalDate.of(2016, 4, 30), LocalDate.of(2016, 3, 31).with(nextDayOfMonth(31)));

		assertEquals(LocalDate.of(2016, 1, 1), LocalDate.of(2015, 12, 1).with(nextDayOfMonth(1)));
	}

	@Test
	public void testNextOrSameDayOfMonth() {
		assertEquals(LocalDate.of(2016, 1, 31), LocalDate.of(2016, 1, 1).with(nextOrSameDayOfMonth(31)));

		assertEquals(LocalDate.of(2016, 2, 29), LocalDate.of(2016, 2, 29).with(nextOrSameDayOfMonth(31)));

		assertEquals(LocalDate.of(2015, 2, 28), LocalDate.of(2015, 2, 21).with(nextOrSameDayOfMonth(31)));

		assertEquals(LocalDate.of(2016, 2, 29), LocalDate.of(2016, 2, 21).with(nextOrSameDayOfMonth(31)));

		assertEquals(LocalDate.of(2016, 4, 30), LocalDate.of(2016, 3, 31).with(nextOrSameDayOfMonth(30)));

		assertEquals(LocalDate.of(2016, 3, 31), LocalDate.of(2016, 3, 31).with(nextOrSameDayOfMonth(31)));

		assertEquals(LocalDate.of(2015, 12, 1), LocalDate.of(2015, 12, 1).with(nextOrSameDayOfMonth(1)));
	}
}
