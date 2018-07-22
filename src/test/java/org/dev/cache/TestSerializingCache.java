package org.dev.cache;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.dev.serialize.impl.StandardSerializer;
import org.junit.Test;

import com.google.common.cache.Cache;

public class TestSerializingCache {

    @Test
    public void testPutGet() {
	final Cache<String, String> cache = new SerializingCache<>(new StandardSerializer(), 2048, String.class);
	cache.put("Hello", "World");
	assertThat(cache.getIfPresent("Hello"), is("World"));
    }
}
