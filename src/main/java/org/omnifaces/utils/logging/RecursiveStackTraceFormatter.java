package org.omnifaces.utils.logging;

import static org.omnifaces.utils.exceptions.Exceptions.getRecursiveStackTrace;

import java.util.function.Predicate;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class RecursiveStackTraceFormatter extends Formatter {

	private final Formatter wrappedFormatter;
	private final Predicate<StackTraceElement> filter;

	public RecursiveStackTraceFormatter(Formatter wrappedFormatter) {
		this(wrappedFormatter, stackTraceElement -> true);
	}

	public RecursiveStackTraceFormatter(Formatter wrappedFormatter, Predicate<StackTraceElement> filter) {
		this.wrappedFormatter = wrappedFormatter;
		this.filter = filter;
	}

	@Override
	public String format(LogRecord record) {
		if (record.getThrown() != null) {
			Throwable throwable = record.getThrown();
			try {
				record.setThrown(null);

				String message = wrappedFormatter.format(record);

				String recursiveStackTrace = getRecursiveStackTrace(throwable, filter);

				return String.format("%s%n%s", message, recursiveStackTrace);
			}
			finally {
				record.setThrown(throwable);
			}

		}

		return wrappedFormatter.format(record);
	}

	public Formatter getWrappedFormatter() {
		return wrappedFormatter;
	}

	public Predicate<StackTraceElement> getFilter() {
		return filter;
	}
}
