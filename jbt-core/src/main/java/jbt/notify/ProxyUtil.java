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

    // 判断一个对象是否是代理类的实例 TODO 不知道有没有更好的判断方法
    public static boolean isProxyInstance(Object obj) {
        // 检查对象的类是否是由 javassist 生成的代理类
        String className = obj.getClass().getSimpleName();
        return className.startsWith("com.sun.proxy.") || className.contains("_$$_");
    }
}
