package utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * get declared method - 开发定义的本类的方法，即排除了jdk的方法
 * get method - 包含本来和父类（含jkd/Object类）的所以public方法
 * @author jinfeng.hu  @Date 2022/8/29
 * @Description:
 **/
@Slf4j
public class ClassUtil {
    @SneakyThrows
    public static Class getClass(String className) {
        return Class.forName(className);
    }

    // 查找第一个匹配类的公开方法 - 从子类往父类递归找到第一个匹配的
    public static Method getDeclaredMethod(Class clazz, String name, Class<?>... parameterTypes) {
        Method method = null;
        try {
            method = clazz.getDeclaredMethod(name, parameterTypes);
            return method;  // clazz.getDeclaredMethod 返回值不为空
        } catch (Exception e) {
            log.warn("No Method: {}.{}", clazz.getName(), name);
        }
        Class sc = clazz.getSuperclass();
        if (null != sc) {
            method = getDeclaredMethod(sc, name, parameterTypes);
        }
        return method;
    }

    // 比较2个方法的入参是否一致
    public static boolean isMethodSignatureSame(Method method1, Method method2) {
        if (method1 == null || method2 == null) {
            return false;
        }

        // 获取两个方法的参数列表
        Class<?>[] paramTypes1 = method1.getParameterTypes();
        Class<?>[] paramTypes2 = method2.getParameterTypes();

        // 比较参数个数
        if (paramTypes1.length != paramTypes2.length) {
            return false;
        }

        // 逐个比较参数类型
        for (int i = 0; i < paramTypes1.length; i++) {
            if (!paramTypes1[i].equals(paramTypes2[i])) {
                return false;
            }
        }

        return true;
    }

    // 查询带注解的方法
    public static List<Method> getDeclaredMethodsWithAnnotation(Class clazz, Class<? extends Annotation> annotationClass,
                                                                boolean withSuper) {
        List<Method> ret = new LinkedList<>();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method m : methods) {
            if (null != m.getAnnotation(annotationClass)) {
                ret.add(m);
            }
        }
        if (withSuper) {
            Class sc = clazz.getSuperclass();
            if (null != sc) {
                List<Method> superMethods = getDeclaredMethodsWithAnnotation(sc, annotationClass, true);
                // override method - 判断父类的方法是否被子类中的某个方法覆盖了
                for (Method sm : superMethods) {
                    if (isInMethods(sm, methods)) continue;
                    ret.add(sm);
                }
            }
        }
        return ret;
    }

    /**
     * 判断Method是否在另一组Method中有相同的定义
     * 注意：这个方法没有考虑Method所属的类
     */
    public static boolean isInMethods(Method m, Method[] methods) {
        for (Method child : methods) {
            if ((child.getName().equals(m.getName()) && child.getReturnType().equals(m.getReturnType()))
                    && Arrays.equals(child.getParameterTypes(), m.getParameterTypes())) {
                return true;
            }
        }
        return false;
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

    // 读取对象属性的值
    public static Object getFieldValue(Object obj, Field f) {
        try {
            boolean flag = f.isAccessible();
            f.setAccessible(true);
            Object v = f.get(obj);
            f.setAccessible(flag);
            return v;
        } catch (Exception e) {
            log.error(obj.getClass().getName() + "." + f.getName(), e);
        }
        return null;
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
            String setter = "set" + NameStringUtil.toClassName(fieldName);
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
