package org.dev.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

public class TransformableFuture<T, R> implements Future<R> {

    private final Future<T> delegate;
    private final Function<T, R> transformer;

    public TransformableFuture(Future<T> delegate, Function<T, R> function) {
	this.delegate = delegate;
	this.transformer = function;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
	return delegate.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
	return delegate.isCancelled();
    }

    @Override
    public boolean isDone() {
	return delegate.isDone();
    }

    @Override
    public R get() throws InterruptedException, ExecutionException {
	return transformer.apply(delegate.get());
    }

    @Override
    public R get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
	return transformer.apply(delegate.get(timeout, unit));
    }
}
