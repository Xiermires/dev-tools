/*******************************************************************************
 * Copyright (c) 2017, Xavier Miret Andres <xavier.mires@gmail.com>
 *
 * Permission to use, copy, modify, and/or distribute this software for any 
 * purpose with or without fee is hereby granted, provided that the above 
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES 
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALLIMPLIED WARRANTIES OF 
 * MERCHANTABILITY  AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR 
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES 
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN 
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF 
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package org.dev.executor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * {@link ThreadPoolExecutor}'s start spamming / waking idle threads up only after the queue is full.
 * <p>
 * The {@link SpamOnAvailabilityExecutor} spams threads whenever the queue is not empty and the
 * {@link #getMaximumPoolSize()} hasn't been reached yet.
 */
public class SpamOnAvailabilityExecutor extends ThreadPoolExecutor {
    private final Semaphore semaphore;
    private volatile int pending;

    /**
     * Creates a {@link SpamOnAvailabilityExecutor} which on rejection runs the submitted task on the caller thread.
     */
    public SpamOnAvailabilityExecutor(String name, int maximumPoolSize, long keepAliveTime,
	    BlockingQueue<Runnable> workQueue) {
	this(name, maximumPoolSize, keepAliveTime, workQueue, new CallerRunsPolicy());
    }

    /**
     * Creates a {@link SpamOnAvailabilityExecutor} with a specific {@link RejectedExecutionHandler}.
     */
    public SpamOnAvailabilityExecutor(String name, int maximumPoolSize, long keepAliveTime,
	    BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
	super(0, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, workQueue, threadFactory(name));

	this.semaphore = new Semaphore(1);
	setRejectedExecutionHandler(new DecreaseOnRejection(handler));
    }

    /**
     * Submitted tasks are wrapped and {@link #execute(Runnable)}. Hence, also affected by {@link #updatePoolSize(int)}.
     */
    @Override
    public void execute(Runnable runnable) {
	updatePoolSize(1);
	super.execute(runnable);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
	updatePoolSize(-1);
    }

    private void updatePoolSize(int increment) {
	try {
	    final int max = getMaximumPoolSize();
	    if (pending < max) {
		semaphore.acquire();
		pending += increment;

		setCorePoolSize(pending < max ? pending : max);
	    }
	} catch (InterruptedException e) {
	    // If waiting or sleeping for some reason. Maintain interrupt and throw exception.
	    Thread.currentThread().interrupt();
	    throw new IllegalStateException(e);
	} finally {
	    semaphore.release();
	}
    }

    private static ThreadFactory threadFactory(String name) {
	return new ThreadFactoryBuilder().setNameFormat(name + "-%d").setDaemon(true).build();
    }

    /**
     * Decrease the pending counter when a task is rejected.
     */
    private class DecreaseOnRejection implements RejectedExecutionHandler {
	private final RejectedExecutionHandler wrapped;

	public DecreaseOnRejection(RejectedExecutionHandler handler) {
	    this.wrapped = handler;
	}

	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
	    updatePoolSize(-1);
	    wrapped.rejectedExecution(r, e);
	}
    }
}
