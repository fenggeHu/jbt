package utils;

import jbt.model.Bar;
import org.junit.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author max.hu  @date 2024/11/23
 **/
public class ClassUtilTests {

    @Test
    public void testClass(){
        List<Bar> list = new LinkedList<>();
        Class<?> clazz = getElementType(list);
        System.out.println(clazz);
    }


    private <T> Class<?> getElementType(Collection<T> lines) {
        ParameterizedType type = (ParameterizedType) lines.getClass().getGenericSuperclass();
        Type[] typeArguments = type.getActualTypeArguments();
        for (Type arg : typeArguments) {
            System.out.println("Generic type: " + arg);
        }
        return (Class<?>) type.getActualTypeArguments()[0];
    }
}
