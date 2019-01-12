/*
 * Copyright 2019 OmniFaces
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
package org.omnifaces.utils.text;

import static java.util.Collections.emptyMap;
import static java.util.Locale.ENGLISH;
import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;

public class NameBasedMessageFormatTest {

	@Test
	public void testString() {
		assertEquals("Test", format("Test", emptyMap()));
		assertEquals("Test'", format("Test''", emptyMap()));
		assertEquals("Test{0,number,currency}", format("Test'{0,number,currency}'", emptyMap()));
		assertEquals("{'}", format("'{''}'", emptyMap()));
	}

	@Test
	public void testWithToStringParameter() {
		Map<String, Object> parameters = buildParameters();
		assertEquals("Test15", new NameBasedMessageFormat("Test{integer}").format(parameters));
	}

	private Map<String, Object> buildParameters() {
		Map<String, Object> parameters = new HashMap<>();

		parameters.put("integer", 15);
		parameters.put("double", 16.5);
		parameters.put("long", Long.MAX_VALUE);
		parameters.put("integerAsString", "" + 54321);
		parameters.put("doubleAsString", "" + 15.6);
		parameters.put("firstOfJanuary2015", LocalDate.of(2015, 1, 1));
		parameters.put("404Time", LocalTime.of(16, 4, 42));
		parameters.put("localDateTime", LocalDateTime.of(2020, 12, 1, 15, 35, 12, 4));
		parameters.put("zonedDateTime", ZonedDateTime.of(2015, 2, 24, 15, 47, 58, 0, ZoneId.of("Europe/Amsterdam")));
		parameters.put("isoLocalDate", "2011-12-03");
		parameters.put("isoLocalTime", "10:15:30");
		parameters.put("isoLocalDateTime", "2012-01-15T11:12:13");
		parameters.put("isoOffsetDateTime", "2009-05-14T15:16:17-0900");
		parameters.put("isoInstant", "2014-11-11T05:06:07Z");
		parameters.put("zero", "0");
		parameters.put("one", "1");
		parameters.put("ten", "10");

		return parameters;
	}

	@Test
	public void testWithNumberParameter() {
		Map<String, Object> parameters = buildParameters();

		assertEquals("Test 15", format("Test {integer,number}", parameters));
		assertEquals("Test 16", format("Test {double,number,integer}", parameters));
		Locale locale = new Locale("nl", "NL");

		assertEquals("Test 54.321", format("Test {integerAsString,number,integer}", locale, parameters));
		assertEquals("Test € 16,50", format("Test {double,number,currency}", locale, parameters));
		assertEquals("Test 1.650%", format("Test {double,number,percent}", locale, parameters));

		assertEquals("Test 1.6,5", format("Test {double,number,#,#.#}", locale, parameters));
	}


	@Test
	public void testWithDates() {
		Map<String, Object> parameters = buildParameters();
		Locale locale = new Locale("nl", "NL");

		assertEquals("Test 1-jan-2015", format("Test {firstOfJanuary2015,date}", locale, parameters));
		assertEquals("Test 1-1-15", format("Test {firstOfJanuary2015,date,short}", locale, parameters));
		assertEquals("Test 1-jan-2015", format("Test {firstOfJanuary2015,date,medium}", locale, parameters));
		assertEquals("Test 1 januari 2015", format("Test {firstOfJanuary2015,date,long}", locale, parameters));
		assertEquals("Test donderdag 1 januari 2015", format("Test {firstOfJanuary2015,date,full}", locale, parameters));

		assertEquals("Test 2015-01-01", format("Test {firstOfJanuary2015,date,yyyy-MM-dd}", locale, parameters));

		assertEquals("Test zaterdag 3 december 2011", format("Test {isoLocalDate,date,full}", locale, parameters));
	}

	@Test
	public void testWithTimes() {
		Map<String, Object> parameters = buildParameters();
		Locale locale = new Locale("nl", "NL");

		assertEquals("Test 16:04:42", format("Test {404Time,time}", locale, parameters));
		assertEquals("Test 16:04", format("Test {404Time,time,short}", locale, parameters));
		assertEquals("Test 16:04:42", format("Test {404Time,time,medium}", locale, parameters));
		assertEquals("Test 15:47:58 CET", format("Test {zonedDateTime,time,long}", locale, parameters));
		assertEquals("Test 15:47:58 uur CET", format("Test {zonedDateTime,time,full}", locale, parameters));

		assertEquals("Test 404", format("Test {404Time,time,hmm}", locale, parameters));

		assertEquals("Test 10:15:30", format("Test {isoLocalTime,time,medium}", locale, parameters));
		assertEquals("Test 15:16:17 uur -09:00", format("Test {isoOffsetDateTime,time,full}", locale, parameters));
	}

	@Test
	public void testWithDateTimes() {
		Map<String, Object> parameters = buildParameters();
		Locale locale = new Locale("nl", "NL");

		assertEquals("Test 24-feb-2015 15:47:58", format("Test {zonedDateTime,dateTime}", locale, parameters));
		assertEquals("Test 24-2-15 15:47", format("Test {zonedDateTime,dateTime,short}", locale, parameters));
		assertEquals("Test 24-feb-2015 15:47:58", format("Test {zonedDateTime,dateTime,medium}", locale, parameters));
		assertEquals("Test 24 februari 2015 15:47:58 CET", format("Test {zonedDateTime,dateTime,long}", locale, parameters));
		assertEquals("Test dinsdag 24 februari 2015 15:47:58 uur CET", format("Test {zonedDateTime,dateTime,full}", locale, parameters));

		assertEquals("Test 1-dec-2020 15:35:12", format("Test {localDateTime,dateTime,medium}", locale, parameters));
		assertEquals("Test donderdag 14 mei 2009 15:16:17 uur -09:00", format("Test {isoOffsetDateTime,dateTime,full}", locale, parameters));
		assertEquals("Test 15-jan-2012 11:12:13", format("Test {isoLocalDateTime,dateTime,medium}", locale, parameters));
	}

	@Test
	public void testChoiceFormat() {
		Map<String, Object> parameters = buildParameters();
		Locale locale = new Locale("nl", "NL");

		assertEquals("Test ZERO", format("Test {zero,choice,0#ZERO|1#ONE|1<Number {zero,number,'#,#'}}", locale, parameters));
		assertEquals("Test ONE", format("Test {one,choice,0#ZERO|1#ONE|1<Number {one,number,'#,#'}}", locale, parameters));
		assertEquals("Test Number 1.0", format("Test {ten,choice,0#ZERO|1#ONE|1<Number {ten,number,'#,#'}}", locale, parameters));
		assertEquals("Test Number 1.6", format("Test {double,choice,0#ZERO|1#ONE|1<Number {double,number,'#,#'}}", locale, parameters));

		NameBasedMessageFormat messageFormat = new NameBasedMessageFormat("Test {double,choice,0#ZERO|0<Date {zonedDateTime,dateTime,full}}", ENGLISH);
		assertEquals("Test Date Tuesday, February 24, 2015 3:47:58 PM CET", messageFormat.format(parameters));
		assertEquals("Test Date dinsdag 24 februari 2015 15:47:58 uur CET", messageFormat.withLocale(locale).format(parameters));
	}

	@Test
	public void testOptionalFormat() {
		Map<String, Object> parameters = buildParameters();

		assertEquals("Is missing", format("{zero1,optional,Is missing}", parameters));
		assertEquals("0", format("{zero,optional,Is missing}", parameters));

		assertEquals("Number 0%", format("{zero,optional,Is missing|Number {zero,number,percent}}", ENGLISH, parameters));
		assertEquals("", format("{zero,optional,Is missing|}", ENGLISH, parameters));

		assertEquals("Is missing |", format("{zero1,optional,Is missing '|'|Number {zero1,number,percent}}", ENGLISH, parameters));
	}

	@Test
	public void testEscapeSequences() {
		Map<String, Object> parameters = buildParameters();

		assertEquals("{zero1,optional,Is missing}", format("'{zero1,optional,Is missing}'", parameters));
		assertEquals("'Is missing", format("''{zero1,optional,Is missing}", parameters));
		assertEquals("{zero1,optional,'Is missing}", format("'{zero1,optional,''Is missing}'", parameters));
	}

	private String format(String formatPattern, Locale locale, Map<String, Object> parameters) {
		return new NameBasedMessageFormat(formatPattern, locale).format(parameters);
	}

	private String format(String formatPattern, Map<String, Object> parameters) {
		return new NameBasedMessageFormat(formatPattern).format(parameters);
	}
}
