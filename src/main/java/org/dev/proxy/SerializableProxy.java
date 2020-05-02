package org.dev.proxy;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class SerializableProxy<T extends Serializable> implements InvocationHandler, Serializable {

    final T delegate;

    protected SerializableProxy(T delegate) {
        this.delegate = delegate;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        switch (method.getName()) {
        case "writeReplace":
            return writeReplace();
        case "getInvocationHandler":
            return this;
        }
        return doInvoke(delegate, method, args);
    }

    protected abstract Object doInvoke(Object proxy, Method method, Object[] args)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;

    private final Object writeReplace() throws ObjectStreamException {
        return new SerializedProxy<>(this);
    }
}
