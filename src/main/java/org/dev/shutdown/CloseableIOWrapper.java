package org.dev.shutdown;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloseableIOWrapper implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(CloseableIOWrapper.class);

    private final java.io.Closeable closeable;

    public CloseableIOWrapper(java.io.Closeable closeable) {
	this.closeable = closeable;
    }

    @Override
    public void close() {
	try {
	    closeable.close();
	} catch (Exception e) {
	    log.warn("Cannot close.", e);
	}
    }
}
