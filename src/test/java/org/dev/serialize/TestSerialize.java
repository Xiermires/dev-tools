package org.dev.serialize;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dev.serialize.impl.KryoSerializer;
import org.dev.serialize.impl.StandardSerializer;
import org.junit.Test;

public class TestSerialize {

    @Test
    public void testKryo() {
	final Serializer std = new StandardSerializer();
	assertThat(std.deserialize(std.serialize("Hello World"), String.class), is("Hello World"));
    }

    @Test
    public void testStd() {
	final Serializer kryo = new KryoSerializer();
	assertThat(kryo.deserialize(kryo.serialize("Hello World"), String.class), is("Hello World"));
    }

    @Test
    public void comparePerformance() {
	final Serializer std = new StandardSerializer();
	final Serializer kryo = new KryoSerializer();

	// warm-up
	for (int i = 0; i < 100; i++) {
	    std.deserialize(std.serialize(new Testee()), Testee.class);
	    kryo.deserialize(kryo.serialize(new Testee()), Testee.class);
	}

	long ini = System.currentTimeMillis();
	for (int i = 0; i < 10000; i++) {
	    std.deserialize(std.serialize(new Testee()), Testee.class);
	}
	System.out.println("Std time " + (System.currentTimeMillis() - ini));

	ini = System.currentTimeMillis();
	for (int i = 0; i < 10000; i++) {
	    kryo.deserialize(kryo.serialize(new Testee()), Testee.class);
	}
	System.out.println("Kryo time " + (System.currentTimeMillis() - ini));
    }

    static class Testee implements Serializable {
	private static final long serialVersionUID = 1L;

	String foo = "bar";
	String bar = "foo";
	int i = 1024;
	long l = Long.MAX_VALUE - 10;
	byte b = 0xF;
	BigDecimal bd = new BigDecimal(BigInteger.valueOf(Long.MAX_VALUE * 20));
	final List<String> list = new ArrayList<>(Arrays.asList("a", "b", "c", "d"));
    }
}
