package org.dev.proxy;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.lang.reflect.InvocationTargetException;

import org.dev.serialize.Serializer;
import org.dev.serialize.impl.StandardSerializer;
import org.junit.Test;

public class TestProxies {

    @Test
    public void testSerializableProxy() throws InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException {

        final Foo foo = new Foo();
        final Foo proxy = Proxies.create(new FooInvocationHandler(foo));

        proxy.bar();
        proxy.bar();
        proxy.bar();

        assertThat(3, is(Proxies.getInvocationHandler(proxy, FooInvocationHandler.class).get().getInvocationCount()));
        
        final Serializer serializer = new StandardSerializer();
        final Foo proxyClone = serializer.deserialize(serializer.serialize(proxy), Foo.class);
        
        final Class<?> proxyClass = proxy.getClass();
        final Class<?> proxyCloneClass = proxyClone.getClass();
        
        assertThat(proxyClass, is(not(proxyCloneClass)));
        
        final SerializableProxy<?> proxyInvocationHandler = Proxies.getInvocationHandler(proxy, FooInvocationHandler.class).get();
        final SerializableProxy<?> proxyCloneInvocationHandler = Proxies.getInvocationHandler(proxyClone, FooInvocationHandler.class).get();
        
        assertThat(proxyInvocationHandler, is(not(proxyCloneInvocationHandler)));
        
        proxyClone.bar();

        assertThat(3, is(Proxies.getInvocationHandler(proxy, FooInvocationHandler.class).get().getInvocationCount()));
        assertThat(4, is(Proxies.getInvocationHandler(proxyClone, FooInvocationHandler.class).get().getInvocationCount()));
    }
}
