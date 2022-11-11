package utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * @Description:
 * @Author jinfeng.hu  @Date 2022/8/29
 **/
@Slf4j
public class ClassUtils {
    @SneakyThrows
    public static Class getClass(String className) {
        return Class.forName(className);
    }

    // 查询带注解的属性
    public static List<Field> getDeclaredFieldsWithAnnotation(Class clazz, Class<? extends Annotation> annotationClass) {
        List<Field> ret = new LinkedList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            if (null != f.getAnnotation(annotationClass)) {
                ret.add(f);
            }
        }
        Class sc = clazz.getSuperclass();
        if (null != sc) {
            ret.addAll(getDeclaredFieldsWithAnnotation(sc, annotationClass));
        }
        return ret;
    }

    public static List<Field> getDeclaredFields(Class clazz) {
        List<Field> ret = new LinkedList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            ret.add(f);
        }
        Class sc = clazz.getSuperclass();
        if (null != sc) {
            ret.addAll(getDeclaredFields(sc));
        }
        return ret;
    }

    // 通过属性注入
    public static boolean silencedInjection(Object obj, String fieldName, Object value) {
        List<Field> fields = getDeclaredFields(obj.getClass());
        for (Field f : fields) {
            if (f.getName().equals(fieldName)) {
                return silencedInjection(obj, f, value);
            }
        }
        return false;
    }

    // 通过属性注入
    public static boolean silencedInjection(Object obj, Field f, Object value) {
        try {
            boolean flag = f.isAccessible();
            f.setAccessible(true);
            f.set(obj, value);
            f.setAccessible(flag);
            return true;
        } catch (Exception e) {
            log.error("injection: {}.{}", f.getClass().getSimpleName(), f.getName(), e);
        }
        return false;
    }

    // 调用setter注入属性
    // 在setter方法中含有逻辑时，使用setter注入属性比较合适
    public static boolean silencedSetter(Object obj, String fieldName, Object value) {
        try {
            String setter = "set" + NameStringUtils.toClassName(fieldName);
            Class clazz = obj.getClass();
            Method method = null;
            while (null != clazz) {
                Method[] methods = clazz.getDeclaredMethods();
                for (Method m : methods) {
                    if (m.getModifiers() == 1 && m.getName().equals(setter) && m.getParameterCount() == 1) {
                        method = m;
                        break;
                    }
                }
                if (null != method) {
                    break;
                }
                clazz = clazz.getSuperclass();
            }
            if (null != method) {
                method.invoke(obj, value);
                return true;
            }
        } catch (Exception e) {
            log.error("invoke {}", fieldName, e);
        }
        return false;
    }
}
