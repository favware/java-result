package tech.favware.java8_result;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Monadic {@link Result} type.
 * Represents a result type that could have succeeded with type {@link T} or errored with a {@link Throwable}.
 *
 * @param <T> The type that is wrapped by {@link Result}
 */
public interface Result<T> {

	/**
	 * Creates a new {@link Result} of a method that might throw an {@link Exception}
	 *
	 * @param f The function that might throw an exception
	 * @param <U> The type of the result if ok
	 * @return A new {@link Result}
	 */
	static <U> Result<U> from(ResultSupplier<U> f) {
		Objects.requireNonNull(f);

		try {
			return Result.ok(f.get());
		} catch (Throwable t) {
			return Result.err(t);
		}
	}

	/**
	 * Factory method for err.
	 *
	 * @param e throwable to create the errored {@link Result} with
	 * @param <U> Type
	 * @return a new {@link Err}
	 */
	static <U> Result<U> err(Throwable e) {
		return new Err<>(e);
	}

	/**
	 * Factory method for {@link Ok}.
	 *
	 * @param x value to create the ok {@link Result} with
	 * @param <U> Type
	 * @return a new {@link Ok}
	 */
	static <U> Result<U> ok(U x) {
		return new Ok<>(x);
	}

	/**
	 * Creates a new {@link Result} of an {@link Optional}
	 *
	 * @param op The {@link Optional} to convert to a {@link Result}
	 * @param e The {@link Throwable} to use if the {@link Optional} is not {@link Optional#isPresent()}
	 * @param <U> The type of the result if ok, this gets wrapped in an {@link Optional}
	 * @return A {@link Ok} if the {@link  Optional} is {@link Optional#isPresent()} or a {@link Err} if the {@link Optional} is not {@link Optional#isPresent()}
	 */
	static <U> Result<U> ofOptional(Optional<U> op, Throwable e) {
		if (op.isPresent()) {
			return new Ok<>(op.get());
		} else {
			return new Err<>(e);
		}
	}

	/**
	 * Creates a new {@link Result} of an {@link Optional}
	 *
	 * @param op The {@link Optional} to convert to a {@link Result}
	 * @param <U> The type of the result if ok, this gets wrapped in an {@link Optional}
	 * @return A {@link Ok} if the {@link  Optional} is {@link Optional#isPresent()} or a {@link Err} if the {@link Optional} is not {@link Optional#isPresent()}
	 */
	static <U> Result<U> ofOptional(Optional<U> op) {
		return ofOptional(op, new IllegalArgumentException("Missing Value"));
	}

	/**
	 * @return true if the {@link Result} is {@link Ok}
	 */
	boolean isOk();

	/**
	 * Unwraps the value {@link T} on {@link Ok} or throws the cause of the err wrapped into a {@link RuntimeException}
	 *
	 * @return T
	 * @throws RuntimeException The exception that caused the err
	 */
	T unwrapUnchecked();

	/**
	 * Transform {@link Ok} or pass on {@link Err}.
	 * Takes an optional type parameter of the new type.
	 * You need to be specific about the new type if changing type
	 * <pre>
	 * {@code Result.from(() -> "1").<Integer>map((x) -> Integer.valueOf(x))}
	 * </pre>
	 *
	 * @param f function to apply to ok value.
	 * @param <U> new type (optional)
	 * @return {@link Ok}{@code <}{@link U}{@code >} or {@link Err}{@code <}{@link U}{@code >}
	 */
	<U> Result<U> map(ResultMapFunction<? super T, ? extends U> f);

	/**
	 * Transform {@link Ok} or pass on {@link Err}, taking a {@link Result}{@code <}{@link U}{@code >} as the result.
	 * Takes an optional type parameter of the new type.
	 * You need to be specific about the new type if changing type.
	 * <pre>
	 * {@code
	 *   Result.from(() -> "1").<Integer>flatMap((x) -> Result.from(() -> Integer.valueOf(x)))
	 *   // returns Integer(1)
	 * }
	 * </pre>
	 *
	 * @param f function to apply to ok value.
	 * @param <U> new type (optional)
	 * @return new composed {@link Result}
	 */
	<U> Result<U> flatMap(ResultMapFunction<? super T, Result<U>> f);

