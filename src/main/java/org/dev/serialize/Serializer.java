package org.dev.serialize;

public interface Serializer {
    
    byte[] serialize(Object o);
    
    <T> T deserialize(byte[] serialized, Class<T> type);
}
