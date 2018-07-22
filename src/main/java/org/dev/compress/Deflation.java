package org.dev.compress;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Deflation {
    private static final Logger log = LoggerFactory.getLogger(Inflation.class);

    public static byte[] deflate(byte[] bytes) {
	return deflate(bytes, Deflater.DEFAULT_COMPRESSION, false);
    }

    public static byte[] deflate(byte[] bytes, int rate, boolean nowrap) {
	final Deflater deflater = new Deflater(rate, nowrap);
	try {
	    return deflate(deflater, bytes);
	} finally {
	    deflater.end();
	}
    }

    public static byte[] deflate(Deflater deflater, byte[] bytes) {
	deflater.setInput(bytes);
	deflater.finish();

	final byte[] buffer = new byte[1024];
	try (final ByteArrayOutputStream out = new ByteArrayOutputStream(bytes.length)) {
	    while (!deflater.finished()) {
		final int count = deflater.deflate(buffer);
		out.write(buffer, 0, count);
	    }
	    return out.toByteArray();
	} catch (IOException e) {
	    log.error("Can't deflate.");
	    return null;
	}
    }
}
