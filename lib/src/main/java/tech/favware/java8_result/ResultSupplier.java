package tech.favware.java8_result;

/**
 * This is similar to the Java Supplier function type.
 * It has a checked exception on it to allow it to be used in lambda expressions on the Try monad.
 *
 * @param <T> The type that is wrapped by this {@link ResultSupplier}
 */
public interface ResultSupplier<T> {

	@SuppressWarnings("java:S112")
	T get() throws Throwable;
}