	/**
	 * Specifies a result to use in case of err.
	 * Gives access to the exception which can be used for a pattern match.
	 * <pre>
	 * {@code
	 * Result.from(() -> "not a number")
	 * .<Integer>flatMap((x) -> Result.from(() -> Integer.valueOf(x)))
	 * .recover((t) -> 1)
	 * // returns Integer(1)
	 * }
	 * </pre>
	 *
	 * @param f function to execute on ok result.
	 * @return new composed {@link Result}
	 */

	T recover(Function<? super Throwable, T> f);

	/**
	 * Try applying <code>f(t)</code> on the case of err.
	 *
	 * @param f function that takes throwable and returns result
	 * @return a new {@link Result} in the case of {@link Err}, or the current {@link Ok}.
	 */
	Result<T> recoverWith(ResultMapFunction<? super Throwable, Result<T>> f);

	/**
	 * Return a value in the case of a {@link Err}.
	 * This is similar to recover but does not expose the exception type.
	 *
	 * @param value return the result's value or else the value specified.
	 * @return new composed {@link Result}
	 */
	T orElse(T value);

	/**
	 * Return another {@link Result} in the case of {@link Err}.
	 * Like {@link Result#recoverWith} but without exposing the exception.
	 *
	 * @param f return the value or the value from the new {@link Result}.
	 * @return new composed {@link Result}
	 */
	Result<T> orElseResult(ResultSupplier<T> f);

	/**
	 * Unwraps the value {@link T} on {@link Ok} or throws the cause of the {@link Err}.
	 *
	 * @param <X> the type of the exception to be thrown
	 * @param exceptionSupplier supplier function to produce the exception to be thrown
	 * @return T
	 * @throws X produced by the supplier function argument
	 */
	<X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X;

	/**
	 * Unwraps the value {@link T} on {@link Ok} or throws the cause of the {@link Err}.
	 *
	 * @return T
	 * @throws Throwable The error that caused the {@link Err}
	 */
	@SuppressWarnings("java:S112")
	T unwrap() throws Throwable;

	/**
	 * Performs the provided action, when ok
	 *
	 * @param <E> the type of the exception to be thrown
	 * @param action action to run
	 * @return new composed {@link Result}
	 * @throws E if the action throws an exception
	 */
	<E extends Throwable> Result<T> onOk(ResultConsumer<T, E> action) throws E;

	/**
	 * Performs the provided action, when {@link Err}
	 *
	 * @param <E> the type of the exception to be thrown
	 * @param action action to run
	 * @return new composed {@link Result}
	 * @throws E if the action throws an exception
	 */
	<E extends Throwable> Result<T> onErr(ResultConsumer<Throwable, E> action) throws E;

	/**
	 * When the <code>clazz</code> {@link Err} type happens, that exception is thrown
	 *
	 * @param <E> the type of the exception to be thrown
	 * @param clazz the expected exception class
	 * @return new composed {@link Result}
	 * @throws E if the {@link Err} type is the on provided
	 */
	<E extends Throwable> Result<T> raise(Class<E> clazz) throws E;

	/**
	 * If a {@link Result} is a {@link Ok} and the predicate holds true, the {@link Ok} is passed further.
	 * Otherwise, ({@link Err} or predicate doesn't hold), pass {@link Err}.
	 *
	 * @param pred predicate applied to the value held by {@link Result}
	 * @return For {@link Ok}, the same {@link Ok} if predicate holds true, otherwise {@link Err}
	 */
	Result<T> filter(Predicate<T> pred);

	/**
	 * {@link Result} contents wrapped in {@link Optional}.
	 *
	 * @return {@link Optional} of {@link T}, if {@link Ok}, Empty if {@link Err} or null value
	 */
	Optional<T> toOptional();
}

class Ok<T> implements Result<T> {

	private final T value;

	public Ok(T value) {
		this.value = value;
	}

	@Override
	public boolean isOk() {
		return true;
	}

