package jbt.notify;

import javassist.util.proxy.MethodHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @author jinfeng.hu  @date 2024-04-13
 **/
@Slf4j
public class NotifyMethodHandler<T> implements MethodHandler {
    private T originalObject;

    public NotifyMethodHandler(T originalObject) {
        this.originalObject = originalObject;
    }

    @Override
    public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
        Object result = thisMethod.invoke(originalObject, args);
        Notify notify = getNotify(thisMethod);
        if (null != notify && notify.enabled()) {
            log.info(thisMethod.getName() + " - Notify: " + result);
        }
        return result;
    }

    private Notify getNotify(Method method) {
        Notify notify = method.getAnnotation(Notify.class);
        if (null != notify) return notify;

        Class<?> declaringClass = method.getDeclaringClass();
        Class<?> superClass = declaringClass.getSuperclass();
        while (superClass != null) {
            try {
                Method superMethod = superClass.getMethod(method.getName(), method.getParameterTypes());
                notify = superMethod.getAnnotation(Notify.class);
                if (null != notify) {
                    return notify;
                }
            } catch (NoSuchMethodException e) {
                // 如果超类没有该方法，继续查找超类的超类
            }
            superClass = superClass.getSuperclass();
        }
        return null;
    }
}
