package org.omnifaces.utils.reflect;

import static java.util.Arrays.stream;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Function;

/**
 * <p>
 * So we can extract method info from a method reference.
 * Usage example:
 * <pre>
 * Map&lt;Getter&lt;YourEntity&gt;, Object&gt; criteria = new HashMap&lt;&gt;();
 * criteria.put(YourEntity::getName, Like.startsWith(searchNameStartsWith));
 * criteria.put(YourEntity::getCreated, Order.greaterThanOrEqualTo(searchStartDate));
 * criteria.put(YourEntity::getType, searchTypes);
 * criteria.put(YourEntity::isDeleted, false);
 * </pre>
 * <p>
 * And then later on in "the backend":
 * <pre>
 * criteria.forEach((getter, value) -&gt; requiredCriteria.put(getter.getPropertyName(), value));
 * </pre>
 * <p>
 * This allows a type safe way of defining property names.
 * <p>
 * Inspired by <a href="http://benjiweber.co.uk/blog/2015/08/17/lambda-parameter-names-with-reflection">Lambda parameter names with reflection</a>.
 * NOTE: works only in Java 8u60 and newer.
 *
 * @param <T> The generic base type.
 * @author Bauke Scholtz
 */
public interface Getter<T> extends Function<T, Object>, Serializable {

	default SerializedLambda getSerializedLambda() {
		try {
			Method writeReplace = getClass().getDeclaredMethod("writeReplace");
			writeReplace.setAccessible(true);
			return (SerializedLambda) writeReplace.invoke(this);
		}
		catch (Exception e) {
			throw new UnsupportedOperationException(e);
		}
	}

	@SuppressWarnings("unchecked")
	default Class<T> getBaseType() {
		String className = getSerializedLambda().getImplClass().replace("/", ".");

		try {
			return (Class<T>) Class.forName(className);
		}
		catch (Exception e) {
			throw new UnsupportedOperationException(e);
		}
	}

    default Method getMethod() {
    	String methodName = getSerializedLambda().getImplMethodName();

    	return stream(getBaseType().getDeclaredMethods())
        	.filter(method -> Objects.equals(method.getName(), methodName))
        	.findFirst().orElseThrow(UnsupportedOperationException::new);
    }

    default String getPropertyName() {
    	Method method = getMethod();
    	BeanInfo beanInfo;

		try {
			beanInfo = Introspector.getBeanInfo(getBaseType());
		}
		catch (IntrospectionException e) {
			throw new UnsupportedOperationException(e);
		}

		return stream(beanInfo.getPropertyDescriptors())
    		.filter(property -> Objects.equals(property.getReadMethod(), method))
        	.findFirst().orElseThrow(UnsupportedOperationException::new)
        	.getName();
    }

    default Class<?> getReturnType() {
    	return getMethod().getReturnType();
    }

}
