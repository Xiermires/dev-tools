package org.dev.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

public class FooInvocationHandler extends SerializableProxy<Foo> {

    private AtomicInteger count = new AtomicInteger();

    public FooInvocationHandler(Foo delegate) {
        super(delegate);
    }

    @Override
    protected Object doInvoke(Object proxy, Method method, Object[] args)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        count.incrementAndGet();
        return method.invoke(proxy, args);
    }
    
    public int getInvocationCount() {
        return count.get();
    }
}
