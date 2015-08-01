package org.omnifaces.utils.function;

/**
 * Functional helper-interface to allow for use of closeable resources that don't implement AutoCloseable in a try-with-resources statement.
 *
 * <p>
 * This interface can be used with any object instance that doesn't implement AutoCloseable, but does have a close or similar method that needs to be
 * called to free up resources. This functional interface is useable for any close method that doesn't throw any checked exceptions, for methods that
 * throw a more specific exception than Exception, please see @link{ThrowingAutoCloseable}.
 * </p>
 *
 * <p>
 *     <h4>Example:</h4>
 *     <code>
 *         CloseableResource resource = ...
 *         try (ExceptionlessAutoCloseable eac = resource::close) {
 *             // Use resource
 *         }
 *     </code>
 * </p>
 */
@FunctionalInterface
public interface ExceptionlessAutoCloseable extends AutoCloseable {

	@Override
	void close();
}
