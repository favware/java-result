package tech.favware.java8_result;

/**
 * This is similar to the Java {@link java.util.function.Consumer Consumer} function type.
 * It has a checked exception on it to allow it to be used in lambda expressions on the {@link Result} monad.
 *
 * @param <T> The type that is wrapped by this {@link ResultConsumer}
 * @param <E> the type of throwable thrown by {@link #accept(Object)}
 */
public interface ResultConsumer<T, E extends Throwable> {

	/**
	 * Performs this operation on the given argument.
	 *
	 * @param t the input argument
	 * @throws E the type of throwable thrown by this method
	 */
	void accept(T t) throws E;

}