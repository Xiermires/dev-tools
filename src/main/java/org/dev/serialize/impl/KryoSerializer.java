package org.dev.serialize.impl;

import java.io.ByteArrayOutputStream;

import org.dev.serialize.Serializer;
import org.objenesis.strategy.StdInstantiatorStrategy;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Kryo.DefaultInstantiatorStrategy;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class KryoSerializer implements Serializer {

    private final Kryo kryo = new Kryo();
    {
	kryo.setReferences(false);
	kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
    }

    @Override
    public byte[] serialize(Object o) {
	final Output out = new Output(new ByteArrayOutputStream());
	kryo.writeObject(out, o);
	return out.toBytes();
    }

    @Override
    public <T> T deserialize(byte[] serialized, Class<T> type) {
	return kryo.readObject(new Input(serialized), type);
    }

    private static final KryoSerializer serializer = new KryoSerializer();

    public static byte[] serializeObject(Object o) {
	return serializer.serialize(o);
    }

    public static <T> T deserializeObject(byte[] serialized, Class<T> type) {
	return serializer.deserialize(serialized, type);
    }
}
