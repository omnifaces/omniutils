package org.omnifaces.utils.text;

import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;

@FunctionalInterface
public interface SubFormatterFactory extends BiFunction<String, Locale, SubFormatter> {

	SubFormatter createFormatter(String modifier, Locale locale);

	@Override
	default SubFormatter apply(String modifier, Locale locale) {
		return createFormatter(modifier, locale);
	}

	static Map<String, SubFormatterFactory> defaultFormatterFactories() {
		return FormatterUtil.DEFAULT_FORMATTER_FACTORIES;
	}

}
