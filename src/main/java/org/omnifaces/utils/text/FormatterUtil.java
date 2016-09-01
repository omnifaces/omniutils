package org.omnifaces.utils.text;

import static java.time.Instant.ofEpochMilli;
import static java.util.Collections.unmodifiableMap;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

final class FormatterUtil {

	private static final DateTimeFormatter PARSING_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("[HH:mm:ss][yyyy-MM-dd['T'HH:mm:ss[x]]");

	static final Map<String, SubFormatterFactory> DEFAULT_FORMATTER_FACTORIES;

	static {
		Map<String, SubFormatterFactory> map = new HashMap<>();

		map.put("string", SubFormatter::stringFormatter);
		map.put("number", SubFormatter::numberFormatter);
		map.put("date", SubFormatter::dateFormatter);
		map.put("time", SubFormatter::timeFormatter);
		map.put("dateTime", SubFormatter::dateTimeFormatter);
		map.put("choice", SubFormatter::choiceFormatter);
		map.put("optional", SubFormatter::optionalFormatter);
		map.put("boolean", SubFormatter::booleanFormatter);

		DEFAULT_FORMATTER_FACTORIES = unmodifiableMap(map);
	}

	private FormatterUtil() {
	}

	static Number getNumber(Object number) {
		if (number instanceof String) {
			return new BigDecimal((String) number);
		}

		if (number instanceof Number) {
			return (Number) number;
		}

		throw new IllegalArgumentException(number + " is not a number");
	}

	static TemporalAccessor getTemporalAccessor(Object object) {
		if (object instanceof TemporalAccessor) {
			return (TemporalAccessor) object;
		}

		if (object instanceof Date) {
			return ((Date) object).toInstant();
		}

		if (object instanceof Long) {
			return ofEpochMilli((Long) object);
		}

		if (object instanceof String) {
			return PARSING_DATE_TIME_FORMATTER.parseBest((String)object, ZonedDateTime::from, LocalDateTime::from, LocalDate::from, LocalTime::from);
		}

		throw new IllegalArgumentException(object + " is not of a valid temporal type");
	}

	static int firstIndexOfNonQuoted(String string, char c) {
		boolean quoted = false;
		for (int i = 0; i < string.length(); i++) {
			if (string.charAt(i) == '\'') {
				quoted = !quoted;
			}
			else if (!quoted && string.charAt(i) == c) {
				return i;
			}
		}

		return -1;
	}
}
