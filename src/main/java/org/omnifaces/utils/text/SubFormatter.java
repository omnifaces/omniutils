package org.omnifaces.utils.text;

import static java.text.NumberFormat.getCurrencyInstance;
import static java.text.NumberFormat.getIntegerInstance;
import static java.text.NumberFormat.getPercentInstance;
import static java.time.format.DateTimeFormatter.ofLocalizedDate;
import static java.time.format.DateTimeFormatter.ofLocalizedDateTime;
import static java.time.format.DateTimeFormatter.ofLocalizedTime;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.time.format.FormatStyle.FULL;
import static java.time.format.FormatStyle.LONG;
import static java.time.format.FormatStyle.MEDIUM;
import static java.time.format.FormatStyle.SHORT;
import static org.omnifaces.utils.Lang.isEmpty;
import static org.omnifaces.utils.text.FormatterUtil.firstIndexOfNonQuoted;
import static org.omnifaces.utils.text.FormatterUtil.getNumber;
import static org.omnifaces.utils.text.FormatterUtil.getTemporalAccessor;

import java.io.Serializable;
import java.text.ChoiceFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

@FunctionalInterface
public interface SubFormatter extends Serializable {

	/**
	 * Formats the the parameter with the given name from the given set of parameters.
	 *
	 * {@link SubFormatter}s can recursively format messages using the supplied parameters and the given {@link NameBasedMessageFormat} factory. The
	 * {@link NameBasedMessageFormat} factory should create an instance that uses the same settings as the format that is calling the current
	 * sub-formatter, but with a different pattern.
	 *
	 * @param name
	 *            the name of the parameter to format
	 * @param parameters
	 *            the map that contains all the parameters used in the current formatter operation
	 * @param nameBasedMessageFormatFactory
	 *            a factory to create a new {@link NameBasedMessageFormat} for any recursively defined message patterns.
	 * @return the formatted parameter
	 */
	String format(String name, Map<? super String, ?> parameters, Function<String, NameBasedMessageFormat> nameBasedMessageFormatFactory);

	static SubFormatter stringFormatter(String modifier, Locale locale) {
		return (name, parameters, patternFormatFactory) -> "" + parameters.get(name);
	}

	static SubFormatter numberFormatter(String modifier, Locale locale) {
		if (isEmpty(modifier)) {
			return (name, parameters, patternFormatFactory) -> NumberFormat.getInstance(locale).format(getNumber(parameters.get(name)));
		}

		switch (modifier) {
			case "integer":
				return (name, parameters, patternFormatFactory) -> getIntegerInstance(locale).format(getNumber(parameters.get(name)));
			case "currency":
				return (name, parameters, patternFormatFactory) -> getCurrencyInstance(locale).format(getNumber(parameters.get(name)));
			case "percent":
				return (name, parameters, patternFormatFactory) -> getPercentInstance(locale).format(getNumber(parameters.get(name)));
			default:
				return (name, parameters, patternFormatFactory) -> new DecimalFormat(modifier, DecimalFormatSymbols.getInstance(locale)).format(getNumber(parameters
						.get(name)));
		}
	}

	static SubFormatter dateFormatter(String modifier, Locale locale) {
		DateTimeFormatter dateTimeFormatter;
		if (isEmpty(modifier)) {
			dateTimeFormatter = ofLocalizedDate(MEDIUM).withLocale(locale);
		}
		else {
			switch (modifier) {
				case "short":
					dateTimeFormatter = ofLocalizedDate(SHORT).withLocale(locale);
					break;
				case "medium":
					dateTimeFormatter = ofLocalizedDate(MEDIUM).withLocale(locale);
					break;
				case "long":
					dateTimeFormatter = ofLocalizedDate(LONG).withLocale(locale);
					break;
				case "full":
					dateTimeFormatter = ofLocalizedDate(FULL).withLocale(locale);
					break;
				default:
					dateTimeFormatter = ofPattern(modifier).withLocale(locale);
			}
		}

		return (name, parameters, patternFormatFactory) -> dateTimeFormatter.format(getTemporalAccessor(parameters.get(name)));
	}

