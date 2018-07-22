package org.dev.cache;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dev.serialize.Serializer;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheStats;
import com.google.common.collect.ImmutableMap;

public class SerializingCache<K, V> implements Cache<K, V> {

    private final Class<V> type;
    private final Cache<K, byte[]> cache;

    private final Serializer serializer;
    private final Function<byte[], V> fromBytes;

    public SerializingCache(Serializer serializer, long maxSizeInBytes, Class<V> type) {
	this.serializer = serializer;
	this.cache = CacheBuilder.newBuilder()//
		.maximumWeight(maxSizeInBytes)//
		.<K, byte[]> weigher((k, v) -> v.length)//
		.expireAfterAccess(60, TimeUnit.MINUTES)//
		.build();
	this.type = type;
	this.fromBytes = bytes -> serializer.deserialize(bytes, type);
    }

    @Override
    public @Nullable V getIfPresent(Object key) {
	return serializer.deserialize(cache.getIfPresent(key), type);
    }

    @Override
    public V get(K key, Callable<? extends V> loader) throws ExecutionException {
	final byte[] bytes = cache.getIfPresent(key);
	if (bytes == null) {
	    try {
		return loader.call();
	    } catch (Exception e) {
		throw new ExecutionException(e);
	    }
	} else {
	    return serializer.deserialize(bytes, type);
	}
    }

    @Override
    @SuppressWarnings("unchecked")
    public ImmutableMap<K, V> getAllPresent(Iterable<?> keys) {
	final ImmutableMap.Builder<K, V> im = ImmutableMap.builder();
	for (Object key : keys) {
	    final V v = getIfPresent(key);
	    if (v != null) {
		im.put((K) key, v);
	    }
	}
	return im.build();
    }

    @Override
    public void put(K key, V value) {
	cache.put(key, serializer.serialize(value));
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
	for (Entry<? extends K, ? extends V> e : m.entrySet()) {
	    put(e.getKey(), e.getValue());
	}
    }

    @Override
    public void invalidate(Object key) {
	cache.invalidate(key);
    }

    @Override
    public void invalidateAll(Iterable<?> keys) {
	cache.invalidateAll(keys);
    }

    @Override
    public void invalidateAll() {
	cache.invalidateAll();
    }

    @Override
    public long size() {
	return cache.size();
    }

    @Override
    public CacheStats stats() {
	return cache.stats();
    }

    @Override
    public ConcurrentMap<K, V> asMap() {
	final ConcurrentMap<K, byte[]> m = cache.asMap();
	final ConcurrentMap<K, V> result = new ConcurrentHashMap<>();
	m.entrySet().stream().forEach(e -> result.put(e.getKey(), fromBytes.apply(e.getValue())));
	return result;
    }

    @Override
    public void cleanUp() {
	cache.cleanUp();
    }
}
