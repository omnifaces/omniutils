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