	@Override
	public T unwrapUnchecked() {
		return value;
	}

	@Override
	public <U> Result<U> flatMap(ResultMapFunction<? super T, Result<U>> f) {
		Objects.requireNonNull(f);
		try {
			return f.apply(value);
		} catch (Throwable t) {
			return Result.err(t);
		}
	}

	@Override
	public T recover(Function<? super Throwable, T> f) {
		Objects.requireNonNull(f);
		return value;
	}

	@Override
	public Result<T> recoverWith(ResultMapFunction<? super Throwable, Result<T>> f) {
		Objects.requireNonNull(f);
		return this;
	}

	@Override
	public T orElse(T value) {
		return this.value;
	}

	@Override
	public Result<T> orElseResult(ResultSupplier<T> f) {
		Objects.requireNonNull(f);
		return this;
	}

	@Override
	public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
		return value;
	}

	@Override
	@SuppressWarnings("java:S4144")
	public T unwrap() throws Throwable {
		return value;
	}

	@Override
	public <U> Result<U> map(ResultMapFunction<? super T, ? extends U> f) {
		Objects.requireNonNull(f);
		try {
			return new Ok<>(f.apply(value));
		} catch (Throwable t) {
			return Result.err(t);
		}
	}

	@Override
	public <E extends Throwable> Result<T> onOk(ResultConsumer<T, E> action) throws E {
		action.accept(value);
		return this;
	}

	@Override
	public Result<T> filter(Predicate<T> p) {
		Objects.requireNonNull(p);

		if (p.test(value)) {
			return this;
		} else {
			return Result.err(new NoSuchElementException("Predicate does not match for " + value));
		}
	}

	@Override
	public Optional<T> toOptional() {
		return Optional.ofNullable(value);
	}

	@Override
	public <E extends Throwable> Result<T> onErr(ResultConsumer<Throwable, E> action) {
		return this;
	}

	@Override
	public <E extends Throwable> Result<T> raise(Class<E> clazz) throws E {
		return this;
	}
}


class Err<T> implements Result<T> {

	private final Throwable e;

	Err(Throwable e) {
		this.e = e;
	}

	@Override
	public boolean isOk() {
		return false;
	}

	@Override
	@SuppressWarnings("java:S112")
	public T unwrapUnchecked() {
		if (e instanceof RuntimeException) {
			throw (RuntimeException) e;
		} else {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <U> Result<U> map(ResultMapFunction<? super T, ? extends U> f) {
		Objects.requireNonNull(f);
		return Result.err(e);
	}

	@Override
	public <U> Result<U> flatMap(ResultMapFunction<? super T, Result<U>> f) {
		Objects.requireNonNull(f);
		return Result.err(e);
	}

	@Override
	public T recover(Function<? super Throwable, T> f) {
		Objects.requireNonNull(f);
		return f.apply(e);
	}

	@Override
	public Result<T> recoverWith(ResultMapFunction<? super Throwable, Result<T>> f) {
		Objects.requireNonNull(f);
		try {
			return f.apply(e);
		} catch (Throwable t) {
			return Result.err(t);
		}
	}

	@Override
	public T orElse(T value) {
		return value;
	}

	@Override
	public Result<T> orElseResult(ResultSupplier<T> f) {
		Objects.requireNonNull(f);
		return Result.from(f);
	}

	@Override
	public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
		throw exceptionSupplier.get();
	}

	@Override
	public T unwrap() throws Throwable {
		throw e;
	}

	@Override
	public <E extends Throwable> Result<T> onOk(ResultConsumer<T, E> action) {
		return this;
	}

	@Override
	public Result<T> filter(Predicate<T> pred) {
		return this;
	}

	@Override
	public Optional<T> toOptional() {
		return Optional.empty();
	}

	@Override
	public <E extends Throwable> Result<T> onErr(ResultConsumer<Throwable, E> action) throws E {
		action.accept(e);
		return this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <E extends Throwable> Result<T> raise(Class<E> clazz) throws E {
		return onErr(t -> {
			if (clazz.isAssignableFrom(t.getClass())) {
				throw (E) t;
			}
		});
	}
}
