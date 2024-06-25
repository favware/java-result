package tech.favware.result;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

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

	@Override
	public <U> Result<T> mapErr(ResultMapErrFunction<? super Throwable, ? extends U> f) {
		Objects.requireNonNull(f);
		try {
			return new Err<>((Throwable) f.apply(e));
		} catch (Throwable t) {
			return Result.err(t);
		}
	}
}
