package org.dev.serialize.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.dev.io.Closeables;
import org.dev.serialize.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StandardSerializer implements Serializer {

    private static final Logger log = LoggerFactory.getLogger(StandardSerializer.class);

    @Override
    public byte[] serialize(Object o) {
	if (o instanceof Serializable) {
	    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
		oos.writeObject(o);
		return bos.toByteArray();
	    } catch (IOException e) {
		log.error("Can't write.", e);
	    } finally {
		Closeables.closeSilently(bos);
	    }
	}
	throw new IllegalArgumentException("Not serializable.");
    }

    @Override
    public <T> T deserialize(byte[] serialized, Class<T> type) {
	if (Serializable.class.isAssignableFrom(type)) {
	    final ByteArrayInputStream bis = new ByteArrayInputStream(serialized);
	    try (ObjectInputStream ois = new ObjectInputStream(bis)) {
		return type.cast(ois.readObject());
	    } catch (IOException | ClassNotFoundException e) {
		log.error("Can't write.", e);
	    } finally {
		Closeables.closeSilently(bis);
	    }
	}
	throw new IllegalArgumentException("Not serializable.");
    }
}
