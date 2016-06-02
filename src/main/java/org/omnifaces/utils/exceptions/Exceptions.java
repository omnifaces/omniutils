package org.omnifaces.utils.exceptions;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class Exceptions {

	private static final List<String> JAVA_SE_STACK_TRACE_EXCLUSIONS = unmodifiableList(asList(
			"java.lang.reflect",
			"java.lang.Thread.run",
			"sun.reflect"
	));

	// TODO Give clearer name and expand with packages from other containers
	private static final List<String> JAVA_EE_STACK_TRACE_EXCLUSIONS = unmodifiableList(asList(
			"org.omnifaces.filter.HttpFilter",
			"com.sun.faces.el.DemuxCompositeELResolver._getValue",
			"com.sun.faces.lifecycle.Phase.doPhase",
			"javax.faces.component.UIComponentBase.processValidators",
			"org.apache.catalina.core",
			"org.apache.catalina.valves.ErrorReportValve",
			"org.apache.coyote.http11.Http11Protocol",
			"org.apache.el.parser.AstValue",
			// JBoss/WildFly specific exclusions
			"org.jboss.aop",
			"org.jboss.aspects",
			"org.jboss.as.ee.component",
			"org.jboss.as.ee.component.interceptors.UserInterceptorFactory",
			"org.jboss.as.ejb3.component.interceptors",
			"org.jboss.as.ejb3.tx.CMTTxInterceptor",
			"org.jboss.as.web.deployment.component.WebComponentInstantiator$2.",
			"org.jboss.as.weld.ejb.Jsr299BindingsInterceptor",
			"org.jboss.ejb3",
			"org.jboss.invocation.InterceptorContext.proceed",
			"org.jboss.invocation.WeavedInterceptor.processInvocation",
			"org.jboss.weld.bean.proxy.EnterpriseBeanProxyMethodHandler.invoke",
			"org.jboss.weld.bean.proxy.EnterpriseTargetBeanInstance",
			"org.jboss.weld.bean.proxy.ProxyMethodHandler.invoke",
			"org.jboss.weld.util.reflection.SecureReflection",
			"org.jboss.invocation.ChainedInterceptor.processInvocation",
			"org.jboss.invocation.InterceptorContext$Invocation.proceed"
	));

	public static final int SHORT_STACKTRACE_DEPTH = 2;

	private Exceptions() {
	}

	public static String getRecursiveStackTrace(Throwable throwable) {
		return getRecursiveStackTrace(throwable, stackTraceElement -> true);
	}

	public static String getRecursiveStackTrace(Throwable throwable, Predicate<StackTraceElement> filter) {
		StringBuilder headerBuilder = new StringBuilder("Exception summary:\n\n");
		StringBuilder builder = new StringBuilder("\n\nException details:");

		int exceptionLevel = 0;

		Throwable rootCause = null;
		Throwable currentThrowable = throwable;

		while (currentThrowable != null) {
			String currentMessage = "Exception level " + exceptionLevel + ": " + getNameAndMessage(currentThrowable) + "\n";
			builder.append("\n\n").append(currentMessage);
			headerBuilder.append(currentMessage);

			appendStackTrace(builder, currentThrowable, filter, exceptionLevel, 1);

			rootCause = currentThrowable;

			currentThrowable = currentThrowable.getCause();
			exceptionLevel++;
		}

		StringBuilder messageBuilder = new StringBuilder().append(headerBuilder);

		if (exceptionLevel > 1) {

			messageBuilder.append("\n\nRoot cause at level ")
			              .append(exceptionLevel - 1)
			              .append(": ")
			              .append(getNameAndMessage(rootCause))
			              .append("\n");

			appendShortStackTrace(messageBuilder, rootCause, filter, 1);
		}

		return messageBuilder.append(builder)
		                     .toString();
	}

	private static void appendShortStackTrace(StringBuilder builder, Throwable throwable, Predicate<StackTraceElement> filter, int indentLevel) {
		String indentString = getIndentString(indentLevel);

		Arrays.stream(throwable.getStackTrace())
		      .filter(filter)
		      .limit(SHORT_STACKTRACE_DEPTH)
		      .forEach(stackTraceElement -> builder.append(indentString).append("at ").append(stackTraceElement).append("\n"));
	}

	private static void appendStackTrace(StringBuilder bodyBuilder, Throwable throwable, Predicate<StackTraceElement> filter, int exceptionLevel,
			int indentLevel) {
		String indentString = getIndentString(indentLevel);

		appendStackTrace(bodyBuilder, throwable, exceptionLevel, indentString, filter);

	}

	private static void appendStackTrace(StringBuilder builder, Throwable throwable, int exceptionLevel, String indentString,
			Predicate<StackTraceElement> filter) {
		Arrays.stream(throwable.getStackTrace())
		      .filter(filter)
		      .forEach(stackTraceElement -> builder.append(indentString).append("at ").append(stackTraceElement).append("\n"));


		for (Throwable suppressed : throwable.getSuppressed()) {
			builder.append(indentString)
			       .append("Suppressed at level ")
			       .append(exceptionLevel)
			       .append(": ")
			       .append(getNameAndMessage(suppressed))
			       .append("\n");

			appendStackTrace(builder, suppressed, exceptionLevel, indentString + "\t", filter);
		}
	}

	private static String getNameAndMessage(Throwable throwable) {
		String message = throwable.getMessage();

		if (message == null) {
			return throwable.getClass().getName();
		} else if (message.contains(":")) {
			String[] messageParts = message.split(":");
			StringBuilder messageBuilder = new StringBuilder();
			int count = 0;

			for (String messagePart : messageParts) {
				boolean lastMessagePart = count == messageParts.length - 1;

				if (lastMessagePart || !messagePart.endsWith("Exception")) {
					messageBuilder.append(messagePart);

					if (lastMessagePart) {
						messageBuilder.append(":");
					}
				}
			}
		}

		return throwable.getClass().getName() + ": " + message;
	}

	private static String getIndentString(int indentLevel) {
		return Stream.generate(() -> "\t")
		             .limit(indentLevel)
		             .collect(joining());
	}

	public static Predicate<StackTraceElement> excludeJavaSE() {
		// TODO better name for this
		return excludeFromStackTrace(JAVA_SE_STACK_TRACE_EXCLUSIONS);
	}

	public static Predicate<StackTraceElement> excludeJavaEE() {
		return excludeFromStackTrace(JAVA_EE_STACK_TRACE_EXCLUSIONS);
	}

	public static Predicate<StackTraceElement> excludeAll() {
		// TODO better name for method and include the other exclusions as well
		return excludeJavaSE().and(excludeJavaEE());
	}

	public static Predicate<StackTraceElement> excludeFromStackTrace(List<String> packageOrClassNames) {
		Objects.requireNonNull(packageOrClassNames);
		return stackTraceElement -> packageOrClassNames.stream()
		                                               .noneMatch(exclusion -> stackTraceElement.toString().startsWith(exclusion) ||
				                                               stackTraceElement.getClassName().startsWith(exclusion));
	}

}
