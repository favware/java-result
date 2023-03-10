package tech.favware.java8_result;

public interface ResultMapFunction<T, R> {

	@SuppressWarnings("java:S112")
	R apply(T t) throws Throwable;
}
