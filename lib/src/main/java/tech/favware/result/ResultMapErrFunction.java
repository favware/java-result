package tech.favware.result;

public interface ResultMapErrFunction<T extends Throwable, R> {

	/**
	 * @param t the input argument
	 * @return the result of the function
	 * @throws Throwable the type of throwable thrown by this method
	 */
	@SuppressWarnings("java:S112")
	R apply(T t) throws Throwable;
}
