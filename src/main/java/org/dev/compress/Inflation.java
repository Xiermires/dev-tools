package org.dev.compress;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Inflation {
    private static final Logger log = LoggerFactory.getLogger(Inflation.class);

    public static byte[] inflate(byte[] bytes) {
	return inflate(bytes, false);
    }

    public static byte[] inflate(byte[] bytes, boolean nowrap) {
	final Inflater inflater = new Inflater(nowrap);
	try {
	    return inflate(inflater, bytes);
	} finally {
	    inflater.end();
	}
    }

    public static byte[] inflate(Inflater inflater, byte[] bytes) {
	inflater.setInput(bytes);

	final byte[] buffer = new byte[1024];
	try (final ByteArrayOutputStream out = new ByteArrayOutputStream(bytes.length)) {
	    while (!inflater.finished()) {
		final int count = inflater.inflate(buffer);
		out.write(buffer, 0, count);
	    }
	    return out.toByteArray();
	} catch (DataFormatException | IOException e) {
	    log.error("Can't inflate.");
	    return null;
	}
    }
}
