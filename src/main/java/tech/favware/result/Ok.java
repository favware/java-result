package tech.favware.result;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

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

	@Override
	public <U> Result<T> mapErr(ResultMapErrFunction<? super Throwable, ? extends U> f) {
		Objects.requireNonNull(f);
		return Result.ok(value);
	}
}

