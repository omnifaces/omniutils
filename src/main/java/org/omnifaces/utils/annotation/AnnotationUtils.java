package org.omnifaces.utils.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public final class AnnotationUtils {

	private AnnotationUtils() {
	}

	/**
	 * Create an instance of the specified annotation type. This method is only suited for annotations without any properties, for annotations with
	 * properties, please see {@link #createAnnotationInstance(Class, InvocationHandler)}.
	 *
	 * @param <T> The type of the annotation
	 * @param annotationType
	 *            the type of annotation
	 * @return an instance of the specified type of annotation
	 */
	public static <T extends Annotation> T createAnnotationInstance(Class<T> annotationType) {
		return createAnnotationInstance(annotationType, new AnnotationInvocationHandler<>(annotationType));
	}

	public static <T extends Annotation> T createAnnotationInstance(Class<T> annotationType, InvocationHandler invocationHandler) {
		return annotationType.cast(Proxy.newProxyInstance(AnnotationUtils.class.getClassLoader(), new Class<?>[] { annotationType },
				invocationHandler));
	}
}
