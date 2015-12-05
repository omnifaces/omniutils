package org.omnifaces.utils.logging;

import java.util.function.Predicate;
import java.util.logging.Filter;
import java.util.logging.LogRecord;

@FunctionalInterface
public interface LogFilter extends Filter, Predicate<LogRecord> {

	@Override
	default boolean test(LogRecord logRecord) {
		return isLoggable(logRecord);
	}

	@Override
	default LogFilter and(Predicate<? super LogRecord> other) {
		return (LogRecord logRecord) -> isLoggable(logRecord) && other.test(logRecord);
	}

	@Override
	default LogFilter negate() {
		return logRecord -> !isLoggable(logRecord);
	}

	@Override
	default LogFilter or(Predicate<? super LogRecord> other) {
		return logRecord -> isLoggable(logRecord) || other.test(logRecord);
	}

	static LogFilter fromPredicate(Predicate<? super LogRecord> predicate ) {
		return predicate::test;
	}

	static LogFilter fromFilter(Filter filter) {
		return filter::isLoggable;
	}

	static LogFilter hasThrowable() {
		return logRecord -> logRecord.getThrown() != null;
	}

	static LogFilter hasThrowableOfType(Class<? extends Throwable> clazz) {
		return logRecord -> clazz.isInstance(logRecord.getThrown());
	}
}
