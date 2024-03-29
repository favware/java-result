package tech.favware.result;

/**
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 */
public interface ResultMapErrFunction<T extends Throwable, R> {

	/**
	 * @param t the input argument
	 * @return the result of the function
	 * @throws Throwable the type of throwable thrown by this method
	 */
	@SuppressWarnings("java:S112")
	R apply(T t) throws Throwable;
}
