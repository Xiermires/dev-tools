package org.dev.shutdown;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShutdownManager {

    private static final Logger log = LoggerFactory.getLogger(ShutdownManager.class);

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final Set<Closeable> closeables = Collections.synchronizedSet(new HashSet<>());

    public ShutdownManager() {
	Runtime.getRuntime().addShutdownHook(new Thread(() -> {
	    scheduler.shutdown();
	    closeables.forEach(c -> close(c));
	}));
    }

    public static void closeOnShutdown(java.io.Closeable closeable) {
	closeOnShutdown(new CloseableIOWrapper(closeable));
    }

    public static void closeOnShutdown(Closeable closeable) {
	closeables.add(closeable);
    }

    public static void closeAfter(Closeable closeable, long delay, TimeUnit unit) {
	scheduler.schedule(() -> close(closeable), delay, unit);
    }

    private static void close(Closeable closeable) {
	try {
	    closeable.close();
	} catch (Exception e) {
	    log.error("Can't close.", e);
	}
    }
}
