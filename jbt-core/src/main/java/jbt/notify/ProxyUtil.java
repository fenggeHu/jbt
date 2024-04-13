package jbt.notify;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import lombok.SneakyThrows;

/**
 * @author jinfeng.hu  @date 2024-04-13
 **/
public class ProxyUtil {

    @SneakyThrows
    public static <T> T get(Class<T> myclass, MethodHandler methodHandler) {
        // 创建代理工厂
        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(myclass);
        // 创建代理类
        Class<?> proxyClass = factory.createClass();
        // 实例化代理对象
        T proxyObj = (T) proxyClass.newInstance();
        // 设置方法处理器
        ((Proxy) proxyObj).setHandler(methodHandler);

        return proxyObj;
    }
}