	static SubFormatter dateTimeFormatter(String modifier, Locale locale) {
		DateTimeFormatter dateTimeFormatter;
		if (isEmpty(modifier)) {
			dateTimeFormatter = ofLocalizedDateTime(MEDIUM).withLocale(locale);
		}
		else {
			switch (modifier) {
				case "short":
					dateTimeFormatter = ofLocalizedDateTime(SHORT).withLocale(locale);
					break;
				case "medium":
					dateTimeFormatter = ofLocalizedDateTime(MEDIUM).withLocale(locale);
					break;
				case "long":
					dateTimeFormatter = ofLocalizedDateTime(LONG).withLocale(locale);
					break;
				case "full":
					dateTimeFormatter = ofLocalizedDateTime(FULL).withLocale(locale);
					break;
				default:
					dateTimeFormatter = ofPattern(modifier).withLocale(locale);
			}
		}

		return (name, parameters, patternFormatFactory) -> dateTimeFormatter.format(getTemporalAccessor(parameters.get(name)));
	}

	static SubFormatter timeFormatter(String modifier, Locale locale) {
		DateTimeFormatter dateTimeFormatter;
		if (isEmpty(modifier)) {
			dateTimeFormatter = ofLocalizedTime(MEDIUM).withLocale(locale);
		}
		else {
			switch (modifier) {
				case "short":
					dateTimeFormatter = ofLocalizedTime(SHORT).withLocale(locale);
					break;
				case "medium":
					dateTimeFormatter = ofLocalizedTime(MEDIUM).withLocale(locale);
					break;
				case "long":
					dateTimeFormatter = ofLocalizedTime(LONG).withLocale(locale);
					break;
				case "full":
					dateTimeFormatter = ofLocalizedTime(FULL).withLocale(locale);
					break;
				default:
					dateTimeFormatter = ofPattern(modifier).withLocale(locale);
			}
		}

		return (name, parameters, patternFormatFactory) -> dateTimeFormatter.format(getTemporalAccessor(parameters.get(name)));
	}

	static SubFormatter choiceFormatter(String modifier, Locale locale) {
		return (name, parameters, patternFormatFactory) -> {
			Number number = getNumber(parameters.get(name));

			ChoiceFormat choiceFormat = new ChoiceFormat(modifier);
			String format = choiceFormat.format(number.doubleValue());

			if (format.indexOf('{') >= 0) {
				return patternFormatFactory.apply(format).format(parameters);
			}

			return format;
		};
	}

	static SubFormatter optionalFormatter(String modifier, Locale locale) {
		String replacementPattern;
		String formatPattern;

		int indexOfSeparator = firstIndexOfNonQuoted(modifier, '|');
		if (indexOfSeparator >= 0) {
			replacementPattern = modifier.substring(0, indexOfSeparator);
			formatPattern = modifier.substring(indexOfSeparator + 1);
		} else {
			replacementPattern = modifier;
			formatPattern = null;
		}

		return (name, parameters, patternFormatFactory) -> {
			Object parameter = parameters.get(name);

			String outputPattern;

			if (parameter == null) {
				outputPattern = replacementPattern;
			} else if (formatPattern == null) {
				return "" + parameter;
			} else {
				outputPattern = formatPattern;
			}

			if (outputPattern.contains("{") || outputPattern.contains("'")) {
				// Pattern contains special characters, format using a new NameBasedMessageFormat instance
				return patternFormatFactory.apply(outputPattern)
										   .format(parameters);
			}

			return outputPattern;
		};
	}

	static SubFormatter booleanFormatter(String modifier, Locale locale) {
		String truePattern;
		String falsePattern;

		int indexOfSeparator = firstIndexOfNonQuoted(modifier, '|');

		if (indexOfSeparator >= 0) {
			truePattern = modifier.substring(0, indexOfSeparator);
			falsePattern = modifier.substring(indexOfSeparator + 1);
		} else {
			truePattern = modifier;
			falsePattern = "";
		}

		return (name, parameters, patternFormatFactory) -> {
			if (Boolean.parseBoolean("" + parameters.get(name))) {
				return patternFormatFactory.apply(truePattern)
				                           .format(parameters);
			}
			else {
				return patternFormatFactory.apply(falsePattern)
				                           .format(parameters);
			}
		};
	}

	/**
	 * Formatter that always formats the given modifier as a pattern instead of using the given parameter. This formatter may be useful for testing or
	 * for more complex dynamic formatting.
	 *
	 * @param modifier the {@link String} to use as alternative
	 * @param locale the locale to use when formatting
	 * @return a formatter that outputs the given modifier, formatted as pattern
	 */
	static SubFormatter alternativeFormatter(String modifier, Locale locale) {
		return (name, parameters, patternFormatFactory) -> patternFormatFactory.apply(modifier).format(parameters);
	}

}
