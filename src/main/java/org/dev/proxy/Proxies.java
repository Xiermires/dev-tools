package org.dev.proxy;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType.Loaded;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;

public class Proxies {

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T create(SerializableProxy<T> ih)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException {

        final InvocationHandlerAdapter adapter = InvocationHandlerAdapter.of(ih);
        final Loaded<T> proxy = (Loaded<T>) new ByteBuddy() //
                .subclass(ih.delegate.getClass()) //
                .implement(Proxied.class).defineMethod("writeReplace", Object.class, Visibility.PUBLIC) //
                .intercept(adapter) //
                .defineMethod("getInvocationHandler", SerializableProxy.class, Visibility.PUBLIC) //
                .intercept(adapter)
                .method(ElementMatchers.any()) //
                .intercept(adapter) //
                .make() //
                .load(ih.delegate.getClass().getClassLoader());

        return proxy.getLoaded().getConstructor().newInstance();
    }

    @SuppressWarnings("unchecked")
    public static <T extends SerializableProxy<?>> Optional<T> getInvocationHandler(Object proxy, Class<T> type) {
        if (proxy instanceof Proxied) {
            final SerializableProxy<?> ih = ((Proxied) proxy).getInvocationHandler();
            if (type.isInstance(ih)) {
                return Optional.of((T) ih);
            }
        }
        return Optional.empty();
    }
}
