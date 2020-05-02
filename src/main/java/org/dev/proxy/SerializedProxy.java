package org.dev.proxy;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

public class SerializedProxy<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = 1L;

    private final SerializableProxy<T> ih;

    public SerializedProxy(SerializableProxy<T> ih) {
        this.ih = ih;
    }

    private Object readResolve()
            throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {

        return Proxies.create(ih);
    }
}
