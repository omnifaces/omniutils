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
package org.omnifaces.utils.text;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static org.omnifaces.utils.text.FormatterUtil.DEFAULT_FORMATTER_FACTORIES;

import java.io.IOException;
import java.io.StringReader;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class NameBasedMessageFormat extends Format {

	private static final long serialVersionUID = -4520307378273079056L;

	private final String pattern;
	private final List<Function<Map<String, ?>, String>> segmentFunctions;
	private final Map<String, SubFormatterFactory> subFormatterFactories;
	private final Locale locale;

	public NameBasedMessageFormat(String messagePattern) {
		this(messagePattern, Locale.getDefault());
	}

	public NameBasedMessageFormat(String pattern, Locale locale) {
		this(pattern, locale, DEFAULT_FORMATTER_FACTORIES);
	}

	public NameBasedMessageFormat(String pattern, Locale locale, Map<String, SubFormatterFactory> subFormatterFactories) {
		this.pattern = pattern;
		this.segmentFunctions = parsePattern(pattern, locale, subFormatterFactories);
		this.subFormatterFactories = copyToUnmodifiableMap(subFormatterFactories);
		this.locale = locale;
	}

	@Override
	@SuppressWarnings("unchecked")
	public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
		if (obj instanceof Map) {
			format((Map<String, ?>)obj, toAppendTo::append);

			return toAppendTo;
		}

		throw new IllegalArgumentException("Can only format map-based arguments");
	}

	public String format(Map<String, ?> parameters) {
		StringBuilder builder = new StringBuilder();

		format(parameters, builder::append);

		return builder.toString();
	}

	public NameBasedMessageFormat withLocale(Locale locale) {
		if (this.locale.equals(locale)) {
			return this;
		}

		return new NameBasedMessageFormat(pattern, locale, subFormatterFactories);
	}

	public NameBasedMessageFormat withPattern(String pattern) {
		if (this.pattern.equals(pattern)) {
			return this;
		}

		return new NameBasedMessageFormat(pattern, locale, subFormatterFactories);
	}

	@Override
	public Object parseObject(String source, ParsePosition pos) {
		throw new UnsupportedOperationException();
	}

	public String getPattern() {
		return pattern;
	}

	public Map<String, SubFormatterFactory> getSubFormatterFactories() {
		return subFormatterFactories;
	}

	public Locale getLocale() {
		return locale;
	}

	public static String format(String pattern, Map<? super String, ?> parameters) {
		return format(pattern, parameters, Locale.getDefault());
	}

	public static String format(String pattern, Map<? super String, ?> parameters, Locale locale) {
		return new NameBasedMessageFormat(pattern, locale).format(parameters);
	}

	public static String format(String pattern, Map<? super String, ?> parameters, Locale locale, Map<String, SubFormatterFactory> formatterFactories) {
		return new NameBasedMessageFormat(pattern, locale, formatterFactories).format(parameters);
	}

	private void format(Map<String, ?> parameters, Consumer<String> toAppendTo) {
		segmentFunctions.stream()
						.map(function -> function.apply(parameters))
						.forEachOrdered(toAppendTo);
	}

	private static List<Function<Map<String, ?>, String>> parsePattern(String pattern, Locale locale, Map<String, SubFormatterFactory> formatterFactories) {
		List<Function<Map<String, ?>, String>> segmentFunctions = new ArrayList<>();

		try (StringReader reader = new StringReader(pattern)) {
			int peek;
			while ((peek = peek(reader)) >= 0) {
				char nextChar = (char) peek;

				if (nextChar == '{') {
					segmentFunctions.add(parseFormat(reader, locale, formatterFactories));
				}
				else {
					segmentFunctions.add(readText(reader));
				}

			}
		}
		catch (IOException e) {
			throw new IllegalArgumentException("Illegal pattern format", e);
		}

		return unmodifiableList(segmentFunctions);
	}

	private static Function<Map<String, ?>, String> readText(StringReader reader) throws IOException {
		int peek;

		StringBuilder builder = new StringBuilder();

		while ((peek = peek(reader)) >= 0 && peek != '{') {
			char c = (char) reader.read();

			if (c == '\'') {
				builder.append(readQuotedString(reader));
			}
			else {
				builder.append(c);
			}

		}

		String s = builder.toString();

		return parameters -> s;
	}

	private static String readQuotedString(StringReader reader) throws IOException {
		StringBuilder builder = new StringBuilder();
		int read;
		boolean quoted = true;

		while ((read = reader.read()) >= 0 && quoted) {

			if (read == '\'') {
				int peek = peek(reader);

				if (builder.length() == 0 && peek != '\'') {
					// Just an escaped single quote
					builder.append('\'');
					quoted = false;

					return builder.toString();
				}
				else if (peek == '\'') {
					reader.skip(1);
					builder.append('\'');
				}
				else {
					quoted = false;
				}
			}
			else {
				builder.append((char) read);
			}

		}

		return builder.toString();
	}

	private static Function<Map<String, ?>, String> parseFormat(StringReader reader, Locale locale, Map<String, SubFormatterFactory> subFormatterFactories)
			throws IOException {
		if (reader.read() != '{') {
			throw new IllegalStateException("Not at the start of a format specification");
		}

		StringBuilder parameterNameBuilder = new StringBuilder();
		int read;
		while ((read = reader.read()) >= 0 && read != '}' && read != ',') {
			if (read == '\'') {
				parameterNameBuilder.append(readQuotedString(reader));
			}
			else {
				parameterNameBuilder.append((char) read);
			}
		}

		String parameterName = parameterNameBuilder.toString();

		String format;
		if (read == ',') {
			StringBuilder formatBuilder = new StringBuilder();

			while ((read = reader.read()) >= 0 && read != '}' && read != ',') {
				formatBuilder.append((char) read);
			}

			format = formatBuilder.toString();
		}
		else {
			format = "string";
		}

		String modifier = null;
		if (read == ',') {
			StringBuilder modifierBuilder = new StringBuilder();
			int bracketDepth = 0;
			boolean inQuotes = false;
			while ((read = reader.read()) >= 0 && (read != '}' || bracketDepth > 0 || inQuotes)) {
				modifierBuilder.append((char) read);

				if (read == '\'') {
					inQuotes = !inQuotes;
				}
				else if (!inQuotes) {
					if (read == '{') {
						bracketDepth++;
					}
					else if (read == '}') {
						bracketDepth--;
					}
				}
			}

			modifier = modifierBuilder.toString();
		}

		if (read != '}') {
			throw new IllegalArgumentException("Invalid message format, formats must end with a }");
		}

		SubFormatterFactory subFormatterFactory = subFormatterFactories.get(format);

		if (subFormatterFactory == null) {
			throw new IllegalArgumentException("Unknown format name: " + format);
		}

		SubFormatter subFormatter = subFormatterFactory.apply(modifier, locale);

		return parameters -> subFormatter.format(parameterName, parameters, pattern -> new NameBasedMessageFormat(pattern, locale, subFormatterFactories));
	}

	private static int peek(StringReader reader) throws IOException {
		reader.mark(1);

		int result = reader.read();

		reader.reset();

		return result;
	}

	private static Map<String, SubFormatterFactory> copyToUnmodifiableMap(Map<String, SubFormatterFactory> subFormatterFactories) {
		Map<String, SubFormatterFactory> subFormatterFactoriesCopy = new HashMap<>(subFormatterFactories);

		return unmodifiableMap(subFormatterFactoriesCopy);
	}
}
