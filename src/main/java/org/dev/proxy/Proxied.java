package org.dev.proxy;

public interface Proxied {

    SerializableProxy<?> getInvocationHandler();
}
