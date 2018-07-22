package org.dev.compress;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Random;

import org.junit.Test;

public class TestCompression {

    private static final Random random = new Random();
    private static final String alphabet = "abcdefghijklmnopqrstuvwxyz";

    @Test
    public void testCompression() throws IOException {
	final StringBuilder sb = new StringBuilder();
	for (int i = 0; i < 10000; i++) {
	    sb.append(alphabet.charAt(random.nextInt(alphabet.length())));
	}

	final byte[] deflated = Deflation.deflate(sb.toString().getBytes());
	final byte[] inflated = Inflation.inflate(deflated);
	assertThat(new String(inflated), is(sb.toString()));
    }
}
