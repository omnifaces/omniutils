package org.omnifaces.utils.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * {@link InvocationHandler} implementation that implements the base methods required for a parameterless annotation. This handler only implements the
 * following methods: {@link Annotation#equals(Object)}, {@link Annotation#hashCode()}, {@link Annotation#annotationType()} and
 * {@link Annotation#toString()}.
 *
 * @param <T>
 *            the type of the annotation
 */
class AnnotationInvocationHandler<T extends Annotation> implements InvocationHandler {

	private Class<T> annotationType;

	/**
	 * Create a new {@link AnnotationInvocationHandler} instance for the given annotation type.
	 *
	 * @param annotationType
	 *            the annotation type this handler is for
	 */
	public AnnotationInvocationHandler(Class<T> annotationType) {
		this.annotationType = annotationType;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		switch (method.getName()) {
		case "toString":
			return "@" + annotationType.getName() + "()";
		case "annotationType":
			return annotationType;
		case "equals":
			return annotationType.isInstance(args[0]);
		case "hashCode":
			return 0;
		}

		return null;
	}

}