package jbt.notify;

import javassist.util.proxy.MethodHandler;
import jbt.Strategy;
import lombok.SneakyThrows;

/**
 * @author jinfeng.hu  @date 2024-04-13
 **/
public class NotifyProxy {
    public static <T> T get(Class<T> myclass) {
        return get(myclass, null);
    }

    @SneakyThrows
    public static <T> T get(Class<T> myclass, Object oriObject) {
        MethodHandler methodHandler;
        if (null == oriObject) {
            methodHandler = new NotifyMethodHandler(myclass.getDeclaredConstructor().newInstance());
        } else {
            methodHandler = new NotifyMethodHandler(oriObject);
        }

        return ProxyUtil.get(myclass, methodHandler);
    }

    public static Strategy getStrategy(Object oriObject) {
        return get(Strategy.class, oriObject);
    }
}
