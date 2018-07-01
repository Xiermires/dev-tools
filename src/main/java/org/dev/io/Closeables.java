package org.dev.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Closeables {

    private static final Logger log = LoggerFactory.getLogger(Closeables.class);

    public static void closeSilently(AutoCloseable closeable) {
	try {
	    closeable.close();
	} catch (Exception e) {
	    log.warn("Cannot close.", e);
	}
    }
}
